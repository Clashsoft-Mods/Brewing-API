package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.attribute.IPotionAttribute;
import clashsoft.brewingapi.potion.base.IPotionBase;
import clashsoft.brewingapi.potion.recipe.PotionRecipe;
import clashsoft.brewingapi.potion.recipe.PotionRecipes;
import clashsoft.cslib.logging.CSLog;
import clashsoft.cslib.minecraft.lang.I18n;
import clashsoft.cslib.util.CSString;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StringUtils;

/**
 * @author Clashsoft
 */
public abstract class AbstractPotionType implements IPotionType
{
	@Override
	public String getUUID()
	{
		return this.getEffectName();
	}
	
	@Override
	public AbstractPotionType register()
	{
		int potionID = this.getPotionID();
		if (potionID != -1)
		{
			if (potionTypes.containsKey(potionID))
			{
				CSLog.warning("Registering duplicate potion ID " + potionID);
			}
			potionTypes.put(potionID, this);
		}
		
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
		return true;
	}
	
	@Override
	public boolean hasEffect()
	{
		return this.getPotionID() > 0;
	}
	
	@Override
	public Potion getPotion()
	{
		PotionEffect pe = this.getEffect();
		return pe == null ? null : Potion.potionTypes[pe.getPotionID()];
	}
	
	@Override
	public int getPotionID()
	{
		PotionEffect pe = this.getEffect();
		return pe == null ? -1 : pe.getPotionID();
	}
	
	@Override
	public int getLiquidColor()
	{
		Potion potion = this.getPotion();
		return potion == null ? 0x0C0CFF : potion.getLiquidColor();
	}
	
	@Override
	public int getDuration()
	{
		PotionEffect pe = this.getEffect();
		return pe == null ? -1 : pe.getDuration();
	}
	
	@Override
	public boolean isInstant()
	{
		Potion potion = this.getPotion();
		return potion != null && potion.isInstant();
	}
	
	@Override
	public int getAmplifier()
	{
		PotionEffect pe = this.getEffect();
		return pe == null ? -1 : pe.getAmplifier();
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
		PotionEffect effect = this.getEffect();
		return effect == null ? "" : effect.getEffectName();
	}
	
	@Override
	public StringBuilder getDisplayName()
	{
		String name = this.getEffectName();
		StringBuilder builder = new StringBuilder(I18n.getString(name));
		int amplifier = this.getAmplifier();
		int duration = this.getDuration();
		
		if (amplifier > 0)
		{
			builder.append(" ").append(CSString.convertToRoman(amplifier + 1));
		}
		if (duration > 20)
		{
			builder.append(" (").append(duration >= 1000000 ? I18n.getString("potion.infinite") : StringUtils.ticksToElapsedTime(duration)).append(")");
		}
		
		return builder;
	}
	
	@Override
	public ItemStack getIngredient()
	{
		PotionRecipe recipe = PotionRecipes.get(this);
		return recipe == null ? null : recipe.getInput();
	}
	
	@Override
	public IPotionBase getBase()
	{
		PotionRecipe recipe = PotionRecipes.get(this);
		return recipe == null ? null : recipe.getBase();
	}
	
	@Override
	public boolean isAmplifiable()
	{
		PotionEffect effect = this.getEffect();
		return effect != null && effect.getAmplifier() < this.getMaxAmplifier();
	}
	
