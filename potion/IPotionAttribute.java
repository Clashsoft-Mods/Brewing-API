package clashsoft.brewingapi.potion;

import net.minecraft.nbt.NBTTagCompound;

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
	 * Writed this attribute to the NBT tag compound
	 * 
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Reads this attribute from the NBT tag compound
	 * 
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt);
	
	/**
	 * Clones this attribute
	 * 
	 * @return a clone of this attribute
	 */
	public IPotionAttribute clone();
}
