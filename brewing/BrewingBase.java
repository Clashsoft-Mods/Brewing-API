package clashsoft.brewingapi.brewing;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingBase extends Brewing implements Comparable<Brewing>
{
	public String					basename;
	
	public static List<BrewingBase>	baseBrewings2	= new LinkedList<BrewingBase>();
	
	public BrewingBase(String par1, ItemStack par2ItemStack)
	{
		super(null, 0, 0, par2ItemStack, null);
		basename = par1;
		baseBrewings2.add(this);
	}
	
	public BrewingBase(String par1)
	{
		this(par1, new ItemStack(0, 0, 0));
	}
	
	public static BrewingBase getBrewingBaseFromIngredient(ItemStack par1ItemStack)
	{
		try
		{
			for (BrewingBase b : baseBrewings2)
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
	public NBTTagCompound createNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("BaseName", basename);
		return nbt;
	}
	
	public static Brewing readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		String name = par1NBTTagCompound.hasKey("BaseName") ? par1NBTTagCompound.getString("BaseName") : "";
		int ingredientID = par1NBTTagCompound.hasKey("IngredientID") ? par1NBTTagCompound.getInteger("IngredientID") : 0;
		int ingredientAmount = par1NBTTagCompound.hasKey("IngredientAmount") ? par1NBTTagCompound.getInteger("IngredientAmount") : 0;
		int ingredientDamage = par1NBTTagCompound.hasKey("IngredientDamage") ? par1NBTTagCompound.getInteger("IngredientDamage") : 0;
		return new BrewingBase(name, new ItemStack(ingredientID, ingredientAmount, ingredientDamage));
	}
	
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
			return (basename != null && ((BrewingBase)o).basename != null) ? basename.compareTo(basename) : 0; 
		else
			return super.compareTo(o);
	}
}
