package clashsoft.brewingapi.lib;

import clashsoft.brewingapi.entity.EntityPotion2;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviorPotionProjectile extends BehaviorProjectileDispense
{
	public final ItemStack					stack;
	
	public final DispenserBehaviorPotion2	dispenserPotion;
	
	public DispenserBehaviorPotionProjectile(DispenserBehaviorPotion2 dispenserPotion2, ItemStack stack)
	{
		this.dispenserPotion = dispenserPotion2;
		this.stack = stack;
	}
	
	@Override
	protected IProjectile getProjectileEntity(World world, IPosition position)
	{
		return new EntityPotion2(world, position.getX(), position.getY(), position.getZ(), this.stack.copy());
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
