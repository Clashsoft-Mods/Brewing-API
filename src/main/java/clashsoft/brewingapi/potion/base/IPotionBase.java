package clashsoft.brewingapi.potion.base;

import java.util.HashMap;
import java.util.Map;

import clashsoft.brewingapi.potion.type.PotionBase;

import net.minecraft.item.ItemStack;

public interface IPotionBase
{
	public static final Map<String, PotionBase>	baseMap	= new HashMap();
	
	public IPotionBase register();
	
	public String getName();
	
	public int getLiquidColor();
	
	public boolean matches(ItemStack potion);
}
