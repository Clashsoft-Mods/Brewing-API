package clashsoft.brewingapi.lib;

import clashsoft.brewingapi.entity.EntityPotion2;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class DispenserBehaviorPotionProjectile extends BehaviorProjectileDispense
{
	final ItemStack					field_96462_b;
	
	final DispenserBehaviorPotion2	field_96463_c;
	
	DispenserBehaviorPotionProjectile(DispenserBehaviorPotion2 par1DispenserBehaviorPotion, ItemStack par2ItemStack)
	{
		this.field_96463_c = par1DispenserBehaviorPotion;
		this.field_96462_b = par2ItemStack;
	}
	
	/**
	 * Return the projectile entity spawned by this dispense behavior.
	 */
	@Override
	protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition)
	{
		return new EntityPotion2(par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ(), this.field_96462_b.copy());
	}
	
	@Override
	protected float func_82498_a()
	{
		return super.func_82498_a() * 0.5F;
	}
	
	@Override
	protected float func_82500_b()
	{
		return super.func_82500_b() * 1.25F;
	}
}
