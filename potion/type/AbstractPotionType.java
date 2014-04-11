package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class AbstractPotionType implements IPotionType
{
	@Override
	public AbstractPotionType register()
	{
		potionTypeList.add(this);
		
		if (this.isCombinable())
		{
			combinableTypes.add(this);
		}
		if (this.hasEffect())
		{
			effectTypes.add(this);
		}
		
		return this;
	}
	
	@Override
	public boolean isCombinable()
	{
		return !this.isBase();
	}
	
	@Override
	public boolean hasEffect()
	{
		return this.getEffect() != null && this.getPotionID() > 0;
	}
	
	@Override
	public Potion getPotion()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? Potion.potionTypes[pe.getPotionID()] : null;
	}
	
	@Override
	public int getPotionID()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getPotionID() : -1;
	}
	
	@Override
	public int getDuration()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getDuration() : -1;
	}
	
	@Override
	public int getAmplifier()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getAmplifier() : -1;
	}
	
	@Override
	public int getRedstoneAmount()
	{
		int duration = this.getDuration();
		int defaultDuration = this.getDefaultDuration();
		int redstoneAmount = 0;
		if (duration > 0)
		{
			while (duration > defaultDuration)
			{
				redstoneAmount++;
				duration /= 2;
			}
		}
		return redstoneAmount;
	}
	
	@Override
	public int getGlowstoneAmount()
	{
		return this.getAmplifier();
	}
	
	@Override
	public String getEffectName()
	{
		return this.hasEffect() ? this.getEffect().getEffectName() : "";
	}
	
	@Override
	public boolean isImprovable()
	{
		return this.hasEffect() && this.getAmplifier() < this.getMaxAmplifier();
	}
	
	@Override
	public boolean isExtendable()
	{
		return this.hasEffect() && this.getDuration() < this.getMaxDuration();
	}
	
	@Override
	public boolean isDilutable()
	{
		return this.hasEffect();
	}
	
	@Override
	public boolean isInversible()
	{
		return this.getInverted() != null;
	}
	
	@Override
	public int getLiquidColor()
	{
		Potion potion = this.getPotion();
		if (potion != null)
		{
			return potion.getLiquidColor();
		}
		return 0x0C0CFF;
	}
	
	@Override
	public List<IPotionType> getSubTypes()
	{
		List<IPotionType> list = new ArrayList();
		list.add(this);
		if (this.isImprovable())
			list.add(this.onImproved());
		if (this.isExtendable())
			list.add(this.onExtended());
		return list;
	}
	
	@Override
	public ItemStack apply(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.stackTagCompound == null)
			{
				stack.setTagCompound(new NBTTagCompound());
			}
			
			NBTTagList list = stack.stackTagCompound.getTagList(COMPOUND_NAME, 11);
			
			NBTTagCompound nbt1 = new NBTTagCompound();
			this.writeToNBT(nbt1);
			list.appendTag(nbt1);
			
			stack.stackTagCompound.setTag(COMPOUND_NAME, list);
		}
		return stack;
	}
	
	@Override
	public ItemStack remove(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = stack.stackTagCompound.getTagList(COMPOUND_NAME, 11);
			
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound nbt1 = list.getCompoundTagAt(i);
				if (this.equals(PotionType.getFromNBT(nbt1)))
				{
					list.removeTag(i);
					break;
				}
			}
			
			stack.stackTagCompound.setTag(COMPOUND_NAME, list);
		}
		return stack;
	}
	
	@Override
	public int compareTo(IPotionType o)
	{
		return (this.hasEffect() && o.hasEffect()) ? Integer.compare(this.getPotionID(), o.getPotionID()) : 0;
	}
}
