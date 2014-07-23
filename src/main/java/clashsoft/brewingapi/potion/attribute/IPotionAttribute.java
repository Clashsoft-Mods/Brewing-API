package clashsoft.brewingapi.potion.attribute;

import java.util.HashMap;
import java.util.Map;

import clashsoft.brewingapi.potion.type.IPotionType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

public interface IPotionAttribute
{
	public static Map<String, IPotionAttribute>	attributes	= new HashMap();
	
	/**
	 * Registers this {@link IPotionAttribute}. It is recommended to register it
	 * by adding it to {@link IPotionAttribute#attributes}. See
	 * {@link AbstractPotionAttribute#register()} for a template.
	 * 
	 * @see AbstractPotionAttribute#register()
	 * @return this potion attribute
	 */
	public IPotionAttribute register();
	
	/**
	 * Returns a copy of this {@link IPotionAttribute}. If this attribute is
	 * supposed to be a singleton, it is recommended to return the singleton
	 * instance in this method. Otherwise, a new instance should be created.
	 * 
	 * @return this potion attribute or a new instance
	 */
	public IPotionAttribute copy();
	
	/**
	 * Returns the name of this {@link IPotionAttribute}. Note that the name is
	 * used for mapping it in {@link IPotionAttribute#attributes}.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Returns the display name of this {@link IPotionAttribute}, depending on
	 * the given {@link IPotionType} {@code type}. The display name is usually
	 * the localized version of the name.
	 * 
	 * @param type
	 *            the potion type
	 * @return the display name
	 */
	public String getDisplayName(IPotionType type);
	
	/**
	 * Returns the modded {@link PotionEffect} {@code effect} of this
	 * {@link IPotionAttribute}, depending on the given {@link IPotionType}
	 * {@code type}.
	 * 
	 * @param type
	 *            the type
	 * @param effect
	 *            the effect
	 * @return the modded effect
	 */
	public PotionEffect getModdedEffect(IPotionType type, PotionEffect effect);
	
	/**
	 * Reads this {@link IPotionAttribute} from the given {@link NBTTagCompound}
	 * {@code nbt}, if it is not a singleton attribute.
	 * 
	 * @param nbt
	 *            the NBT
	 */
	public void writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Writes this {@link IPotionAttribute} to the given {@link NBTTagCompound}
	 * {@code nbt}, if it is not a singleton attribute.
	 * 
	 * @param nbt
	 *            the NBT
	 */
	public void readFromNBT(NBTTagCompound nbt);
}
