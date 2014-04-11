package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public interface IPotionType extends Comparable<IPotionType>
{
	/** Name of the tag compound that stores the potion type list */
	public static final String		COMPOUND_NAME	= "Brewing";
	
	/** Version identifier for NBTs. **/
	public static final String		NBT_VERSION		= "1.1";
	
	/** List that stores ALL PotionTypes, also PotionBase types **/
	public static List<IPotionType>	potionTypeList	= new ArrayList();
	public static List<IPotionType>	combinableTypes	= new ArrayList();
	public static List<IPotionType>	effectTypes		= new ArrayList();
	
	/**
	 * Returns an unique identifier for this potion type.
	 * 
	 * @return the UUID
	 */
	public String getUUID();
	
	/**
	 * Creates a copy of this potion type. This is usually used to make a
	 * delegate.
	 * 
	 * @return a copy of this potion type
	 */
	public IPotionType copy();
	
	/**
	 * Registers a potion type. This is useful to sort the potion types.
	 * 
	 * @return this
	 */
	public IPotionType register();
	
	/**
	 * Returns true if this is a base potion type.
	 * 
	 * @return true, if this is a base potion type
	 */
	public boolean isBase();
	
	/**
	 * Returns true if this potion type is combinable with an other potion type.
	 * 
	 * @return true, if this is combinable
	 */
	public boolean isCombinable();
	
	/**
	 * Returns the underlying {@link PotionEffect} of this potion type.
	 * 
	 * @return the PotionEffect
	 */
	public PotionEffect getEffect();
	
	/**
	 * Returns true if this potion type has an effect
	 * 
	 * @return true, if this has an effect
	 */
	public boolean hasEffect();
	
	/**
	 * Returns the name of this potion type's effect. If the effect is
	 * {@code null}, an empty String is returned.
	 * 
	 * @return
	 */
	public String getEffectName();
	
	/**
	 * Returns the liquid color of this potion type's effect. If the effect is
	 * {@code null}, 0x0C0CFF is returned.
	 * 
	 * @return
	 */
	public int getLiquidColor();
	
	/**
	 * Returns the {@link Potion} of this potion type's effect. If the effect is
	 * {@code null}, {@code null} is returned.
	 * 
	 * @return the potion
	 */
	public Potion getPotion();
	
	/**
	 * Returns the potion ID of this potion type's effect. If the effect is
	 * {@code null}, {@code -1} is returned.
	 * 
	 * @return the potion ID
	 */
	public int getPotionID();
	
	/**
	 * Returns true if the effect of this potion type is a bad effect / debuff
	 * (red color) or a normal buff (green color)
	 * 
	 * @return true, if this has a bad effect
	 */
	public boolean isBadEffect();
	
	/**
	 * Returns the duration of this potion type's effect. If the effect is
	 * {@code null}, {@code 0} is returned.
	 * 
	 * @return the potion
	 */
	public int getDuration();
	
	/**
	 * Returns true if this potion type's effect is instant.
	 * 
	 * @return true, if this effect is instant.
	 */
	public boolean isInstant();
	
	/**
	 * Returns the amplifier of this potion type's effect. If the effect is
	 * {@code null}, {@code 0} is returned.
	 * 
	 * @return the potion
	 */
	public int getAmplifier();
	
	/**
	 * Returns the amount of redstone dust that had to be used to reach the
	 * duration of this potion effect.
	 * 
	 * @return the redstone amount
	 */
	public int getRedstoneAmount();
	
	/**
	 * Returns the amount of redstone dust that had to be used to reach the
	 * amplifier of this potion effect.
	 * 
	 * @return the redstone amount
	 */
	public int getGlowstoneAmount();
	
	/**
	 * Returns the mximimum amplifier this potion type's effect can reach.
	 * 
	 * @return the max amplifier
	 */
	public int getMaxAmplifier();
	
	/**
	 * Returns the mximimum duration this potion type's effect can reach.
	 * 
	 * @return the max duration
	 */
	public int getMaxDuration();
	
	/**
	 * Returns the default duration of this potion type's effect. If this effect
	 * is {@code null}, {@code 0} is returned.
	 * 
	 * @return the default duration
	 */
	public int getDefaultDuration();
	
	/**
	 * Returns the base potion type that is required to brew this potion type.
	 * 
	 * @return the base potion type
	 */
	public PotionBase getBase();
	
	/**
	 * Returns the inverted effect potion type. This is used when a fermented
	 * spider eye is applied to a potion.
	 * 
	 * @return the inverted potion type
	 */
	public IPotionType getInverted();
	
	/**
	 * Returns the ingredient that is used to brew this potion type.
	 * 
	 * @return the ingredient
	 */
	public ItemStack getIngredient();
	
	public boolean isImprovable();
	
	public boolean isExtendable();
	
	public boolean isDilutable();
	
	public boolean isInversible();
	
	public IPotionType onImproved();
	
	public IPotionType onExtended();
	
	public IPotionType onDiluted();
	
	public IPotionType onGunpowderUsed();
	
	public IPotionType onInverted();
	
	/**
	 * Returns a list of sub-types of this potion type.
	 * <p>
	 * If this is improvable, the potion type created by
	 * {@link IPotionType#onImproved()} is added to the list.<br>
	 * If this is extendable, the potion type created by
	 * {@link IPotionType#onExtended()} is added to the list.
	 * 
	 * @return
	 */
	public List<IPotionType> getSubTypes();
	
	/**
	 * Applies this potion type to the {@link ItemStack}. The item of the stack
	 * should be a {@link ItemPotion2}
	 * 
	 * @param stack
	 *            the stack
	 * @return the stack
	 */
	public ItemStack apply(ItemStack stack);
	
	/**
	 * Removes this potion type from the {@link ItemStack}. The item of the
	 * stack should be a {@link ItemPotion2}
	 * 
	 * @param stack
	 *            the stack
	 * @return the stack
	 */
	public ItemStack remove(ItemStack stack);
	
	/**
	 * Applies this potion type's effect to the living entity or player.
	 * 
	 * @param target
	 *            the target
	 */
	public void apply(EntityLivingBase target);
	
	/**
	 * Applies this potion type's effect to the living entity or player.
	 * <p>
	 * This method also calculates a new duration from the distance.<br>
	 * The formula for this is
	 * <p>
	 * <code>
	 * integer(distance*duration+0.5)</code>
	 * <p>
	 * If the effect is {@link Potion#heal} or {@link Potion#harm}, the effect
	 * is directly applied using
	 * {@link Potion#performEffect(EntityLivingBase, int)}
	 * 
	 * @param thrower
	 * @param target
	 * @param distance
	 */
	public void apply(EntityLivingBase thrower, EntityLivingBase target, double distance);
	
	/**
	 * Directly applies the given {@link PotionEffect} to the living entity or
	 * player.
	 * 
	 * @param target
	 *            the target
	 * @param effect
	 *            the effect to apply
	 */
	public void apply_do(EntityLivingBase target, PotionEffect effect);
	
	/**
	 * Writes this potion type to the {@link NBTTagCompound}.
	 * 
	 * @param nbt
	 *            the NBTTagCompound
	 */
	public void writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Reads this potion type from the {@link NBTTagCompound}.
	 * 
	 * @param nbt
	 *            the NBTTagCompound
	 */
	public void readFromNBT(NBTTagCompound nbt);
}