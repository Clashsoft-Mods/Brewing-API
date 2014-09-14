package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.cslib.minecraft.stack.CSStacks;

import net.minecraft.item.ItemStack;

public class CustomPotionRecipe extends AbstractPotionRecipe
{
	public ItemStack input;
	public ItemStack output;
	
	public CustomPotionRecipe(ItemStack input, ItemStack ingredient, ItemStack output)
	{
		super(ingredient);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean canApply(PotionTypeList potionTypes)
	{
		return CSStacks.equals(potionTypes.getPotion(), this.input);
	}

	@Override
	public void apply(PotionTypeList potionTypes)
	{
		potionTypes.setStack(this.output.copy());
	}
}
