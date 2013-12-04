package clashsoft.brewingapi.brewing;

import java.util.*;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.api.IBrewingAttribute;
import clashsoft.brewingapi.api.IIngredientHandler;
import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class that stores all data that the new potion need and all new potion types
 * 
 * @author Clashsoft
 */
public class PotionType implements Comparable<PotionType>
{
	/** List that stores ALL potionTypes **/
	public static List<PotionType>					potionTypeList				= new ArrayList<PotionType>();
	public static Map<PotionEffect, PotionType>		effectMap					= new HashMap();
	
	public static List<PotionType>					combinableEffects			= new ArrayList<PotionType>();
	
	/** Version identifier for NBTs. **/
	public static String							NBTVersion					= "1.1";
	
	public static Map<String, IBrewingAttribute>	defaultExtendedAttributes	= new HashMap<String, IBrewingAttribute>();
	
	/** The effect **/
	private PotionEffect							effect;
	/** Max effect amplifier **/
	private int										maxAmplifier;
	/** Max effect duration **/
	private int										maxDuration;
	/** Fermented Spider Eye **/
	private PotionType								inverted					= null;
	/** The ingredient to brew the potion **/
	private ItemStack								ingredient					= new ItemStack(0, 0, 0);
	/** Determines the base that is needed to brew the potion **/
	private PotionBase								base;
	
