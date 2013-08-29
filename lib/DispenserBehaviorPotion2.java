package clashsoft.brewingapi.lib;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;

public final class DispenserBehaviorPotion2 implements IBehaviorDispenseItem
{
	public final BehaviorDefaultDispenseItem	field_96458_b	= new BehaviorDefaultDispenseItem();
	
	public DispenserBehaviorPotion2()
	{
	}
	
	/**
	 * Dispenses the specified ItemStack from a dispenser.
	 */
	@Override
	public ItemStack dispense(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
	{
		return par2ItemStack.getItem() instanceof ItemPotion2 && ((ItemPotion2) par2ItemStack.getItem()).isSplash(par2ItemStack.getItemDamage()) ? (new DispenserBehaviorPotionProjectile(this, par2ItemStack)).dispense(par1IBlockSource, par2ItemStack) : this.field_96458_b.dispense(par1IBlockSource, par2ItemStack);
	}
}
