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

package grondag.facility;

import java.util.function.BiFunction;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.hooks.block.BlockEntityHooks;
import dev.architectury.hooks.block.BlockEntityHooks.Constructor;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import io.vram.modkeys.api.ModKey;

import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.MenuTypes;
import grondag.facility.init.PipeBlocks;
import grondag.facility.init.TankBlocks;
import grondag.facility.init.Textures;
import grondag.facility.packet.BinActionC2S;
import grondag.facility.varia.Base32Namer;
import grondag.facility.varia.WorldTaskManager;
import grondag.fluidity.impl.article.ArticleTypeRegistryImpl;

public abstract class Facility {
	private Facility() { }

	public static final Logger LOG = LogManager.getLogger("Facility");
	public static final String MODID = "facility";
	public static final ResourceLocation FORCE_KEY_NAME = id("force");
	public static final ResourceLocation MODIFY_KEY_NAME = id("modify");

	public static ModKey forceKey;
	public static ModKey modifyKey;

	private static CreativeModeTab itemGroup;
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MODID, Registry.ITEM_REGISTRY);
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MODID, Registry.BLOCK_REGISTRY);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(MODID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(MODID, Registry.MENU_REGISTRY);

	public static final Material CRATE_MATERIAL = (new Material.Builder(MaterialColor.WOOD)).build();
	public static TagKey<Item> STORAGE_BLACKLIST_WITH_CONTENT = TagKey.create(Registry.ITEM_REGISTRY, id("storage_blacklist_with_content"));
	public static TagKey<Item> STORAGE_BLACKLIST_ALWAYS = TagKey.create(Registry.ITEM_REGISTRY, id("storage_blacklist_always"));

	public static void initialize() {
		itemGroup = CreativeTabRegistry.create(id("group"), () -> new ItemStack(Registry.ITEM.get(id("hyper_crate"))));
		forceKey = ModKey.getOrCreate(FORCE_KEY_NAME);
		modifyKey = ModKey.getOrCreate(MODIFY_KEY_NAME);

		FacilityConfig.initialize();
		MenuTypes.initialize();
		Textures.initialize();
		CrateBlocks.initialize();
		BinBlocks.initialize();
		PipeBlocks.initialize();
		TankBlocks.initialize();
		ArticleTypeRegistryImpl.initialize();
		NetworkManager.registerReceiver(NetworkManager.c2s(), BinActionC2S.ID, BinActionC2S::accept);

		ITEMS.register();
		BLOCKS.register();
		BLOCK_ENTITY_TYPES.register();
		MENU_TYPES.register();

		TickEvent.SERVER_POST.register(s -> WorldTaskManager.doServerTick());

		LifecycleEvent.SERVER_BEFORE_START.register(s -> {
			Base32Namer.loadBadNams(s.getResourceManager(), id("names/offensive.json"));
		});
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MODID, name);
	}

	public static Item.Properties itemSettings() {
		return new Item.Properties().tab(itemGroup);
	}

	public static <T extends Block> T block(String name, T block, Item.Properties settings) {
		return block(name, block, new BlockItem(block, settings));
	}

	public static <T extends Block> T block(String name, T block) {
		return block(name, block, itemSettings());
	}

	public static <T extends Block> T blockNoItem(String name, T block) {
		BLOCKS.register(id(name), () -> block);
		return block;
	}

	public static <T extends Block> T block(String name, T block, BlockItem item) {
		BLOCKS.register(id(name), () -> block);

		if (item != null) {
			final BlockItem bi = item(name, item);
			bi.registerBlocks(BlockItem.BY_BLOCK, bi);
		}

		return block;
	}

	public static <T extends Block> T block(String name, T block, BiFunction<T, Item.Properties, BlockItem> itemFactory) {
		return block(name, block, itemFactory.apply(block, itemSettings()));
	}

	public static <T extends Item> T item(String name, T item) {
		ITEMS.register(id(name), () -> item);
		return item;
	}

	public static <T extends BlockItem> T blockItem(String name, T item) {
		ITEMS.register(id(name), () -> item);
		item.registerBlocks(Item.BY_BLOCK, item);
		return item;
	}

	public static <T extends BlockEntity> BlockEntityType<T> blockEntityType(String id, Constructor<T> constructor, Block... blocks) {
		final BlockEntityType<T> type = BlockEntityHooks.builder(constructor, blocks).build(null);
		BLOCK_ENTITY_TYPES.register(id(id), () -> type);
		return type;
	}

	public static <T extends AbstractContainerMenu> MenuType<T> menuType(ResourceLocation id, MenuType<T> menuType) {
		MENU_TYPES.register(id, () -> menuType);
		return menuType;
	}
}
