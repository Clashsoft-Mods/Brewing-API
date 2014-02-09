package clashsoft.brewingapi.brewing;

import java.util.*;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.api.IIngredientHandler;
import clashsoft.brewingapi.api.IPotionAttribute;
import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class that stores all data that the new potion need and all new potion types
 * 
 * @author Clashsoft
 */
public class PotionType implements Comparable<PotionType>
{
	/** Name of the tag compound that stores the potion type list */
	public static final String					COMPOUND_NAME				= "Brewing";
	/** Version identifier for NBTs. **/
	public static final String					NBT_VERSION					= "1.1";
	
	/** List that stores ALL PotionTypes, also PotionBase types **/
	public static List<PotionType>				potionTypeList				= new ArrayList<PotionType>();
	public static Map<PotionEffect, PotionType>	effectMap					= new HashMap();
	
	public static List<PotionType>				combinableEffects			= new ArrayList<PotionType>();
	
	public static Map<String, IPotionAttribute>	defaultExtendedAttributes	= new HashMap<String, IPotionAttribute>();
	
	/** The effect **/
	private PotionEffect						effect;
	/** Maximum effect amplifier **/
	private int									maxAmplifier;
	/** Maximum effect duration **/
	private int									maxDuration;
	/** Fermented Spider Eye effect **/
	private PotionType							inverted;
	/** The ingredient to brew the potion **/
	private ItemStack							ingredient;
	/** Determines the base that is needed to brew the potion **/
	private PotionBase							base;
	/** Was this PotionType read from a NBT? **/
	private boolean								dummy						= false;
	
	private Map<String, IPotionAttribute>		extendedAttributes			= new HashMap<String, IPotionAttribute>();
	
	protected PotionType()
	{
		
	}
	
