/*******************************************************************************
 * Copyright 2019 grondag
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

package grondag.smart_chest;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SmartChest implements ModInitializer {
    public static final Logger LOG = LogManager.getLogger("SmartChest2K");
    public static final String MODID = "smart_chest";
    
    public static final BlockEntityType<SmartChestBlockEntity> SMART_CHEST_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(SmartChestBlockEntity::new).build(null);
    public static final SmartChestBlock SMART_CHEST_BLOCK = new SmartChestBlock();
 
    @Override
    public void onInitialize() {
        initTextures();
        
        register(SMART_CHEST_BLOCK, MODID, ITEM_FUNCTION_STANDARD);
        
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, "be_test"), SMART_CHEST_BLOCK_ENTITY_TYPE);
        
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> ((modelId, context) -> {
            return modelId.getNamespace().equals(MODID) ? new SimpleUnbakedModel(new SmartChestModel()) : null;
        }));
    }

   
    private static void initTextures() {
        ClientSpriteRegistryCallback.EVENT.register((atlas, registry) -> {
            if(atlas == MinecraftClient.getInstance().getSpriteAtlas()) {
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_0"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_1"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_2"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_3"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_4"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_5"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_6"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_0_7"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_1_0"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_1_1"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_1_2"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_1_3"));
                registry.register(new Identifier(MODID, "block/border_filmstrip_1_4"));
                registry.register(new Identifier(MODID, "block/noise_moderate_0_0"));
                registry.register(new Identifier(MODID, "block/noise_moderate_0_1"));
                registry.register(new Identifier(MODID, "block/noise_moderate_0_2"));
                registry.register(new Identifier(MODID, "block/noise_moderate_0_3"));
                registry.register(new Identifier(MODID, "block/symbol_chest"));
            }
        });
    }
    
    private static Item register(Block block, String name, Function<Block, Item> itemFunc) {
        Identifier id = new Identifier(MODID, name);
        Registry.BLOCK.add(id, block);
        Item result = itemFunc.apply(block);
        Registry.ITEM.add(id, result);
        return result;
    }
    
    private static final Function<Block, Item> ITEM_FUNCTION_STANDARD = block -> {
        return new BlockItem(block, new Item.Settings()
                .stackSize(64)
                .itemGroup(ItemGroup.MISC));
    };
}
