package clashsoft.brewingapi.potion.base;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IPotionBase
{
	public static final Map<String, IPotionBase>	bases		= new HashMap();
	
	public static IPotionBase						water		= new PotionBaseWater();
	
	public IPotionBase register();
	
	public String getName();
	
	public int getLiquidColor();
	
	public ItemStack apply(ItemStack potion);
	
	public boolean matches(ItemStack potion);
}
