package clashsoft.brewingapi.api;

import net.minecraft.nbt.NBTBase;

/**
 * The Interface IBrewingAttribute.
 * 
 * @param <T>
 *            the generic type
 */
public interface IPotionAttribute<T>
{
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public T getValue();
	
	/**
	 * Sets the value.
	 * 
	 * @return the t
	 */
	public T setValue();
	
	/**
	 * Creates a NBTTag for this attribute
	 * 
	 * @return the NBT base
	 */
	public NBTBase toNBT();
	
	/**
	 * Sets this attribute from the given NBTBase
	 * 
	 * @param nbtbase
	 *            the nbtbase
	 * @return the i brewing attribute
	 */
	public IPotionAttribute fromNBT(NBTBase nbtbase);
}
