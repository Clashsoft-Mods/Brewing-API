package clashsoft.brewingapi.potion.type;

import java.util.ArrayList;
import java.util.List;

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
	
	public IPotionType copy();
	
	/**
	 * Sorts the PotionType in the lists
	 * 
	 * @return PotionType
	 */
	public IPotionType register();
	
	/**
	 * Determines if the effect is a bad effect (red color)
	 */
	public boolean isBadEffect();
	
	public boolean isCombinable();
	
	public PotionEffect getEffect();
	
	public boolean hasEffect();
	
	public Potion getPotion();
	
	public int getPotionID();
	
	public int getDuration();
	
	public int getAmplifier();
	
	public int getRedstoneAmount();
	
	public int getGlowstoneAmount();
	
	public String getEffectName();
	
	public int getMaxAmplifier();
	
	public int getMaxDuration();
	
	public int getDefaultDuration();
	
	public boolean isImprovable();
	
	public boolean isExtendable();
	
	public boolean isDilutable();
	
	public boolean isInversible();
	
	public IPotionType getInverted();
	
	public ItemStack getIngredient();
	
	public PotionBase getBase();
	
	public boolean isBase();
	
	public int getLiquidColor();
	
	public IPotionType onImproved();
	
	public IPotionType onExtended();
	
	public IPotionType onDiluted();
	
	public IPotionType onGunpowderUsed();
	
	public IPotionType onInverted();
	
	/**
	 * Returns the subtypes for one Potion depending on improvability and extendability
	 * 
	 * @return
	 */
	public List<IPotionType> getSubTypes();
	
	/**
	 * Writes the PotionType to the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return ItemStack with PotionType NBT
	 */
	public ItemStack apply(ItemStack stack);
	
	public ItemStack remove(ItemStack stack);
	
	public void writeToNBT(NBTTagCompound nbt);
	
	public void readFromNBT(NBTTagCompound nbt);
}