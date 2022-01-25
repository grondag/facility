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

public class Storage2BusTickHandler implements TransportTickHandler {
	public static final Storage2BusTickHandler INSTANCE = new Storage2BusTickHandler();

	@Override
	public boolean tick(TransportContext context) {
		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.prepareForTick()) {
			return false;
		}

		final TransportCarrierContext carrierContext = context.carrierContext();

		// see if we have something to send
		final Article targetArticle = storageContext.proposeSupply(carrierContext.articleType);

		// storage did not have anything available, try again next time
		if (targetArticle.isNothing()) {
			return true;
		}

		final long units = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.available(targetArticle, units);

		if (howMany > 0) {
			// find out how many buffer can hold
			final ArticleFunction bufferConsumer = context.buffer().consumer();
			howMany = bufferConsumer.apply(targetArticle, howMany, units, true);

			if (howMany > 0) {
				try (Transaction tx = Transaction.open()) {
					tx.enlist(bufferConsumer);
					final long bufferResult = bufferConsumer.apply(targetArticle, howMany, units, false);

					final long storageResult = storageContext.supply(targetArticle, howMany, units);

					if (storageResult == bufferResult) {
						tx.commit();
					} else {
						assert storageResult == 0;
					}
				}
			}
		}

		return true;
	}
}
