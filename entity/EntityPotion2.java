package clashsoft.brewingapi.entity;

import java.util.Iterator;
import java.util.List;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion2 extends EntityPotion
{
	public EntityPotion2(World world)
	{
		super(world);
	}
	
	public EntityPotion2(World world, EntityLivingBase thrower, ItemStack stack)
	{
		super(world, thrower, stack);
		this.setPotion(stack);
	}
	
	public EntityPotion2(World world, double x, double y, double z, ItemStack stack)
	{
		super(world, x, y, z, stack);
		this.setPotion(stack);
	}
	
	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity()
	{
		return 0.05F;
	}
	
	@Override
	protected float func_70182_d()
	{
		return 0.5F;
	}
	
	@Override
	protected float func_70183_g()
	{
		return -20.0F;
	}
	
	public void setPotion(ItemStack stack)
	{
		this.getDataWatcher().updateObject(10, stack);
		this.getDataWatcher().setObjectWatched(10);
	}
	
	public ItemStack getPotion()
	{
		return this.getDataWatcher().getWatchableObjectItemStack(10);
	}
	
	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(MovingObjectPosition movingObjectPosition)
	{
		if (!this.worldObj.isRemote)
		{
			ItemStack potion = this.getPotion();
			List list = ((ItemPotion2) potion.getItem()).getEffects(potion);
			
			if (list != null && !list.isEmpty())
			{
				AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
				List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
				
				if (list1 != null && !list1.isEmpty())
				{
					Iterator iterator = list1.iterator();
					
					while (iterator.hasNext())
					{
						EntityLivingBase entitylivingbase = (EntityLivingBase) iterator.next();
						double d0 = this.getDistanceSqToEntity(entitylivingbase);
						
						if (d0 < 16.0D)
						{
							double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
							
							if (entitylivingbase == movingObjectPosition.entityHit)
							{
								d1 = 1.0D;
							}
							
							Iterator iterator1 = list.iterator();
							
							while (iterator1.hasNext())
							{
								PotionEffect potioneffect = ((PotionType) iterator1.next()).getEffect();
								if (potioneffect != null)
								{
									int i = potioneffect.getPotionID();
									
									if (Potion.potionTypes[i].isInstant())
									{
										Potion.potionTypes[i].affectEntity(this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
									}
									else
									{
										int j = (int) (d1 * potioneffect.getDuration() + 0.5D);
										
										if (j > 20)
										{
											entitylivingbase.addPotionEffect(new PotionEffect(i, j, potioneffect.getAmplifier()));
										}
									}
								}
							}
						}
					}
				}
			}
			
			BrewingAPI.proxy.playSplashEffect(this.worldObj, this.posX, this.posY, this.posZ, potion);
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.glass", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			this.setDead();
		}
	}
	
	@Override
	protected void entityInit()
	{
		this.getDataWatcher().addObjectByDataType(10, 5);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		if (this.getPotion() != null)
			nbt.setCompoundTag("Item", this.getPotion().writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("Item");
		this.setPotion(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		
		ItemStack item = this.getDataWatcher().getWatchableObjectItemStack(10);
		
		if (item == null || item.stackSize <= 0)
			this.setDead();
	}
}
