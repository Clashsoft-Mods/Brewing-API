package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.oredict.OreDictionary;

public class PotionBase extends AbstractPotionType
{
	public static final List<PotionBase>		baseList	= new ArrayList();
	public static final Map<String, PotionBase>	baseMap		= new HashMap();
	
	private String								name;
	private ItemStack							ingredient;
	
	public PotionBase()
	{
		super();
	}
	
	public PotionBase(String name, ItemStack ingredient)
	{
		this.name = name;
		this.ingredient = ingredient;
	}
	
	public PotionBase(String name)
	{
		this(name, null);
	}
	
	@Override
	public String getUUID()
	{
		return this.name;
	}
	
	public static PotionBase getFromIngredient(ItemStack ingredient)
	{
		for (PotionBase pb : baseList)
		{
			if (OreDictionary.itemMatches(pb.getIngredient(), ingredient, true))
			{
				return pb;
			}
			if (pb.getIngredient().getItem() == ingredient.getItem() && pb.getIngredient().getItemDamage() == ingredient.getItemDamage())
			{
				return pb;
			}
		}
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
	public void readFromNBT(NBTTagCompound nbt)
	{
		PotionBase base = baseMap.get(nbt.getString("BaseName"));
		if (base != null)
		{
			this.name = base.name;
			this.ingredient = base.ingredient;
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
		result.append("Name:\"").append(this.name).append("\", ");
		result.append("Ingredient:[").append(this.ingredient).append("]]");
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
	
	public boolean matches(PotionBase base)
	{
		String name = base.getName();
		if (BrewingAPI.defaultAwkwardBrewing && "awkward".equals(name))
		{
			return true;
		}
		return this.name.equals(base.getName());
	}
	
	@Override
	public IPotionType copy()
	{
		return new PotionBase(this.name, this.ingredient);
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
	public ItemStack getIngredient()
	{
		return this.ingredient;
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
