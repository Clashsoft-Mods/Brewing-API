package clashsoft.brewingapi.brewing;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingBase extends Brewing implements Comparable<Brewing>
{
	public static Map<String, BrewingBase>	baseMap	= new HashMap();
	
	public String							basename;
	
	public BrewingBase()
	{
		super();
	}
	
	public BrewingBase(String par1, ItemStack par2ItemStack)
	{
		super(null, 0, 0, par2ItemStack, null);
		basename = par1;
	}
	
	public BrewingBase(String par1)
	{
		this(par1, new ItemStack(0, 0, 0));
	}
	
	public static BrewingBase getBrewingBaseFromIngredient(ItemStack par1ItemStack)
	{
		try
		{
			for (BrewingBase b : baseBrewings)
			{
				if (OreDictionary.itemMatches(b.getIngredient(), par1ItemStack, true))
				{
					return b;
				}
				if (b.getIngredient().getItem() == par1ItemStack.getItem() && b.getIngredient().getItemDamage() == par1ItemStack.getItemDamage())
				{
					return b;
				}
			}
		}
		catch (Exception ex)
		{
		}
		return null;
	}
	
	@Override
	public BrewingBase register()
	{
		super.register();
		baseMap.put(basename, this);
		return this;
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		String nbtVersion = par1NBTTagCompound.getString("VERSION");
		if ("1.0.1".equals(nbtVersion))
		{
			this.basename = par1NBTTagCompound.hasKey("BaseName") ? par1NBTTagCompound.getString("BaseName") : "";
			
			int ingredientID = par1NBTTagCompound.hasKey("IngredientID") ? par1NBTTagCompound.getInteger("IngredientID") : 0;
			int ingredientAmount = par1NBTTagCompound.hasKey("IngredientAmount") ? par1NBTTagCompound.getInteger("IngredientAmount") : 0;
			int ingredientDamage = par1NBTTagCompound.hasKey("IngredientDamage") ? par1NBTTagCompound.getInteger("IngredientDamage") : 0;
			
			this.setIngredient(new ItemStack(ingredientID, ingredientAmount, ingredientDamage));
		}
		else
		{
			BrewingBase base = baseMap.get(par1NBTTagCompound.getString("BaseName"));
			if (base != null)
			{
				this.basename = base.basename;
				this.setIngredient(base.getIngredient());
			}
		}
	}
	
	@Override
	public String toString()
	{
		String s = "BrewingBase{";
		if (basename != null)
			s += "Name<" + basename + ">";
		if (this.getIngredient() != null)
			s += "Ingredient<" + this.getIngredient().itemID + ":" + this.getIngredient().getItemDamage() + ">";
		s += "}";
		return s;
	}
	
	@Override
	public int compareTo(Brewing o)
	{
		if (o instanceof BrewingBase)
			return (basename != null && ((BrewingBase) o).basename != null) ? basename.compareTo(((BrewingBase) o).basename) : 0;
		else
			return super.compareTo(o);
	}
}
