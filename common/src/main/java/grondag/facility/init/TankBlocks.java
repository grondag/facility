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

package grondag.facility.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import grondag.facility.Facility;
import grondag.facility.storage.PortableStore;
import grondag.facility.storage.bulk.PortableTankItem;
import grondag.facility.storage.bulk.TankBlock;
import grondag.facility.storage.bulk.TankBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.SimpleTank;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.CubeWithFace;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.api.texture.core.CoreTextures;

@SuppressWarnings("unchecked")
public abstract class TankBlocks {
	private TankBlocks() { }

	private static BlockEntityType<TankBlockEntity> tankBlockEntityType;

	public static BlockEntityType<TankBlockEntity> tankBlockEntityType() {
		return tankBlockEntityType;
	}

	private static TankBlockEntity tankBe(BlockPos pos, BlockState state) {
		return new TankBlockEntity(tankBlockEntityType, pos, state, () -> new SimpleTank(Fraction.of(32)).filter(ArticleType.FLUID), "TANK ");
	}

	private static PortableTankItem portableTankItem;

	public static PortableTankItem portableTankItem() {
		assert portableTankItem != null;
		return portableTankItem;
	}

	public static void initialize() {
		final var tankBlock = Facility.blockNoItem("tank", new TankBlock(Block.Properties.of(Material.METAL).strength(1, 1), TankBlocks::tankBe, false));
		tankBlockEntityType = Facility.blockEntityType("tank", TankBlocks::tankBe, tankBlock);
		portableTankItem = Facility.blockItem("tank", new PortableTankItem(tankBlock, Facility.itemSettings().stacksTo(1).durability(32768)));

		//CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(TANK);
		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getEffectiveStorage(), tankBlock);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getInternalStorage(), tankBlock);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getEffectiveStorage().getConsumer(), tankBlock);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((TankBlockEntity) ctx.blockEntity()).getEffectiveStorage().getSupplier(), tankBlock);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> new PortableStore(new SimpleTank(Fraction.of(32)), ctx), portableTankItem);

		final XmPaint basePaint = XmPaint.finder()
				.textureDepth(2)
				.texture(0, XmTextures.TILE_NOISE_SUBTLE)
				.textureColor(0, 0xFF404045)
				.texture(1, CoreTextures.BORDER_WEATHERED_LINE)
				.textureColor(1, 0xA0000000)
				.find();

		XmBlockRegistry.addBlockStates(tankBlock, bs -> PrimitiveStateFunction.builder()
				.withJoin(TankBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint), bs), bs))
				.build());
	}
}
