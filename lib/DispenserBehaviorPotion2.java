package clashsoft.brewingapi.lib;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;

public final class DispenserBehaviorPotion2 implements IBehaviorDispenseItem
{
	public final BehaviorDefaultDispenseItem	itemDispenser	= new BehaviorDefaultDispenseItem();
	
	public DispenserBehaviorPotion2()
	{
	}
	
	@Override
	public ItemStack dispense(IBlockSource source, ItemStack stack)
	{
		return stack.getItem() instanceof ItemPotion2 && ((ItemPotion2) stack.getItem()).isSplash(stack.getItemDamage()) ? (new DispenserBehaviorPotionProjectile(this, stack)).dispense(source, stack) : this.itemDispenser.dispense(source, stack);
	}
}
