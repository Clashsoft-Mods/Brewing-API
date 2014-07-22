package clashsoft.brewingapi.potion.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clashsoft.brewingapi.potion.type.PotionBase;

import net.minecraft.item.ItemStack;

public interface IPotionBase
{
	public static final Map<String, IPotionBase>	baseMap		= new HashMap();
	public static final List<PotionBase>			potionBases	= new ArrayList();
	
	public static IPotionBase						water		= new PotionBaseWater();
	
	public IPotionBase register();
	
	public String getName();
	
	public int getLiquidColor();
	
	public boolean matches(ItemStack potion);
}
