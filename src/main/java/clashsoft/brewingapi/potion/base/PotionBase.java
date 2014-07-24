package clashsoft.brewingapi.potion.base;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.recipe.PotionRecipes;
import clashsoft.brewingapi.potion.type.AbstractPotionType;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

/**
 * @author Clashsoft
 */
public class PotionBase extends AbstractPotionType implements IPotionBase
{
	private String	name;
	
	public PotionBase()
	{
		super();
	}
	
	public PotionBase(String name)
	{
		this.name = name;
	}
	
	public PotionBase(String name, ItemStack ingredient)
	{
		this(name);
		
		PotionRecipes.addRecipe(ingredient, water, this);
	}
	
	@Override
	public String getUUID()
	{
		return this.name;
	}
	
	public static PotionBase getFromName(String name)
	{
		return (PotionBase) bases.get(name);
	}
	
	public static PotionBase getFromIngredient(ItemStack ingredient)
	{
		IPotionType potionType = PotionType.getFromIngredient(ingredient);
		if (potionType instanceof PotionBase)
		{
			return (PotionBase) potionType;
		}
		return null;
	}
	
	@Override
	public int getLiquidColor()
	{
		return 0x0C0CFF;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public String getEffectName()
	{
		return "potion.prefix." + this.name;
	}
	
	@Override
	public PotionBase register()
	{
		potionTypeList.add(this);
		bases.put(this.name, this);
		
		return this;
	}
	
	@Override
	public boolean isCombinable()
	{
		return false;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("BaseName", this.name);
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("PotionBase [");
		result.append("Name:\"").append(this.name).append("]");
		return result.toString();
	}
	
	@Override
	public int compareTo(IPotionType o)
	{
		if (o instanceof PotionBase)
		{
			PotionBase base = (PotionBase) o;
			return this.name != null && base.name != null ? this.name.compareTo(base.name) : 0;
		}
		return 0;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return this.equals((PotionBase) obj);
	}
	
	public boolean equals(PotionBase other)
	{
		if (this.name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!this.name.equals(other.name) || (BrewingAPI.defaultAwkwardBrewing && "awkward".equals(other.name)))
			return false;
		return true;
	}
	
	@Override
	public boolean accepts(IPotionType potion)
	{
		return false;
	}
	
	@Override
	public boolean matches(IPotionType type, PotionTypeList potionTypes)
	{
		for (IPotionType type1 : potionTypes)
		{
			if (type1 == this)
			{
				return true;
			}
			else if (type1 instanceof IPotionBase && ((IPotionBase) type1).accepts(type))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onApplied(IPotionType type, PotionTypeList potionTypes)
	{
	}
	
	@Override
	public IPotionType copy()
	{
		return this;
	}
	
	@Override
	public boolean isBadEffect()
	{
		return false;
	}
	
	@Override
	public PotionEffect getEffect()
	{
		return null;
	}
	
	@Override
	public int getMaxAmplifier()
	{
		return 0;
	}
	
	@Override
	public int getMaxDuration()
	{
		return 0;
	}
	
	@Override
	public int getDefaultDuration()
	{
		return 0;
	}
	
	@Override
	public IPotionType getInverted()
	{
		return null;
	}
	
	@Override
	public PotionBase getBase()
	{
		return null;
	}
	
	@Override
	public boolean isBase()
	{
		return true;
	}
	
	@Override
	public IPotionType onAmplified()
	{
		return this;
	}
	
	@Override
	public IPotionType onExtended()
	{
		return this;
	}
	
	@Override
	public IPotionType onDiluted()
	{
		return this;
	}
	
	@Override
	public IPotionType onGunpowderUsed()
	{
		return this;
	}
	
	@Override
	public IPotionType onInverted()
	{
		return this;
	}
	
	@Override
	public void apply_do(EntityLivingBase target, PotionEffect effect)
	{
	}
}
