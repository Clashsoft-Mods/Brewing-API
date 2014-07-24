package clashsoft.brewingapi.potion.type;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.attribute.IPotionAttribute;
import clashsoft.brewingapi.potion.base.IPotionBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Clashsoft
 */
public interface IPotionType extends Comparable<IPotionType>
{
	/** Name of the tag compound that stores the potion type list */
	public static final String					COMPOUND_NAME	= "Brewing";
	
	/** Version identifier for NBTs. **/
	public static final String					NBT_VERSION		= "1.1";
	
	/** List that stores ALL PotionTypes, also PotionBase types **/
	public static TIntObjectMap<IPotionType>	potionTypes		= new TIntObjectHashMap();
	public static List<IPotionType>				potionTypeList	= new ArrayList();
	public static List<IPotionType>				combinableTypes	= new ArrayList();
	public static List<IPotionType>				effectTypes		= new ArrayList();
	
	/**
	 * Returns an unique identifier for this {@link IPotionType}.
	 * 
	 * @return the UUID
	 */
	public String getUUID();
	
	/**
	 * Creates a copy of this {@link IPotionType}. This is usually used to make
	 * a delegate.
	 * 
	 * @return a copy of this {@link IPotionType}
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
	 * Returns true if this {@link IPotionType} is combinable with an other
	 * potion type.
	 * 
	 * @return true, if this is combinable
	 */
	public boolean isCombinable();
	
	/**
	 * Returns the underlying {@link PotionEffect} of this {@link IPotionType}.
	 * 
	 * @return the PotionEffect
	 */
	public PotionEffect getEffect();
	
	/**
	 * Returns true if this {@link IPotionType} has an effect
	 * 
	 * @return true, if this has an effect
	 */
	public boolean hasEffect();
	
	/**
	 * Returns the name of this {@link IPotionType}'s effect. If the effect is
	 * {@code null}, an empty String is returned.
	 * 
	 * @return
	 */
	public String getEffectName();
	
	/**
	 * Returns the display name of this {@link IPotionType}'s effect. The
	 * display name usually contains the effect name, the amplifier and the
	 * duration. The default implementation returns
	 * <p>
	 * <code>
	 * EffectName [RomanAmplifier] ([Minutes]:[Seconds])
	 * </code>
	 * 
	 * @return the display name
	 */
	public StringBuilder getDisplayName();
	
	/**
	 * Returns the liquid color of this {@link IPotionType}'s effect. If the
	 * effect is {@code null}, 0x0C0CFF is returned, which is the color of a
	 * Bottle of Water.
	 * 
	 * @return
	 */
	public int getLiquidColor();
	
	/**
	 * Returns the {@link Potion} of this {@link IPotionType}'s effect. If the
	 * effect is {@code null}, {@code null} is returned.
	 * 
	 * @return the potion
	 */
	public Potion getPotion();
	
	/**
	 * Returns the potion ID of this {@link IPotionType}'s effect. If the effect
	 * is {@code null}, {@code -1} is returned.
	 * 
	 * @return the potion ID
	 */
	public int getPotionID();
	
	/**
	 * Returns true if the effect of this {@link IPotionType} is a bad effect /
	 * debuff (red color) or a normal buff (green color)
	 * 
	 * @return true, if this has a bad effect
	 */
	public boolean isBadEffect();
	
	/**
	 * Returns the duration of this {@link IPotionType}'s effect. If the effect
	 * is {@code null}, {@code 0} is returned.
	 * 
	 * @return the potion
	 */
	public int getDuration();
	
	/**
	 * Returns true if this {@link IPotionType}'s effect is instant.
	 * 
	 * @return true, if this effect is instant.
	 */
	public boolean isInstant();
	
	/**
	 * Returns the amplifier of this {@link IPotionType}'s effect. If the effect
	 * is {@code null}, {@code 0} is returned.
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
	 * Returns the mximimum amplifier this {@link IPotionType}'s effect can
	 * reach.
	 * 
	 * @return the max amplifier
	 */
	public int getMaxAmplifier();
	
	/**
	 * Returns the mximimum duration this {@link IPotionType}'s effect can
	 * reach.
	 * 
	 * @return the max duration
	 */
	public int getMaxDuration();
	
	/**
	 * Returns the default duration of this {@link IPotionType}'s effect. If
	 * this effect is {@code null}, {@code 0} is returned.
	 * 
	 * @return the default duration
	 */
	public int getDefaultDuration();
	
	/**
	 * Returns the base potion type that is required to brew this
	 * {@link IPotionType}.
	 * 
	 * @return the base potion type
	 */
	public IPotionBase getBase();
	
	/**
	 * Returns the inverted effect potion type. This is used when a fermented
	 * spider eye is applied to a potion.
	 * 
	 * @return the inverted potion type
	 */
	public IPotionType getInverted();
	
