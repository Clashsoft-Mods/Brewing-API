package clashsoft.brewingapi;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class BAPICreativeTabs extends CreativeTabs
{
	private ItemStack icon;
	
	public BAPICreativeTabs(String label)
	{
		super(label);
	}
	
	public BAPICreativeTabs setIconItemStack(ItemStack stack)
	{
		this.icon = stack;
		return this;
	}

	/**
     * Get the ItemStack that will be rendered to the tab.
     */
    public ItemStack getIconItemStack()
    {
        return icon;
    }
}
