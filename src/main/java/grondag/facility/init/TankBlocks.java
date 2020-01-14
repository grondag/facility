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
package grondag.facility.init;

import static grondag.facility.Facility.REG;

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import grondag.facility.storage.bulk.TankBlock;
import grondag.facility.storage.bulk.TankBlockEntity;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.SimpleTank;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.CubeWithFace;
import grondag.xm.api.texture.XmTextures;

@SuppressWarnings("unchecked")
public enum TankBlocks {
	;

	public static final TankBlock TANK = REG.block("tank", new TankBlock(FabricBlockSettings.of(Material.METAL).strength(1, 1).build(), TankBlocks::tankBe, false));
	public static final BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY_TYPE = REG.blockEntityType("tank", TankBlocks::tankBe, TANK);
	private static TankBlockEntity tankBe() {
		return new TankBlockEntity(TANK_BLOCK_ENTITY_TYPE, () -> new SimpleTank(Fraction.of(32)).filter(CrateBlocks.FILTER_NESTING), "TANK");
	}

	static {
		CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(TANK);
		Store.STORAGE_COMPONENT.addProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getEffectiveStorage(), TANK);
		Store.INTERNAL_STORAGE_COMPONENT.addProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getInternalStorage(), TANK);

		final XmPaint basePaint = XmPaint.finder()
				.textureDepth(2)
				.texture(0, XmTextures.TILE_NOISE_SUBTLE)
				.textureColor(0, 0xFF404045)
				.texture(1, XmTextures.BORDER_WEATHERED_LINE)
				.blendMode(1, PaintBlendMode.TRANSLUCENT)
				.textureColor(1, 0xA0000000)
				.find();

		XmBlockRegistry.addBlockStates(TANK, bs -> PrimitiveStateFunction.builder()
				.withJoin(TankBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint), bs), bs))
				.build());
	}
}
