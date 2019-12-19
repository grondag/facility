package grondag.smart_chest;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

public class SmartChestContainer extends Container {
	public static Identifier ID = SmartChest.REG.id("smart_chest");

	protected SmartChestContainer(PlayerInventory playerInventory, int synchId) {
		super(null, synchId);

		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				addSlot(new Slot(playerInventory, o + p * 9 + 9, o * 18, p * 18));
			}
		}

		for(int p = 0; p < 9; ++p) {
			addSlot(new Slot(playerInventory, p, p * 18, 58));
		}

	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return true;
	}
}
