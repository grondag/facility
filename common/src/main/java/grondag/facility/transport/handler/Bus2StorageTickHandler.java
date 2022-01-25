/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.transport.handler;

import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.transact.Transaction;

public class Bus2StorageTickHandler implements TransportTickHandler {
	public static final Bus2StorageTickHandler INSTANCE = new Bus2StorageTickHandler();

	@Override
	public boolean tick(TransportContext context) {
		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.prepareForTick()) {
			return false;
		}

		final TransportCarrierContext carrierContext = context.carrierContext();

		if (!carrierContext.isReady()) {
			return true;
		}

		// see if we know what we want
		Article targetArticle = storageContext.proposeAccept(carrierContext.articleType);

		// if not accepting anything then exit
		if (targetArticle == null) {
			return true;
		}

		final boolean didStoragePropose;

		if (targetArticle.isNothing()) {
			didStoragePropose = false;
			// local storage has no preference, so find something random on network
			targetArticle = carrierContext.anyAvailableArticle();

			// if still nothing, then try again next time
			if (targetArticle.isNothing()) {
				return true;
			}

			// if can't accept, tell carrier to find something different next time and try again next tick
			if (!storageContext.canAccept(targetArticle)) {
				carrierContext.resetAvailableArticle();
				return true;
			}
		} else {
			didStoragePropose = true;
		}

		final ArticleFunction supplier = carrierContext.sourceFor(targetArticle);

		if (supplier == null) {
			if (didStoragePropose) {
				storageContext.advanceAcceptProposal(carrierContext.articleType);
			}

			return true;
		}

		final ArticleFunction bufferConsumer = context.buffer().consumer();
		final long units = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.capacityFor(targetArticle, units);
		howMany = bufferConsumer.apply(targetArticle, howMany, units, true);
		howMany = carrierContext.throttle(targetArticle, howMany, units, true);
		howMany = supplier.apply(targetArticle, howMany, units, true);

		if (howMany > 0) {
			try (Transaction tx = Transaction.open()) {
				tx.enlist(supplier);
				tx.enlist(bufferConsumer);

				howMany = carrierContext.throttle(targetArticle, howMany, units, false);
				howMany = supplier.apply(targetArticle, howMany, units, false);

				if (howMany > 0 && bufferConsumer.apply(targetArticle, howMany, units, false) == howMany) {
					tx.commit();
					carrierContext.resetCooldown();
				}
			}
		}

		return true;
	}
}