	/**
	 * Returns the ingredient that is used to brew this {@link IPotionType}.
	 * 
	 * @return the ingredient
	 */
	public ItemStack getIngredient();
	
	/**
	 * Returns true if this {@link IPotionType} is amplifiable with Glowstone.
	 * 
	 * @return true, if this {@link IPotionType} is amplifiable
	 */
	public boolean isAmplifiable();
	
	/**
	 * Returns true if this {@link IPotionType} is extendable with Redstone.
	 * 
	 * @return true, if this {@link IPotionType} is extendable
	 */
	public boolean isExtendable();
	
	/**
	 * Returns true if this {@link IPotionType} is dilutable.
	 * 
	 * @return true, if this {@link IPotionType} is dilutable
	 */
	public boolean isDilutable();
	
	/**
	 * Returns true if this {@link IPotionType} is inversible with a Fermented
	 * Spider Eye.
	 * 
	 * @return true, if this {@link IPotionType} is inversible
	 */
	public boolean isInversible();
	
	/**
	 * Returns the Glowstone-amplified version of this {@link IPotionType}.
	 * 
	 * @return the amplified version
	 */
	public IPotionType onAmplified();
	
	/**
	 * Returns the Redstone-extended version of this {@link IPotionType}.
	 * 
	 * @return the extended version
	 */
	public IPotionType onExtended();
	
	/**
	 * Returns the diluted version of this {@link IPotionType}.
	 * 
	 * @return the diluted version
	 */
	public IPotionType onDiluted();
	
	/**
	 * Returns the splash version of this {@link IPotionType}.
	 * 
	 * @return the splash version
	 */
	public IPotionType onGunpowderUsed();
	
	/**
	 * Returns the Fermented Spider Eye-inverted version of this
	 * {@link IPotionType}.
	 * 
	 * @return the inverted version
	 */
	public IPotionType onInverted();
	
	/**
	 * Sets the attributes of this {@link IPotionType} to the given {@link List}
	 * of {@link IPotionAttribute} {@code attributes}.
	 * 
	 * @param attributes
	 *            the attributes
	 */
	public void setAttributes(List<IPotionAttribute> attributes);
	
	/**
	 * Returns a {@link List} of {@link IPotionAttribute} of this
	 * {@link IPotionType}'s attributes.
	 * 
	 * @return the attributes
	 */
	public List<IPotionAttribute> getAttributes();
	
	/**
	 * Returns true if this {@link IPotionType} has any attributes.
	 * 
	 * @return true, if this {@link IPotionType} has attributes
	 */
	public boolean hasAttributes();
	
	/**
	 * Adds the given {@link IPotionAttribute} {@code attribute} to this potion
	 * type's attributes
	 * 
	 * @param attribute
	 */
	public void addAttribute(IPotionAttribute attribute);
	
	/**
	 * Returns true if this {@link IPotionType} has the given
	 * {@link IPotionAttribute} {@code attribute}.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true, if this {@link IPotionType} has the attribute
	 */
	public boolean hasAttribute(IPotionAttribute attribute);
	
	/**
	 * Returns a list of sub-types of this {@link IPotionType}.
	 * <p>
	 * If this is improvable, the potion type created by
	 * {@link IPotionType#onImproved()} is added to the list.<br>
	 * If this is extendable, the potion type created by
	 * {@link IPotionType#onExtended()} is added to the list.
	 * 
	 * @return the list of subtypes
	 */
	public List<IPotionType> getSubTypes();
	
	public ItemStack apply(ItemStack potion);
	
	/**
	 * Adds this {@link IPotionType} to the given {@link PotionTypesList} {@code potionTypes}.
	 * 
	 * @param potionTypes
	 *            the cached list of potion types
	 */
	public ItemStack apply(PotionTypeList potionTypes);
	
	public ItemStack remove(ItemStack potion);
	
	/**
	 * Removes this {@link IPotionType} from the given {@link PotionTypesList} {@code potionTypes}.
	 * 
	 * @param potionTypes
	 *            the cached list of potion types
	 */
	public ItemStack remove(PotionTypeList potionTypes);
	
	/**
	 * Applies this {@link IPotionType}'s effect to the living entity or player.
	 * 
	 * @param target
	 *            the target
	 */
	public void apply(EntityLivingBase target);
	
	/**
	 * Applies this {@link IPotionType}'s effect to the living entity or player.
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
	 * Writes this {@link IPotionType} to the given {@link NBTTagCompound}
	 * {@code nbt}.
	 * 
	 * @param nbt
	 *            the NBTTagCompound
	 */
	public void writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Reads this {@link IPotionType} from the given {@link NBTTagCompound}
	 * {@code nbt}.
	 * 
	 * @param nbt
	 *            the NBTTagCompound
	 */
	public void readFromNBT(NBTTagCompound nbt);
}
