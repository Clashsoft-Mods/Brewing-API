package clashsoft.brewingapi.potion.type;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

public class PotionTypeDelegate extends AbstractPotionType
{
	private PotionEffect	effect;
	private IPotionType		thePotionType;
	
	public PotionTypeDelegate()
	{
	}
	
	public PotionTypeDelegate(PotionEffect effect)
	{
		this.setEffect(effect);
	}
	
	public PotionTypeDelegate(PotionEffect effect, IPotionType potionType)
	{
		this.effect = effect;
		this.thePotionType = potionType;
	}
	
	public void setEffect(PotionEffect effect)
	{
		this.effect = effect;
		this.thePotionType = PotionType.getFromEffect_(effect);
	}
	
	@Override
	public String getUUID()
	{
		return this.getEffectName();
	}
	
	@Override
	public IPotionType copy()
	{
		return new PotionTypeDelegate(this.effect, this.thePotionType);
	}
	
	@Override
	public boolean isBadEffect()
	{
		return this.thePotionType.isBadEffect();
	}
	
	@Override
	public PotionEffect getEffect()
	{
		return this.effect;
	}
	
	@Override
	public int getMaxAmplifier()
	{
		return this.thePotionType.getMaxAmplifier();
	}
	
	@Override
	public int getMaxDuration()
	{
		return this.thePotionType.getMaxDuration();
	}
	
	@Override
	public int getDefaultDuration()
	{
		return this.thePotionType.getDuration();
	}
	
	@Override
	public IPotionType getInverted()
	{
		return this.thePotionType.getInverted();
	}
	
	@Override
	public ItemStack getIngredient()
	{
		ItemStack ingredient = this.thePotionType.getIngredient();
		return ingredient == null ? null : ingredient.copy();
	}
	
	@Override
	public PotionBase getBase()
	{
		return this.thePotionType.getBase();
	}
	
	@Override
	public boolean isBase()
	{
		return false;
	}
	
	@Override
	public IPotionType onImproved()
	{
		return this.thePotionType.onImproved();
	}
	
	@Override
	public IPotionType onExtended()
	{
		return this.thePotionType.onExtended();
	}
	
	@Override
	public IPotionType onDiluted()
	{
		return this.thePotionType.onDiluted();
	}
	
	@Override
	public IPotionType onGunpowderUsed()
	{
		return this.thePotionType.onGunpowderUsed();
	}
	
	@Override
	public IPotionType onInverted()
	{
		return this.thePotionType.onInverted();
	}
	
	@Override
	public void apply_do(EntityLivingBase target, PotionEffect effect)
	{
		this.thePotionType.apply_do(target, effect);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound effect = new NBTTagCompound();
		this.effect.writeCustomPotionEffectToNBT(effect);
		nbt.setTag("Effect", effect);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("Effect"))
		{
			NBTTagCompound effect = nbt.getCompoundTag("Effect");
			this.setEffect(PotionEffect.readCustomPotionEffectFromNBT(effect));
		}
		else
		{
			int id = nbt.getInteger("PotionID");
			int duration = nbt.getInteger("PotionDuration");
			int amplifier = nbt.getInteger("PotionAmplifier");
			
			this.effect = new PotionEffect(id, duration, amplifier);
		}
	}
}
