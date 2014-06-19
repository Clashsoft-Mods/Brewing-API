package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.potion.PotionRecipe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

/**
 * @author Clashsoft
 */
public class PotionBase extends AbstractPotionType
{
	public static final List<PotionBase>		baseList	= new ArrayList();
	public static final Map<String, PotionBase>	baseMap		= new HashMap();
	
	private String								name;
	
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
		
		PotionRecipe.addRecipe(ingredient, null, this);
	}
	
	@Override
	public String getUUID()
	{
		return this.name;
	}
	
	public static PotionBase getFromIngredient(ItemStack ingredient)
	{
		PotionRecipe recipe = PotionRecipe.get(ingredient);
		IPotionType pt = recipe.getOutput();
		if (pt instanceof PotionBase)
			return (PotionBase) pt;
		return null;
	}
	
	@Override
	public int getLiquidColor()
	{
		return 0x0C0CFF;
	}
	
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
		baseList.add(this);
		if (this.name != null)
		{
			baseMap.put(this.name, this);
		}
		
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
		PotionBase base = baseMap.get(nbt.getString("BaseName"));
		if (base != null)
		{
			this.name = base.name;
		}
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
		PotionBase other = (PotionBase) obj;
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
	public IPotionType onImproved()
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
