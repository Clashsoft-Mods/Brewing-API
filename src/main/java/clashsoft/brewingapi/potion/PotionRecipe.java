package clashsoft.brewingapi.potion;

import gnu.trove.map.hash.TCustomHashMap;

import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.cslib.minecraft.util.ItemStackHashingStrategy;

import net.minecraft.item.ItemStack;

public class PotionRecipe
{
	public static final TCustomHashMap<ItemStack, PotionRecipe> potionRecipes = new TCustomHashMap(ItemStackHashingStrategy.instance);
	
	private IPotionType output;
	private ItemStack input;
	
	public PotionRecipe(ItemStack input, IPotionType output)
	{
		this.input = input;
		this.output = output;
	}
	
	public static PotionRecipe get(ItemStack input)
	{
		return potionRecipes.get(input);
	}
	
	public static void registerRecipe(PotionRecipe recipe)
	{
		potionRecipes.put(recipe.input, recipe);
	}
	
	public static void addRecipe(ItemStack ingredient, IPotionType potionType)
	{
		registerRecipe(new PotionRecipe(ingredient, potionType));
	}
	
	public ItemStack getInput()
	{
		return this.input;
	}
	
	public IPotionType getOutput()
	{
		return this.output;
	}
	
	public ItemStack apply(ItemStack potionStack)
	{
		PotionBase requiredBase = this.output.getBase();
		boolean flag = false;
		
		List<IPotionType> potionTypes = ((ItemPotion2) potionStack.getItem()).getPotionTypes(potionStack);
		
		if (requiredBase == null)
		{
			flag = potionTypes.isEmpty();
		}
		else
		{
			for (IPotionType pt : potionTypes)
			{
				if (pt instanceof PotionBase && ((PotionBase) pt).equals(requiredBase))
				{
					flag = true;
					pt.remove(potionStack);
				}
			}
		}
		
		if (flag)
		{
			return this.output.apply(potionStack);
		}
		return potionStack;
	}
}