	/**
	 * Creates a new PotionType
	 * 
	 * @param effect
	 *            the effect
	 * @param maxAmplifier
	 *            the max amplifier
	 * @param maxDuration
	 *            the max duration
	 */
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration)
	{
		this(effect, maxAmplifier, maxDuration, (PotionType) null);
	}
	
	/**
	 * Creates a new PotionType
	 * 
	 * @param effect
	 *            Effect
	 * @param maxAmplifier
	 *            the max amplifier
	 * @param maxDuration
	 *            the max duration
	 * @param inverted
	 *            the inverted brewing
	 */
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration, PotionType inverted)
	{
		this(effect, maxAmplifier, maxDuration, inverted, (ItemStack) null, (PotionBase) null);
	}
	
	/**
	 * Creates a new PotionType
	 * 
	 * @param effect
	 *            the effect
	 * @param maxAmplifier
	 *            the max amplifier
	 * @param maxDuration
	 *            the max duration
	 * @param ingredient
	 *            the ingredient
	 * @param base
	 *        the base
	 */
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration, ItemStack ingredient, PotionBase base)
	{
		this(effect, maxAmplifier, maxDuration, (PotionType) null, ingredient, base);
	}
	
	/**
	 * Creates a new PotionType with Opposite, Ingredient and Base
	 * 
	 * @param effect
	 *            the effect
	 * @param maxAmplifier
	 *            the max amplifier
	 * @param maxDuration
	 *            the max duration
	 * @param inverted
	 *            the inverted brewing
	 * @param ingredient
	 *            the ingredient
	 */
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration, PotionType inverted, ItemStack ingredient, PotionBase base)
	{
		this.effect = effect;
		this.maxAmplifier = maxAmplifier;
		this.maxDuration = maxDuration;
		this.inverted = inverted;
		this.ingredient = ingredient;
		this.base = base;
	}
	
	public PotionType copy()
	{
		PotionType potionType = new PotionType(this.getEffect(), this.getMaxAmplifier(), this.getMaxDuration(), this.getInverted(), this.getIngredient(), this.getBase());
		potionType.extendedAttributes = new HashMap(this.extendedAttributes);
		potionType.dummy = this.dummy;
		return potionType;
	}
	
	/**
	 * Sorts the PotionType in the lists
	 * 
	 * @return PotionType
	 */
	public PotionType register()
	{
		potionTypeList.add(this);
		
		if (this.isCombinable())
			combinableEffects.add(this);
		
		if (this.hasEffect())
			effectMap.put(this.getEffect(), this);
		
		return this;
	}
	
	/**
	 * Determines if the effect is a bad effect (red color)
	 */
	public boolean isBadEffect()
	{
		if (this.hasEffect())
		{
			switch (this.getPotionID())
			{
				case 2:
				case 4:
				case 7:
				case 9:
				case 15:
				case 17:
				case 18:
				case 19:
				case 20:
					return true;
			}
			if (BrewingAPI.isClashsoftLibInstalled())
			{
				if (this.getPotion() instanceof clashsoft.cslib.minecraft.potion.CustomPotion)
				{
					return ((clashsoft.cslib.minecraft.potion.CustomPotion) this.getPotion()).isBadEffect();
				}
			}
		}
		return false;
	}
	
	public boolean isCombinable()
	{
		return !this.isBase();
	}
	
	public PotionEffect getEffect()
	{
		return this.effect;
	}
	
	public boolean hasEffect()
	{
		return this.getEffect() != null && this.getPotionID() > 0;
	}
	
	public Potion getPotion()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? Potion.potionTypes[pe.getPotionID()] : null;
	}
	
	public int getPotionID()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getPotionID() : -1;
	}
	
	public int getDuration()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getDuration() : -1;
	}
	
	public int getAmplifier()
	{
		PotionEffect pe = this.getEffect();
		return pe != null ? pe.getAmplifier() : -1;
	}
	
	public int getRedstoneAmount()
	{
		int duration = this.getDuration();
		int defaultDuration = this.getDefaultDuration();
		int redstoneAmount = 0;
		if (duration > 0)
		{
			while (duration > defaultDuration)
			{
				redstoneAmount++;
				duration /= 2;
			}
		}
		return redstoneAmount;
	}
	
	public int getGlowstoneAmount()
	{
		return this.getAmplifier();
	}
	
	public String getEffectName()
	{
		return this.hasEffect() ? this.getEffect().getEffectName() : "";
	}
	
	public int getMaxAmplifier()
	{
		return this.maxAmplifier;
	}
	
	public int getMaxDuration()
	{
		return this.maxDuration;
	}
	
	public boolean isImprovable()
	{
		return this.hasEffect() ? this.getAmplifier() < this.getMaxAmplifier() : false;
	}
	
	public boolean isExtendable()
	{
		return this.hasEffect() ? this.getDuration() < this.getMaxDuration() : false;
	}
	
	public boolean isInversible()
	{
		return this.getInverted() != null;
	}
	
	public PotionType getInverted()
	{
		return this.inverted;
	}
	
	public ItemStack getIngredient()
	{
		if ((this.ingredient == null || this.ingredient.getItem() == null) && this.isDummy())
		{
			PotionType potionType = this.getEqualPotionType();
			if (potionType != this)
				this.setIngredient(potionType.getIngredient());
		}
		return this.ingredient;
	}
	
	public PotionBase getBase()
	{
		return this.base;
	}
	
	public boolean isDummy()
	{
		return this.dummy;
	}
	
	public boolean isBase()
	{
		return this instanceof PotionBase;
	}
	
	public int getLiquidColor()
	{
		Potion potion = this.getPotion();
		if (potion != null)
			return potion.getLiquidColor();
		return 0x0C0CFF;
	}
	
	public int getDefaultDuration()
	{
		PotionType potionType = this.getEqualPotionType();
		if (potionType != this)
			return potionType.getDuration();
		return this.getDuration();
	}
	
	public Map<String, IPotionAttribute> getExtendedAttributes()
	{
		return this.extendedAttributes;
	}
	
	public <T> T getExtendedAttribute(String name)
	{
		return (T) this.extendedAttributes.get(name);
	}
	
	public PotionType setEffect(PotionEffect effect)
	{
		this.effect = effect;
		return this;
	}
	
	public PotionType setMaxAmplifier(int maxAmplifier)
	{
		this.maxAmplifier = maxAmplifier;
		return this;
	}
	
	public PotionType setMaxDuration(int maxDuration)
	{
		this.maxDuration = maxDuration;
		return this;
	}
	
	public PotionType setInverted(PotionType opposite)
	{
		this.inverted = opposite;
		return this;
	}
	
	public PotionType setIngredient(ItemStack ingredient)
	{
		this.ingredient = ingredient;
		return this;
	}
	
	public PotionType setBase(PotionBase base)
	{
		this.base = base;
		return this;
	}
	
	public void setDummy(boolean dummy)
	{
		this.dummy = dummy;
	}
	
	public Object setExtendedAttribute(String name, IPotionAttribute value)
	{
		Object old = this.getExtendedAttribute(name);
		this.extendedAttributes.put(name, value);
		return old;
	}
	
	public static void registerExtendedAttribute(String name, IPotionAttribute defaultValue)
	{
		defaultExtendedAttributes.put(name, defaultValue);
	}
	
	public static void unregisterExtendedAttribute(String name)
	{
		defaultExtendedAttributes.remove(name);
	}
	
	public PotionType onImproved()
	{
		if (this.isImprovable() && this.hasEffect())
		{
			PotionEffect pe = new PotionEffect(this.getPotionID(), this.getDuration(), this.getAmplifier() + 1);
			return this.copy().setEffect(pe).setMaxDuration(pe.getDuration());
		}
		return this;
	}
	
	public PotionType onExtended()
	{
		if (this.isExtendable() && this.hasEffect())
		{
			PotionEffect pe = new PotionEffect(this.getPotionID(), this.getDuration() * 2, this.getAmplifier());
			return this.copy().setEffect(pe).setMaxAmplifier(pe.getAmplifier());
		}
		return this;
	}
	
	public PotionType onGunpowderUsed()
	{
		if (this.hasEffect())
		{
			PotionEffect pe = new PotionEffect(this.getPotionID(), (int) (this.getDuration() * 0.75D), this.getAmplifier());
			return this.copy().setEffect(pe);
		}
		return this;
	}
	
	public PotionType onInverted()
	{
		if (this.isInversible() && this.hasEffect())
		{
			PotionType inverted = this.getInverted().copy();
			if (inverted.hasEffect())
				inverted.setEffect(new PotionEffect(inverted.getPotionID(), (int) (this.getDuration() * 0.75D), this.getAmplifier()));
			return inverted;
		}
		return this;
	}
	
	/**
	 * Returns the subtypes for one Potion depending on improvability and extendability
	 * 
	 * @return
	 */
	public List<PotionType> getSubTypes()
	{
		List<PotionType> list = new ArrayList<PotionType>();
		list.add(this);
		if (this.isImprovable())
			list.add(this.onImproved());
		if (this.isExtendable())
			list.add(this.onExtended());
		return list;
	}
	
	public static PotionType getRandom(Random rng)
	{
		return combinableEffects.get(rng.nextInt(combinableEffects.size()));
	}
	
	public static PotionType getLegacyPotionType(PotionEffect potionEffect)
	{
		PotionType potionType = getPotionTypeFromPotionID(potionEffect.getPotionID());
		if (potionType != null)
		{
			potionType = potionType.copy();
			potionType.setEffect(potionEffect);
			potionType.setDummy(true);
			return potionType;
		}
		
		return new PotionType(potionEffect, potionEffect.getAmplifier(), potionEffect.getDuration()).register();
	}
	
	public PotionType getEqualPotionType()
	{
		int potionID = this.getPotionID();
		if (this.hasEffect())
		{
			for (PotionType potionType : potionTypeList)
			{
				if (!potionType.isDummy() && potionID == potionType.getPotionID())
					return potionType;
			}
		}
		return this;
	}
	
	public static PotionType getPotionTypeFromPotionID(int potionID)
	{
		for (PotionType pt : potionTypeList)
		{
			if (!pt.isDummy() && pt.getPotionID() == potionID)
			{
				return pt;
			}
		}
		return null;
	}
	
	/**
	 * Writes the PotionType to the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return ItemStack with PotionType NBT
	 */
	public ItemStack addPotionTypeToItemStack(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.stackTagCompound == null)
				stack.setTagCompound(new NBTTagCompound());
			
			if (!stack.stackTagCompound.hasKey(COMPOUND_NAME))
				stack.stackTagCompound.setTag(COMPOUND_NAME, new NBTTagList());
			
			NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag(COMPOUND_NAME);
			
			NBTTagCompound compound = new NBTTagCompound();
			this.writeToNBT(compound);
			tagList.appendTag(compound);
		}
		return stack;
	}
	
	public ItemStack removePotionTypeFromItemStack(ItemStack stack)
	{
		if (stack != null)
		{
			List<PotionType> potionTypes = ((ItemPotion2) stack.getItem()).getEffects(stack);
			
			if (stack.stackTagCompound == null)
				stack.setTagCompound(new NBTTagCompound());
			
			if (!stack.stackTagCompound.hasKey(COMPOUND_NAME))
				stack.stackTagCompound.setTag(COMPOUND_NAME, new NBTTagList());
			
			NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag(COMPOUND_NAME);
			
			for (int i = 0; i < potionTypes.size(); i++)
			{
				if (this.equals(potionTypes.get(i)))
				{
					tagList.removeTag(i);
				}
			}
		}
		return stack;
	}
	
	/**
	 * Returns the first PotionType that can be read from the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return First PotionType read from ItemStack NBT
	 */
	@Deprecated
	public static PotionType getFirstPotionType(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = stack.stackTagCompound.getTagList(COMPOUND_NAME, Constants.NBT.TAG_COMPOUND);
			if (list != null && list.tagCount() > 0)
			{
				NBTTagCompound compound = list.getCompoundTagAt(0);
				PotionType potionType = getPotionTypeFromNBT(compound);
				return potionType;
			}
		}
		return PotionList.awkward;
	}
	
	/**
	 * Checks if the stack has any effect on a potion
	 * 
	 * @param stack
	 *            the stack
	 * @return true, if the stack is a valid potion ingredient
	 */
	public static boolean isPotionIngredient(ItemStack stack)
	{
		return getPotionTypeFromIngredient(stack) != null || hasIngredientHandler(stack);
	}
	
	/**
	 * Finds the first registered {@link IIngredientHandler} that can handle the ingredient
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @return the corresponding {@link IIngredientHandler}
	 */
	public static IIngredientHandler getHandlerForIngredient(ItemStack ingredient)
	{
		for (IIngredientHandler handler : BrewingAPI.ingredientHandlers)
			if (handler.canHandleIngredient(ingredient))
				return handler;
		return null;
	}
	
	/**
	 * Checks if the ingredient needs an IngredientHandler to be applied to a potion
	 * 
	 * @param ingredient
	 * @return
	 */
	public static boolean hasIngredientHandler(ItemStack ingredient)
	{
		return getHandlerForIngredient(ingredient) != null;
	}
	
	/**
	 * Applys an ingredient to a potion
	 * 
	 * @param ingredient
	 *            Ingredient
	 * @param potion
	 *            Potion
	 * @return Potion with applied ingredients
	 */
	public static ItemStack applyIngredient(ItemStack ingredient, ItemStack potion)
	{
		IIngredientHandler handler = getHandlerForIngredient(ingredient);
		if (handler != null && handler.canApplyIngredient(ingredient, potion))
			return handler.applyIngredient(ingredient, potion);
		
		PotionType potionType = getPotionTypeFromIngredient(ingredient);
		if (potionType != null)
		{
			PotionBase requiredBase = potionType.getBase();
			boolean flag = false;
			
			List<PotionType> potionTypes = ((ItemPotion2) potion.getItem()).getEffects(potion);
			
			if (requiredBase == null)
				flag = potionTypes.isEmpty();
			else
			{
				for (PotionType pt : potionTypes)
				{
					if (pt instanceof PotionBase)
					{
						String basename = ((PotionBase) pt).basename;
						if (basename.equals(requiredBase.basename))
						{
							flag = true;
							pt.removePotionTypeFromItemStack(potion);
							break;
						}
					}
				}
			}
			
			if (flag)
				return potionType.addPotionTypeToItemStack(potion);
		}
		return potion;
	}
	
	public static boolean canApplyIngredient(ItemStack ingredient, ItemStack potion)
	{
		IIngredientHandler handler = getHandlerForIngredient(ingredient);
		
		if (handler != null)
			return handler.canApplyIngredient(ingredient, potion);
		else if (getPotionTypeFromIngredient(ingredient) != null)
		{
			PotionType potionType = getPotionTypeFromIngredient(ingredient);
			PotionBase requiredBase = potionType.getBase();
			
			List<PotionType> potionTypes = ((ItemPotion2) potion.getItem()).getEffects(potion);
			
			if (requiredBase == null)
				return potionTypes.isEmpty();
			else
				for (PotionType pt : potionTypes)
				{
					if (pt instanceof PotionBase)
					{
						String basename = ((PotionBase) pt).basename;
						if (basename.equals(requiredBase.basename))
							return true;
					}
				}
		}
		return false;
	}
	
	/**
	 * Returns a PotionType that is brewed with the itemstack. it doesn't check for the amount.
	 * Ignores Special Ingredient Handlers.
	 * 
	 * @param ingredient
	 * @return PotionType that is brewed with the ItemStack
	 */
	public static PotionType getPotionTypeFromIngredient(ItemStack ingredient)
	{
		if (ingredient != null)
		{
			for (PotionType pt : potionTypeList)
			{
				if (pt.getIngredient() != null)
				{
					// Ore Dictionary
					if (OreDictionary.itemMatches(pt.getIngredient(), ingredient, true))
						return pt;
					if (pt.getIngredient().getItem() == ingredient.getItem() && pt.getIngredient().getItemDamage() == ingredient.getItemDamage())
						return pt;
				}
			}
		}
		return null;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("VERSION", NBT_VERSION);
		if ("1.1".equals(NBT_VERSION))
		{
			if (this.effect != null)
			{
				NBTTagCompound effect = new NBTTagCompound();
				this.effect.writeCustomPotionEffectToNBT(effect);
				nbt.setTag("Effect", effect);
			}
			if (this.maxAmplifier > 0)
				nbt.setInteger("MaxAmplifier", this.maxAmplifier);
			if (this.effect == null || this.maxDuration > this.effect.getDuration())
				nbt.setInteger("MaxDuration", this.maxDuration);
			if (this.inverted != null)
			{
				NBTTagCompound inverted = new NBTTagCompound();
				this.inverted.writeToNBT(inverted);
				nbt.setTag("Inverted", inverted);
			}
			if (this.extendedAttributes != null)
			{
				NBTTagList list = new NBTTagList();
				for (String s : this.extendedAttributes.keySet())
				{
					NBTTagCompound compound = new NBTTagCompound();
					this.extendedAttributes.get(s).writeToNBT(compound);
					list.appendTag(compound);
				}
				nbt.setTag("ExtendedAttributes", list);
			}
		}
		else
		{
			if (this.effect != null)
			{
				if (this.effect.getPotionID() > 0)
					nbt.setInteger("PotionID", this.effect.getPotionID());
				if (this.effect.getDuration() > 0)
					nbt.setInteger("PotionDuration", this.effect.getDuration());
				if (this.effect.getAmplifier() > 0)
					nbt.setInteger("PotionAmplifier", this.effect.getAmplifier());
			}
			if (this.maxAmplifier > 0)
				nbt.setInteger("MaxAmplifier", this.maxAmplifier);
			if (this.effect == null || this.maxDuration > this.effect.getDuration())
				nbt.setInteger("MaxDuration", this.maxDuration);
			if (this.inverted != null)
			{
				NBTTagCompound inverted = new NBTTagCompound();
				this.inverted.writeToNBT(inverted);
				nbt.setTag("Opposite", inverted);
			}
			if (this.extendedAttributes != null)
			{
				NBTTagList list = new NBTTagList();
				for (String s : this.extendedAttributes.keySet())
				{
					NBTTagCompound compound = new NBTTagCompound();
					this.extendedAttributes.get(s).writeToNBT(compound);
					list.appendTag(compound);
				}
				nbt.setTag("ExtendedAttributes", list);
			}
		}
	}
	
	public static PotionType getPotionTypeFromNBT(NBTTagCompound nbt)
	{
		if (nbt != null && !nbt.hasNoTags())
		{
			PotionType result;
			if (nbt.hasKey("BaseName"))
				result = new PotionBase();
			else
				result = new PotionType();
			result.readFromNBT(nbt);
			result.setDummy(true);
			return result;
		}
		return null;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		String nbtVersion = nbt.getString("VERSION");
		
		if ("1.1".equals(nbtVersion))
		{
			this.effect = PotionEffect.readCustomPotionEffectFromNBT(nbt.getCompoundTag("Effect"));
			
			this.maxAmplifier = nbt.getInteger("MaxAmplifier");
			this.maxDuration = nbt.getInteger("MaxDuration");
			
			this.ingredient = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Ingredient"));
			
			this.inverted = getPotionTypeFromNBT(nbt.getCompoundTag("Inverted"));
			this.base = (PotionBase) getPotionTypeFromNBT(nbt.getCompoundTag("Base"));
			
			if (nbt.hasKey("ExtendedAttributes"))
			{
				NBTTagList list = nbt.getTagList("ExtendedAttributes", Constants.NBT.TAG_COMPOUND);
				
				for (int i = 0; i < list.tagCount(); i++)
				{
					NBTTagCompound compound = list.getCompoundTagAt(i);
					if (compound != null)
					{
						for (IPotionAttribute attribute : defaultExtendedAttributes.values())
						{
							IPotionAttribute attribute1 = attribute.clone();
							attribute1.readFromNBT(nbt);
							this.setExtendedAttribute(attribute1.getName(), attribute1);
						}
					}
				}
			}
		}
		else
		{
			int potionID = nbt.hasKey("PotionID") ? nbt.getInteger("PotionID") : 0;
			int potionDuration = nbt.hasKey("PotionDuration") ? nbt.getInteger("PotionDuration") : 0;
			int potionAmplifier = nbt.hasKey("PotionAmplifier") ? nbt.getInteger("PotionAmplifier") : 0;
			
			this.setEffect(new PotionEffect(potionID, potionDuration, potionAmplifier));
			
			this.maxAmplifier = nbt.hasKey("MaxAmplifier") ? nbt.getInteger("MaxAmplifier") : (nbt.hasKey("Improvable") ? (nbt.getBoolean("Improvable") ? 1 : 0) : 0);
			this.maxDuration = nbt.hasKey("MaxDuration") ? nbt.getInteger("MaxDuration") : (nbt.hasKey("Extendable") ? (nbt.getBoolean("Extendable") ? potionDuration * 2 : potionDuration) : potionDuration);
			
			int ingredientID = nbt.getInteger("IngredientID");
			int ingredientAmount = nbt.getInteger("IngredientAmount");
			int ingredientDamage = nbt.getInteger("IngredientDamage");
			
			this.ingredient = new ItemStack(Item.getItemById(ingredientID), ingredientAmount, ingredientDamage);
			
			this.inverted = getPotionTypeFromNBT(nbt.getCompoundTag("Opposite"));
			this.base = (PotionBase) getPotionTypeFromNBT(nbt.getCompoundTag("Base"));
			
			if (nbt.hasKey("ExtendedAttributes"))
			{
				NBTTagList list = nbt.getTagList("ExtendedAttributes", Constants.NBT.TAG_COMPOUND);
				
				for (int i = 0; i < list.tagCount(); i++)
				{
					NBTTagCompound compound = list.getCompoundTagAt(i);
					if (compound != null)
					{
						for (IPotionAttribute attribute : defaultExtendedAttributes.values())
						{
							IPotionAttribute attribute1 = attribute.clone();
							attribute1.readFromNBT(nbt);
							this.setExtendedAttribute(attribute1.getName(), attribute1);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the experience given for brewing this potion
	 * 
	 * @param stack
	 *            potion
	 * @return Experience float
	 */
	public static float getExperience(ItemStack stack)
	{
		if (stack != null && stack.getItem() instanceof ItemPotion2)
		{
			List<PotionType> effects = ((ItemPotion2) stack.getItem()).getEffects(stack);
			float value = ((ItemPotion2) stack.getItem()).isSplash(stack.getItemDamage()) ? 0.3F : 0.2F;
			for (PotionType b : effects)
			{
				if (b.hasEffect())
				{
					float f1 = b.isBadEffect() ? 0.4F : 0.5F;
					value += f1 + (b.getAmplifier() * 0.1F) + (b.getEffect().getDuration() / 600);
				}
			}
			return value;
		}
		return 0F;
	}
	
	/**
	 * Used to determine if it should use the actual Base or awkward when turned off in the config
	 * 
	 * @param base
	 *        PotionType Base
	 * @return base or awkward
	 */
	public static PotionBase getBaseBrewing(PotionBase base)
	{
		if (PotionList.DEFAULT_AWKWARD_BREWING)
			return PotionList.awkward;
		return base;
	}
	
	public static Collection<PotionType> removeDuplicates(Collection<PotionType> list)
	{
		if (list != null && list.size() > 0)
		{
			List<PotionType> result = new ArrayList<PotionType>();
			for (PotionType b : list)
			{
				boolean duplicate = false;
				for (PotionType b2 : result)
				{
					if (b.hasEffect() && b2.hasEffect() && b.getPotionID() == b2.getPotionID())
					{
						duplicate = true;
						break;
					}
				}
				if (!duplicate)
					result.add(b);
			}
			Collections.sort(result);
			return result;
		}
		return list;
	}
	
	public static List<PotionType> removeDuplicates(PotionType[] list)
	{
		if (list != null && list.length > 0)
		{
			List<PotionType> result = new ArrayList<PotionType>();
			for (PotionType b : list)
			{
				boolean duplicate = false;
				for (PotionType b2 : result)
				{
					if (b.getPotionID() == b2.getPotionID())
					{
						duplicate = true;
						break;
					}
				}
				if (!duplicate)
					result.add(b);
			}
			return result;
		}
		return new LinkedList<PotionType>();
	}
	
	@Override
	public int compareTo(PotionType o)
	{
		return (this.hasEffect() && o.hasEffect()) ? Integer.compare(this.getPotionID(), o.getPotionID()) : 0;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.base == null) ? 0 : this.base.hashCode());
		result = prime * result + (this.dummy ? 1231 : 1237);
		result = prime * result + ((this.effect == null) ? 0 : this.effect.hashCode());
		result = prime * result + ((this.extendedAttributes == null) ? 0 : this.extendedAttributes.hashCode());
		result = prime * result + ((this.ingredient == null) ? 0 : this.ingredient.hashCode());
		result = prime * result + ((this.inverted == null) ? 0 : this.inverted.hashCode());
		result = prime * result + this.maxAmplifier;
		result = prime * result + this.maxDuration;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		PotionType other = (PotionType) obj;
		if (this.base == null)
		{
			if (other.base != null)
				return false;
		}
		else if (!this.base.equals(other.base))
			return false;
		if (this.dummy != other.dummy)
			return false;
		if (this.effect == null)
		{
			if (other.effect != null)
				return false;
		}
		else if (!this.effect.equals(other.effect))
			return false;
		if (this.extendedAttributes == null)
		{
			if (other.extendedAttributes != null)
				return false;
		}
		else if (!this.extendedAttributes.equals(other.extendedAttributes))
			return false;
		if (this.ingredient == null)
		{
			if (other.ingredient != null)
				return false;
		}
		else if (!this.ingredient.equals(other.ingredient))
			return false;
		if (this.inverted == null)
		{
			if (other.inverted != null)
				return false;
		}
		else if (!this.inverted.equals(other.inverted))
			return false;
		if (this.maxAmplifier != other.maxAmplifier)
			return false;
		if (this.maxDuration != other.maxDuration)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("PotionType {");
		if (this.hasEffect())
			result.append("PotionEffect=[").append(this.getPotionID()).append(":").append(this.getAmplifier()).append("*").append(this.getDuration()).append("]");
		result.append("&MaxAmplifier=[").append(this.maxAmplifier).append("]");
		result.append("&MaxDuration=[").append(this.maxDuration).append("]");
		if (this.getInverted() != null)
			result.append("&Inverted=[").append(this.getInverted().toString()).append("]");
		ItemStack ingredient = this.getIngredient();
		if (ingredient != null)
			result.append("&Ingredient=[").append(ingredient).append("]");
		if (this.getBase() != null)
			result.append("&Base=[").append(this.getBase().toString()).append("]");
		result.append("&Loaded=").append(this.isDummy());
		result.append("}");
		return result.toString();
	}
}