	private Map<String, IBrewingAttribute>			extendedAttributes			= new HashMap<String, IBrewingAttribute>();
	
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
	 *            the base
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
		this.inverted = inverted != null ? inverted.copy() : null;
		this.ingredient = ingredient;
		this.base = base;
	}
	
	public PotionType copy()
	{
		PotionType potionType = new PotionType(this.getEffect(), this.getMaxAmplifier(), this.getMaxDuration(), this.getIngredient(), this.getBase());
		potionType.extendedAttributes = new HashMap(this.extendedAttributes);
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
		
		if (!this.isBase() && this != PotionList.effectRemove)
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
				if (this.getPotion() instanceof clashsoft.cslib.minecraft.CustomPotion)
				{
					return ((clashsoft.cslib.minecraft.CustomPotion) this.getPotion()).isBadEffect();
				}
			}
		}
		return false;
	}
	
	public PotionEffect getEffect()
	{
		return this.effect;
	}
	
	public boolean hasEffect()
	{
		return this.getEffect() != null && this.getEffect().getPotionID() > 0;
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
		return this.effect != null ? this.effect.getAmplifier() < this.maxAmplifier : false;
	}
	
	public boolean isExtendable()
	{
		return this.effect != null ? this.effect.getDuration() < this.maxDuration : false;
	}
	
	public PotionType getInverted()
	{
		return this.inverted;
	}
	
	public ItemStack getIngredient()
	{
		return this.ingredient;
	}
	
	public PotionBase getBase()
	{
		return this.base;
	}
	
	public boolean isBase()
	{
		return this instanceof PotionBase;
	}
	
	public int getLiquidColor()
	{
		if (this.hasEffect())
		{
			if (this.getEffect().getPotionID() < Potion.potionTypes.length && Potion.potionTypes[this.getEffect().getPotionID()] != null)
			{
				return Potion.potionTypes[this.getEffect().getPotionID()].getLiquidColor();
			}
		}
		return 0x0C0CFF;
	}
	
	public int getDefaultDuration()
	{
		for (PotionType b : PotionType.potionTypeList)
		{
			if (this.hasEffect() && b.hasEffect() && b.getEffect().getPotionID() == this.getEffect().getPotionID())
			{
				return b.getEffect().getDuration();
			}
		}
		return this.getEffect().getDuration();
	}
	
	public Map<String, IBrewingAttribute> getExtendedAttributes()
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
	
	public PotionType setOpposite(PotionType opposite)
	{
		this.inverted = opposite;
		return this;
	}
	
	public PotionType setIngredient(ItemStack ingredient)
	{
		this.ingredient = ingredient;
		return this;
	}
	
	public PotionType setBase(PotionBase brewingbase)
	{
		this.base = brewingbase;
		return this;
	}
	
	public Object setExtendedAttribute(String name, IBrewingAttribute value)
	{
		Object old = this.getExtendedAttribute(name);
		this.extendedAttributes.put(name, value);
		return old;
	}
	
	public static void registerExtendedAttribute(String name, IBrewingAttribute defaultValue)
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
			PotionEffect pe = new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration(), this.getEffect().getAmplifier() + 1);
			return this.copy().setEffect(pe).setMaxDuration(pe.getDuration());
		}
		return this;
	}
	
	public PotionType onExtended()
	{
		if (this.isExtendable() && this.hasEffect())
		{
			PotionEffect pe = new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration() * 2, this.getEffect().getAmplifier());
			return this.copy().setEffect(pe).setMaxAmplifier(pe.getAmplifier());
		}
		return this;
	}
	
	/**
	 * Returns the subtypes for one Potion depending on improvability and
	 * extendability
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
		for (PotionType type : potionTypeList)
		{
			if (type.getPotionID() == potionEffect.getPotionID())
				return type.copy().setEffect(potionEffect);
		}
		return new PotionType(potionEffect, potionEffect.getAmplifier(), potionEffect.getDuration()).register();
	}
	
	/**
	 * Writes the PotionType to the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return ItemStack with PotionType NBT
	 */
	public ItemStack addBrewingToItemStack(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.stackTagCompound == null)
				stack.setTagCompound(new NBTTagCompound());
			
			if (!stack.stackTagCompound.hasKey("Brewing"))
				stack.stackTagCompound.setTag("Brewing", new NBTTagList());
			
			NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag("Brewing");
			
			NBTTagCompound compound = new NBTTagCompound();
			this.writeToNBT(compound);
			tagList.appendTag(compound);
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
	public static PotionType getFirstBrewing(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = stack.stackTagCompound.getTagList("Brewing");
			if (list != null && list.tagCount() > 0)
			{
				NBTTagCompound compound = (NBTTagCompound) list.tagAt(0);
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
		return getBrewingFromIngredient(stack) != null || hasIngredientHandler(stack);
	}
	
	/**
	 * Finds the first registered {@link IIngredientHandler} that can handle the
	 * ingredient
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
	 * Checks if the ingredient needs an IngredientHandler to be applied to a
	 * potion
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
		if (getBrewingFromIngredient(ingredient) != null)
			// The ingredient is a normal ingredient or a base ingredient
			return getBrewingFromIngredient(ingredient).addBrewingToItemStack(potion);
		else
			// The ingredient is a special one that must NOT only add a
			// PotionType
			// NBT
			// to the ItemStack NBT
			return getHandlerForIngredient(ingredient).applyIngredient(ingredient, potion);
	}
	
	/**
	 * Returns a brewing that is brewed with the itemstack. it doesn't check for
	 * the amount. Ignores Special Ingredient Handlers
	 * 
	 * @param stack
	 * @return PotionType that is brewed with the ItemStack
	 */
	public static PotionType getBrewingFromIngredient(ItemStack stack)
	{
		if (stack != null)
		{
			PotionBase base = PotionBase.getBrewingBaseFromIngredient(stack);
			if (base != null)
				return base;
			for (PotionType b : potionTypeList)
			{
				if (b.getIngredient() != null)
				{
					// Ore Dictionary
					if (OreDictionary.itemMatches(b.getIngredient(), stack, true))
						return b;
					if (b.getIngredient().getItem() == stack.getItem() && b.getIngredient().getItemDamage() == stack.getItemDamage())
						return b;
				}
			}
		}
		return null;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("VERSION", NBTVersion);
		if ("1.1".equals(NBTVersion))
		{
			if (this.effect != null)
			{
				NBTTagCompound effect = new NBTTagCompound();
				this.effect.writeCustomPotionEffectToNBT(effect);
				nbt.setCompoundTag("Effect", effect);
			}
			if (this.maxAmplifier > 0)
				nbt.setInteger("MaxAmplifier", this.maxAmplifier);
			if (this.effect == null || this.maxDuration > this.effect.getDuration())
				nbt.setInteger("MaxDuration", this.maxDuration);
			if (this.inverted != null)
			{
				NBTTagCompound inverted = new NBTTagCompound();
				this.inverted.writeToNBT(inverted);
				nbt.setCompoundTag("Inverted", inverted);
			}
			if (this.extendedAttributes != null)
			{
				NBTTagList list = new NBTTagList();
				for (String s : this.extendedAttributes.keySet())
				{
					list.appendTag(this.extendedAttributes.get(s).toNBT());
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
				nbt.setCompoundTag("Opposite", inverted);
			}
			if (this.extendedAttributes != null)
			{
				NBTTagList list = new NBTTagList();
				for (String s : this.extendedAttributes.keySet())
				{
					list.appendTag(this.extendedAttributes.get(s).toNBT());
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
				NBTTagList list = nbt.getTagList("ExtendedAttributes");
				
				for (int i = 0; i < list.tagCount(); i++)
				{
					NBTBase nbtbase = list.tagAt(i);
					if (nbtbase != null)
					{
						for (IBrewingAttribute attribute : defaultExtendedAttributes.values())
						{
							attribute = attribute.fromNBT(nbtbase);
							this.setExtendedAttribute(attribute.getName(), attribute);
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
			
			this.ingredient = new ItemStack(ingredientID, ingredientAmount, ingredientDamage);
			
			this.inverted = getPotionTypeFromNBT(nbt.getCompoundTag("Opposite"));
			this.base = (PotionBase) getPotionTypeFromNBT(nbt.getCompoundTag("Base"));
			
			if (nbt.hasKey("ExtendedAttributes"))
			{
				NBTTagList list = nbt.getTagList("ExtendedAttributes");
				
				for (int i = 0; i < list.tagCount(); i++)
				{
					NBTBase nbtbase = list.tagAt(i);
					if (nbtbase != null)
					{
						for (IBrewingAttribute attribute : defaultExtendedAttributes.values())
						{
							attribute = attribute.fromNBT(nbtbase);
							this.setExtendedAttribute(attribute.getName(), attribute);
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
	 * Used to determine if it should use the actual Base or awkward when turned
	 * off in the config
	 * 
	 * @param base
	 *            PotionType Base
	 * @return base or awkward
	 */
	public static PotionBase getBaseBrewing(PotionBase base)
	{
		if (PotionList.DEFAULT_AWKWARD_BREWING)
			return PotionList.awkward;
		return base;
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("PotionType {");
		if (this.hasEffect())
			result.append("PotionEffect=[").append(this.getPotionID()).append(":").append(this.getAmplifier()).append("*").append(this.getDuration() / 20F).append("s").append("]");
		result.append("&MaxAmplifier=[").append(this.maxAmplifier).append("]");
		result.append("&MaxDuration=[").append(this.maxDuration).append("]");
		if (this.getInverted() != null)
			result.append("&Inverted=[").append(this.getInverted().toString()).append("]");
		if (this.getIngredient() != null)
			result.append("&Ingredient=[").append(this.getIngredient().itemID).append(":").append(this.getIngredient().getItemDamage()).append("]");
		if (this.getBase() != null)
			result.append("&Base=[").append(this.getBase().toString()).append("]");
		result.append("}");
		return result.toString();
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
}
