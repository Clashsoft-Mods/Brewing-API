package clashsoft.brewingapi.brewing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class PotionBase extends PotionType implements Comparable<PotionType>
{
	public static List<PotionBase>			baseList	= new ArrayList();
	public static Map<String, PotionBase>	baseMap		= new HashMap();
	
	public String							basename;
	
	public PotionBase()
	{
		super();
	}
	
	public PotionBase(String name, ItemStack ingredient)
	{
		super(null, 0, 0, ingredient, null);
		basename = name;
	}
	
	public PotionBase(String name)
	{
		this(name, null);
	}
	
	public static PotionBase getBrewingBaseFromIngredient(ItemStack ingredient)
	{
		for (PotionBase pb : baseList)
		{
			if (OreDictionary.itemMatches(pb.getIngredient(), ingredient, true))
				return pb;
			if (pb.getIngredient().getItem() == ingredient.getItem() && pb.getIngredient().getItemDamage() == ingredient.getItemDamage())
				return pb;
		}
		return null;
	}
	
	@Override
	public PotionBase register()
	{
		super.register();
		
		baseList.add(this);
		if (this.basename != null)
			baseMap.put(this.basename, this);
		
		return this;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		String nbtVersion = nbt.getString("VERSION");
		if ("1.1".equals(nbtVersion))
		{
			PotionBase base = baseMap.get(nbt.getString("BaseName"));
			if (base != null)
			{
				this.basename = base.basename;
				this.setIngredient(base.getIngredient());
			}
		}
		else
		{
			this.basename = nbt.getString("BaseName");
			
			int ingredientID = nbt.getInteger("IngredientID");
			int ingredientAmount = nbt.getInteger("IngredientAmount");
			int ingredientDamage = nbt.getInteger("IngredientDamage");
			
			this.setIngredient(new ItemStack(ingredientID, ingredientAmount, ingredientDamage));
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		String nbtVersion = NBTVersion;
		if ("1.1".equals(nbtVersion))
		{
			nbt.setString("BaseName", this.basename);
		}
		else
		{
			nbt.setString("BaseName", this.basename);
			super.writeToNBT(nbt);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("PotionBase {");
		if (basename != null)
			result.append("Name=[\"").append(basename).append("\"]");
		if (this.getIngredient() != null)
			result.append("&Ingredient=[").append(this.getIngredient().itemID).append(":").append(this.getIngredient().getItemDamage()).append("]");
		result.append("}");
		return result.toString();
	}
	
	@Override
	public int compareTo(PotionType o)
	{
		if (o instanceof PotionBase)
			return (basename != null && ((PotionBase) o).basename != null) ? basename.compareTo(((PotionBase) o).basename) : 0;
		else
			return super.compareTo(o);
	}
}
