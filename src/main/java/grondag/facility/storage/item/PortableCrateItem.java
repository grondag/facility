package grondag.facility.storage.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import grondag.facility.init.ScreenHandlers;
import grondag.facility.storage.PortableStore;
import grondag.fluidity.api.storage.Store;

public class PortableCrateItem extends BlockItem {
	public final PortableStore displayCrate;
	protected final Supplier<Store> storeFactory;

	public PortableCrateItem(Block block, Settings settings, Supplier<Store> storeFactory) {
		super(block, settings);
		displayCrate = new PortableStore(storeFactory.get());
		this.storeFactory = storeFactory;
	}

	public PortableStore makeStore(PlayerEntity player, Hand hand) {
		return new PortableStore(storeFactory.get(), () -> player.getStackInHand(hand), s -> player.setStackInHand(hand, s));
	}

	@Override
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		// NOOP
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		if(!ctx.getPlayer().isSneaking()) {
			if(use(ctx.getWorld(), ctx.getPlayer(), ctx.getHand()).getResult().isAccepted()) {
				return ActionResult.SUCCESS;
			}
		}

		return super.useOnBlock(ctx);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		final ItemStack itemStack = playerEntity.getStackInHand(hand);

		if(itemStack.getItem() != this) {
			return TypedActionResult.pass(itemStack);
		}

		if(itemStack.hasNbt()) {
			if (!world.isClient) {
				// TODO: get the label from BE tags, not currently displayed
				final String label = "todo";
				((ServerPlayerEntity) playerEntity).openHandledScreen(ScreenHandlers.crateItemFactory(label, hand));
			}

			return TypedActionResult.success(itemStack);
		}

		return TypedActionResult.pass(itemStack);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, world, list, tooltipContext);
		// TODO: localize
		displayCrate.readFromStack(itemStack);

		if(displayCrate.isEmpty()) {
			list.add(new LiteralText("Empty"));
		} else {
			list.add(new LiteralText(Long.toString(displayCrate.count()) + " of " + displayCrate.capacity()));
		}
	}
}
