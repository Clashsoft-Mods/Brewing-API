package clashsoft.brewingapi.potion.base;

import java.util.HashMap;
import java.util.Map;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;

public interface IPotionBase
{
	public static final Map<String, IPotionBase>	bases	= new HashMap();
	
	public static IPotionBase						water	= new PotionBaseWater();
	
	/**
	 * Registers this {@link IPotionBase}. It is recommended to register it by
	 * adding it to {@link IPotionBase#bases}.
	 * 
	 * @return this potion recipe
	 */
	public IPotionBase register();
	
	/**
	 * Returns the name of this {@link IPotionBase}.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Returns the liquid color of this {@link IPotionBase}. Usually
	 * {@code 0x0C0CFF}.
	 * 
	 * @return the liquid color
	 */
	public int getLiquidColor();
	
	/**
	 * Applies this potion base to the {@link ItemStack}. The item of the stack
	 * should be an instance of {@link ItemPotion2}.
	 * 
	 * @param potion
	 *            the potion stack
	 * @return the potion
	 */
	public ItemStack apply(ItemStack potion);
	
	/**
	 * Returns true if this potion base matches the given {@link ItemStack}
	 * {@code potion}. The usual implementation returns true if this potion base
	 * can be found in the potion.
	 * 
	 * @param potion
	 *            the potion stack
	 * @return true, if this potion base matches the potion.
	 */
	public boolean matches(ItemStack potion);
}
