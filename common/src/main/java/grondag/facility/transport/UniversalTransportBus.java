/*******************************************************************************
 * Copyright 2019, 2020 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.transport;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.wip.api.transport.CarrierType;

public class UniversalTransportBus implements CarrierType {
	protected static final Set<ArticleType<?>> articleTypes = ImmutableSet.of(ArticleType.ITEM, ArticleType.FLUID);

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return articleTypes;
	}

	public static UniversalTransportBus BASIC = new UniversalTransportBus();

	public static Set<CarrierType> SET_OF_BASIC = ImmutableSet.of(BASIC);
}
