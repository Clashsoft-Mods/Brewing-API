package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.potion.attribute.IPotionAttribute;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;

public class PotionTypeDelegate extends AbstractPotionType
{
	private PotionEffect			effect;
	private IPotionType				thePotionType;
	private List<IPotionAttribute>	attributes	= new ArrayList();
	
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
		PotionTypeDelegate delegate = new PotionTypeDelegate(this.effect, this.thePotionType);
		delegate.attributes = this.attributes;
		return delegate;
	}
	
	@Override
	public boolean isBadEffect()
	{
		return this.thePotionType.isBadEffect();
	}
	
	@Override
	public PotionEffect getEffect()
	{
		if (this.hasAttributes())
		{
			PotionEffect effect = this.getEffect();
			for (IPotionAttribute attribute : this.getAttributes())
			{
				effect = attribute.getModdedEffect(this, effect);
			}
			return effect;
		}
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
	public void setAttributes(List<IPotionAttribute> attributes)
	{
		this.attributes = attributes;
	}
	
	@Override
	public List<IPotionAttribute> getAttributes()
	{
		return this.attributes;
	}
	
	@Override
	public boolean hasAttributes()
	{
		return this.attributes != null && !this.attributes.isEmpty();
	}
	
	@Override
	public void addAttribute(IPotionAttribute attribute)
	{
		this.attributes.add(attribute);
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
		
		if (this.hasAttributes())
		{
			NBTTagList attributes = new NBTTagList();
			for (IPotionAttribute attribute : this.attributes)
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("Name", attribute.getName());
				attribute.writeToNBT(nbt);
			}
		}
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
		
		if (nbt.hasKey("Attributes"))
		{
			NBTTagList attributes = (NBTTagList) nbt.getTag("Attributes");
			for (int i = 0; i < attributes.tagCount(); i++)
			{
				NBTTagCompound compound = attributes.getCompoundTagAt(i);
				String name = compound.getString("Name");
				IPotionAttribute attribute = IPotionAttribute.attributes.get(name);
				IPotionAttribute attribute2 = attribute.copy();
				
				if (attribute != attribute2)
				{
					// the attribute is not a singleton
					attribute2.readFromNBT(nbt);
					this.addAttribute(attribute2);
				}
				else
				{
					this.addAttribute(attribute);
				}
			}
		}
	}
}
