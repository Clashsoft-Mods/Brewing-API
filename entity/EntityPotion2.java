package clashsoft.brewingapi.entity;

import java.util.Iterator;
import java.util.List;
import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.brewing.Brewing;
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
	/**
	 * The damage value of the thrown potion that this EntityPotion represents.
	 */
	public ItemStack potionDamage;
	public int color;

	public EntityPotion2(World par1World)
	{
		super(par1World);
	}

	public EntityPotion2(World par1World, EntityLivingBase par2EntityLiving, ItemStack par3ItemStack)
	{
		super(par1World, par2EntityLiving, par3ItemStack);
		this.potionDamage = par3ItemStack;
		this.color = ((ItemPotion2)par3ItemStack.getItem()).getColorFromItemStack(par3ItemStack, 0);
	}

	public EntityPotion2(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
	{
		super(par1World, par2, par4, par6, par8ItemStack);
		this.potionDamage = par8ItemStack;
		this.color = ((ItemPotion2)par8ItemStack.getItem()).getColorFromItemStack(par8ItemStack, 0);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	protected float getGravityVelocity()
	{
		return 0.05F;
	}

	protected float func_70182_d()
	{
		return 0.5F;
	}

	protected float func_70183_g()
	{
		return -20.0F;
	}

	public void setPotionDamage(ItemStack par1)
	{
		this.potionDamage = par1;
	}

	/**
	 * Returns the damage value of the thrown potion that this EntityPotion represents.
	 */
	public ItemStack getPotion()
	{
		return potionDamage;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
	{
		if (!this.worldObj.isRemote)
		{
			List list = BrewingAPI.potion2.getEffects(this.potionDamage);

			if (list != null && !list.isEmpty())
			{
				AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
				List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

				if (list1 != null && !list1.isEmpty())
				{
					Iterator iterator = list1.iterator();

					while (iterator.hasNext())
					{
						EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();
						double d0 = this.getDistanceSqToEntity(entitylivingbase);

						if (d0 < 16.0D)
						{
							double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

							if (entitylivingbase == par1MovingObjectPosition.entityHit)
							{
								d1 = 1.0D;
							}

							Iterator iterator1 = list.iterator();

							while (iterator1.hasNext())
							{
								PotionEffect potioneffect = ((Brewing)iterator1.next()).getEffect();
								if (potioneffect != null)
								{
									int i = potioneffect.getPotionID();

									if (Potion.potionTypes[i].isInstant())
									{
										Potion.potionTypes[i].affectEntity(this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
									}
									else
									{
										int j = (int)(d1 * (double)potioneffect.getDuration() + 0.5D);

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

			this.playSplashEffect((int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), this.getPotion());
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.glass", 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			this.setDead();
		}
	}

	private void playSplashEffect(int par1, int par2, int par3, ItemStack par4ItemStack)
	{
		BrewingAPI.proxy.playSplashEffect(this.worldObj, par1, par2, par3, par4ItemStack);
		this.worldObj.playSound((double)par1 + 0.5D, (double)par2 + 0.5D, (double)par3 + 0.5D, "random.glass", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F, false);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("Potion"))
		{
			this.potionDamage = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("Potion"));
		}

		if (this.potionDamage == null)
		{
			this.setDead();
		}

		if (par1NBTTagCompound.hasKey("Color"))
		{
			color = par1NBTTagCompound.getInteger("Color");
		}
		else
		{
			color = 0x0F0F0F;
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);

		if (this.potionDamage != null)
		{
			par1NBTTagCompound.setCompoundTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
		}
		par1NBTTagCompound.setInteger("Color", color);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
	}
}
