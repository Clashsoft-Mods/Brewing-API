package clashsoft.brewingapi.potion.recipe;

import java.util.List;

import clashsoft.brewingapi.potion.attribute.IPotionAttribute;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.item.ItemStack;

public class PotionRecipeAttribute implements IPotionRecipe
{
	private ItemStack			input;
	private IPotionAttribute	attribute;
	
	public PotionRecipeAttribute(ItemStack input, IPotionAttribute attribute)
	{
		this.input = input;
		this.attribute = attribute;
	}
	
	@Override
	public ItemStack getInput()
	{
		return this.input;
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		List<IPotionType> potionTypes = PotionType.getPotionTypes(potion);
		for (IPotionType potionType : potionTypes)
		{
			potionType.addAttribute(this.attribute);
		}
		return potion;
	}
}