	@Override
	public boolean isExtendable()
	{
		PotionEffect effect = this.getEffect();
		return effect != null && effect.getDuration() < this.getMaxDuration();
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
	
	public static PotionEffect improve(PotionEffect effect)
	{
		return new PotionEffect(effect.getPotionID(), (int) (effect.getDuration() * 2F / 3F), effect.getAmplifier() + 1);
	}
	
	public static PotionEffect extend(PotionEffect effect)
	{
		return new PotionEffect(effect.getPotionID(), effect.getDuration() * 2, effect.getAmplifier());
	}
	
	public static PotionEffect dilute(PotionEffect effect)
	{
		return new PotionEffect(effect.getPotionID(), (int) (effect.getDuration() * 2F / 3F), (int) (effect.getAmplifier() * 0.8F));
	}
	
	public static PotionEffect useGunpowder(PotionEffect effect)
	{
		return new PotionEffect(effect.getPotionID(), (int) (effect.getDuration() * 0.75F), effect.getAmplifier());
	}
	
	public static PotionEffect invert(PotionEffect effect, PotionEffect inverted)
	{
		if (effect != null)
		{
			return new PotionEffect(inverted.getPotionID(), (int) (effect.getDuration() * 0.75F), effect.getAmplifier());
		}
		else
		{
			return inverted;
		}
	}
	
	@Override
	public void setAttributes(List<IPotionAttribute> attributes)
	{
	}
	
	@Override
	public List<IPotionAttribute> getAttributes()
	{
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public boolean hasAttributes()
	{
		return false;
	}
	
	@Override
	public void addAttribute(IPotionAttribute attribute)
	{
	}
	
	@Override
	public boolean hasAttribute(IPotionAttribute attribute)
	{
		return false;
	}
	
	@Override
	public List<IPotionType> getSubTypes()
	{
		List<IPotionType> list = new ArrayList();
		list.add(this);
		if (this.isAmplifiable())
		{
			list.add(this.onAmplified());
		}
		if (this.isExtendable())
		{
			list.add(this.onExtended());
		}
		return list;
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		if (potion.stackTagCompound == null)
		{
			potion.stackTagCompound = new NBTTagCompound();
		}
		
		NBTTagList list = (NBTTagList) potion.stackTagCompound.getTag(COMPOUND_NAME);
		if (list == null)
		{
			list = new NBTTagList();
			potion.stackTagCompound.setTag(COMPOUND_NAME, list);
		}
		
		NBTTagCompound nbt1 = new NBTTagCompound();
		this.writeToNBT(nbt1);
		list.appendTag(nbt1);
		
		return potion;
	}
	
	@Override
	public ItemStack apply(PotionTypeList potionTypes)
	{
		potionTypes.add(this);
		
		Iterator<IPotionType> iterator = potionTypes.iterator();
		IPotionBase base = this.getBase();
		while (iterator.hasNext())
		{
			IPotionType type = iterator.next();
			if (type == base)
			{
				iterator.remove();
				continue;
			}
			else if (type instanceof IPotionBase)
			{
				((IPotionBase) type).onApplied(this, potionTypes);
			}
		}
		
		return potionTypes.getPotion();
	}
	
	@Override
	public ItemStack remove(ItemStack potion)
	{
		if (potion.stackTagCompound == null)
		{
			return potion;
		}
		
		NBTTagList list = (NBTTagList) potion.stackTagCompound.getTag(COMPOUND_NAME);
		if (list == null)
		{
			return potion;
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		for (int i = 0; i < list.tagCount(); i++)
		{
			if (nbt.equals(list.getCompoundTagAt(i)))
			{
				list.removeTag(i);
				break;
			}
		}
		
		return potion;
	}
	
	@Override
	public ItemStack remove(PotionTypeList potionTypes)
	{
		potionTypes.remove(this);
		
		return potionTypes.getPotion();
	}
	
	@Override
	public void apply(EntityLivingBase target)
	{
		PotionEffect pe = new PotionEffect(this.getEffect());
		this.apply_do(target, pe);
	}
	
	@Override
	public void apply(EntityLivingBase thrower, EntityLivingBase target, double distance)
	{
		PotionEffect effect = this.getEffect();
		if (effect != null)
		{
			int id = effect.getPotionID();
			
			if (id == Potion.heal.id || id == Potion.harm.id)
			{
				Potion.potionTypes[id].affectEntity(thrower, target, effect.getAmplifier(), distance);
			}
			else
			{
				int j = (int) (distance * effect.getDuration() + 0.5D);
				
				if (j > 20)
				{
					this.apply_do(target, new PotionEffect(id, j, effect.getAmplifier()));
				}
			}
		}
	}
	
	@Override
	public int compareTo(IPotionType o)
	{
		return this.hasEffect() && o.hasEffect() ? Integer.compare(this.getPotionID(), o.getPotionID()) : 0;
	}
}
