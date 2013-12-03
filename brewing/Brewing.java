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
public class Brewing implements Comparable<Brewing>
{
	/** List that stores ALL brewings **/
	public static List<Brewing>						brewingList					= new ArrayList<Brewing>();
	/** List that stores all brewings with good effects **/
	public static List<Brewing>						goodEffects					= new ArrayList<Brewing>();
	/** List that stores all brewings with bad effects **/
	public static List<Brewing>						badEffects					= new ArrayList<Brewing>();
	/**
	 * List that stores brewings that can be used together with other brewings
	 * (e.g. Effect Remover potion is not inside)
	 **/
	public static List<Brewing>						combinableEffects			= new ArrayList<Brewing>();
	/** List that stores all base brewing (e.g. awkward, mundane, ...) **/
	public static List<BrewingBase>					baseBrewings				= new ArrayList<BrewingBase>();
	/** List that stores all brewings with effects **/
	public static List<Brewing>						effectBrewings				= new ArrayList<Brewing>();
	
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
	private Brewing									opposite					= null;
	/** The ingredient to brew the potion **/
	private ItemStack								ingredient					= new ItemStack(0, 0, 0);
	/** Used by the random potion **/
	private boolean									isRandom					= false;
	/** Determines the base that is needed to brew the potion **/
	private BrewingBase								base;
	
	private Map<String, IBrewingAttribute>			extendedAttributes			= new HashMap<String, IBrewingAttribute>();
	
	/**
	 * Empty constructor for use with serialization
	 */
	public Brewing()
	{}
	
	/**
	 * Creates a new Brewing
	 * 
	 * @param effect
	 *            the effect
	 * @param maxAmplifier
	 *            the max amplifier
	 * @param maxDuration
	 *            the max duration
	 */
	public Brewing(PotionEffect effect, int maxAmplifier, int maxDuration)
	{
		this(effect, maxAmplifier, maxDuration, (Brewing) null);
	}
	
	/**
	 * Creates a new Brewing
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
	public Brewing(PotionEffect effect, int maxAmplifier, int maxDuration, Brewing inverted)
	{
		this(effect, maxAmplifier, maxDuration, inverted, (ItemStack) null, (BrewingBase) null);
	}
	
	/**
	 * Creates a new Brewing
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
	public Brewing(PotionEffect effect, int maxAmplifier, int maxDuration, ItemStack ingredient, BrewingBase base)
	{
		this(effect, maxAmplifier, maxDuration, (Brewing) null, ingredient, base);
	}
	
	/**
	 * Creates a new Brewing with Opposite, Ingredient and Base
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
	public Brewing(PotionEffect effect, int maxAmplifier, int maxDuration, Brewing inverted, ItemStack ingredient, BrewingBase base)
	{
		this.effect = effect;
		this.maxAmplifier = maxAmplifier;
		this.maxDuration = maxDuration;
		this.opposite = inverted;
		this.ingredient = ingredient;
		this.base = base;
	}
	
	public Brewing copy()
	{
		Brewing brewing = new Brewing(this.getEffect(), this.maxAmplifier, this.maxDuration, this.ingredient, this.base);
		brewing.extendedAttributes = new HashMap(this.extendedAttributes);
		return brewing;
	}
	
	/**
	 * Determines if the effect is a bad effect (red color)
	 */
	public boolean isBadEffect()
	{
		if (this.getEffect() != null)
		{
			switch (this.getEffect().getPotionID())
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
				if (Potion.potionTypes[this.getEffect().getPotionID()] instanceof clashsoft.cslib.minecraft.CustomPotion)
				{
					return ((clashsoft.cslib.minecraft.CustomPotion) Potion.potionTypes[this.getEffect().getPotionID()]).isBadEffect();
				}
			}
		}
		return false;
	}
	
	public PotionEffect getEffect()
	{
		return this.effect;
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
	
	public Brewing getInverted()
	{
		if (this.opposite != null && this.opposite.getEffect() != null)
		{
			PotionEffect pe = new PotionEffect(this.opposite.getEffect().getPotionID(), this.getEffect().getDuration() / 2, this.isImprovable() ? this.getEffect().getAmplifier() : 0);
			return this.opposite.copy().setEffect(pe);
		}
		return this.opposite;
	}
	
	public ItemStack getIngredient()
	{
		return this.ingredient;
	}
	
	public boolean isRandom()
	{
		return this.isRandom;
	}
	
	public BrewingBase getBase()
	{
		return this.base;
	}
	
	public boolean isBase()
	{
		return this instanceof BrewingBase || this.getEffect() == null;
	}
	
	public int getLiquidColor()
	{
		if (this.getEffect() != null)
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
		for (Brewing b : Brewing.brewingList)
		{
			if (this.getEffect() != null && b.getEffect() != null && b.getEffect().getPotionID() == this.getEffect().getPotionID())
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
	
	public Brewing setEffect(PotionEffect effect)
	{
		this.effect = effect;
		return this;
	}
	
	public Brewing setMaxAmplifier(int maxAmplifier)
	{
		this.maxAmplifier = maxAmplifier;
		return this;
	}
	
	public Brewing setMaxDuration(int maxDuration)
	{
		this.maxDuration = maxDuration;
		return this;
	}
	
	public Brewing setOpposite(Brewing opposite)
	{
		this.opposite = opposite;
		return this;
	}
	
	public Brewing setIngredient(ItemStack ingredient)
	{
		this.ingredient = ingredient;
		return this;
	}
	
	public Brewing setIsRandom(boolean random)
	{
		this.isRandom = random;
		return this;
	}
	
	public Brewing setBase(BrewingBase brewingbase)
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
	
	public Brewing onImproved()
	{
		if (this.isImprovable() && this.getEffect() != null)
		{
			PotionEffect pe = new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration(), this.getEffect().getAmplifier() + 1);
			return this.copy().setEffect(pe).setMaxDuration(pe.getDuration());
		}
		return this;
	}
	
	public Brewing onExtended()
	{
		if (this.isExtendable() && this.getEffect() != null)
		{
			PotionEffect pe = new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration() * 2, this.getEffect().getAmplifier());
			return this.copy().setEffect(pe).setMaxAmplifier(pe.getAmplifier());
		}
		return this;
	}
	
	/**
	 * Sorts the Brewing in the lists
	 * 
	 * @return Brewing
	 */
	public Brewing register()
	{
		brewingList.add(this);
		if (this.getEffect() != null && this.getEffect().getPotionID() > 0)
		{
			if (this.isBadEffect())
			{
				badEffects.add(this);
			}
			else
			{
				goodEffects.add(this);
			}
		}
		if (!this.isBase() && !(this instanceof BrewingBase))
		{
			effectBrewings.add(this);
		}
		if (!(this instanceof BrewingBase) && this != BrewingList.effectRemove && this != BrewingList.random)
		{
			combinableEffects.add(this);
		}
		if (this instanceof BrewingBase)
		{
			baseBrewings.add((BrewingBase) this);
		}
		
		return this;
	}
	
	/**
	 * Returns the subtypes for one Potion depending on improvability and
	 * extendability
	 * 
	 * @return
	 */
	public List<Brewing> getSubTypes()
	{
		List<Brewing> list = new ArrayList<Brewing>();
		list.add(this);
		if (this.isImprovable())
			list.add(this.onImproved());
		if (this.isExtendable())
			list.add(this.onExtended());
		return list;
	}
	
	/**
	 * Writes the Brewing to the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return ItemStack with Brewing NBT
	 */
	public ItemStack addBrewingToItemStack(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.stackTagCompound == null)
				stack.setTagCompound(new NBTTagCompound());
			
			if (!stack.stackTagCompound.hasKey("Brewing"))
				stack.stackTagCompound.setTag("Brewing", new NBTTagList("Brewing"));
			
			NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag("Brewing");
			NBTTagCompound compound = new NBTTagCompound();
			this.writeToNBT(compound);
			tagList.appendTag(compound);
		}
		return stack;
	}
	
	/**
	 * Returns the first Brewing that can be read from the ItemStack NBT
	 * 
	 * @param stack
	 *            the stack
	 * @return First Brewing read from ItemStack NBT
	 */
	public static Brewing getFirstBrewing(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = stack.stackTagCompound.getTagList("Brewing");
			if (list != null && list.tagCount() > 0)
			{
				NBTTagCompound compound = (NBTTagCompound) list.tagAt(0);
				Brewing brewing = new Brewing();
				brewing.readFromNBT(compound);
				return brewing;
			}
		}
		return BrewingList.awkward;
	}
	
	/**
	 * Checks if the ingredient has any effect on a potion
	 * 
	 * @param ingredient
	 * @return
	 */
	public static boolean isPotionIngredient(ItemStack ingredient)
	{
		if (isSpecialIngredient(ingredient))
			return true;
		return getBrewingFromIngredient(ingredient) != null;
	}
	
	/**
	 * Finds the first registered IngredientHandler that can handle the
	 * ingredient
	 * 
	 * @param ingredient
	 * @return
	 */
	public static IIngredientHandler getHandlerForIngredient(ItemStack ingredient)
	{
		for (IIngredientHandler handler : BrewingAPI.ingredientHandlers)
		{
			if (handler.canHandleIngredient(ingredient))
			{
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * Checks if the ingredient needs an IngredientHandler to be applied to a
	 * potion
	 * 
	 * @param ingredient
	 * @return
	 */
	public static boolean isSpecialIngredient(ItemStack ingredient)
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
		// The ingredient is a normal ingredient or a base ingredient
		if (getBrewingFromIngredient(ingredient) != null)
		{
			return getBrewingFromIngredient(ingredient).addBrewingToItemStack(potion);
		}
		else
		// The ingredient is a special one that must NOT only add a Brewing NBT
		// to the ItemStack NBT
		{
			return getHandlerForIngredient(ingredient).applyIngredient(ingredient, potion);
		}
	}
	
	/**
	 * Returns a brewing that is brewed with the itemstack. it doesn't check for
	 * the amount. Ignores Special Ingredient Handlers
	 * 
	 * @param stack
	 * @return Brewing that is brewed with the ItemStack
	 */
	public static Brewing getBrewingFromIngredient(ItemStack stack)
	{
		if (stack != null)
		{
			BrewingBase base = BrewingBase.getBrewingBaseFromIngredient(stack);
			if (base != null)
				return base;
			for (Brewing b : brewingList)
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
			if (this.opposite != null)
			{
				NBTTagCompound inverted = new NBTTagCompound();
				this.opposite.writeToNBT(inverted);
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
			if (this.opposite != null)
			{
				NBTTagCompound inverted = new NBTTagCompound();
				this.opposite.writeToNBT(inverted);
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
	
	/**
	 * Reads a Brewing from a NBTTagCompound
	 * 
	 * @param nbt
	 *            NBTTagCompound to read Brewing from
	 * @return Brewing read from NBTTagCompound
	 */
	public void readFromNBT(NBTTagCompound nbt)
	{
		
		String nbtVersion = nbt.getString("VERSION");
		
		if ("1.1".equals(nbtVersion))
		{
			PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(nbt.getCompoundTag("Effect"));
			
			if (effect.getPotionID() == 0 || nbt.hasKey("BaseName"))
				((BrewingBase)this).readFromNBT(nbt);
			
			int maxAmplifier = nbt.getInteger("MaxAmplifier");
			int maxDuration = nbt.getInteger("MaxDuration");
			
			ItemStack ingredient = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Ingredient"));
			
			Brewing inverted = new Brewing();
			inverted.readFromNBT(nbt.getCompoundTag("Inverted"));
			
			BrewingBase base = new BrewingBase();
			base.readFromNBT(nbt.getCompoundTag("Base"));
			
			Brewing brewing = new Brewing(effect, maxAmplifier, maxDuration, inverted, ingredient, base);
			
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
							brewing.setExtendedAttribute(attribute.getName(), attribute);
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
			
			if (potionID == 0 || nbt.hasKey("BaseName"))
				((BrewingBase)this).readFromNBT(nbt);
			
			this.maxAmplifier = nbt.hasKey("MaxAmplifier") ? nbt.getInteger("MaxAmplifier") : (nbt.hasKey("Improvable") ? (nbt.getBoolean("Improvable") ? 1 : 0) : 0);
			this.maxDuration = nbt.hasKey("MaxDuration") ? nbt.getInteger("MaxDuration") : (nbt.hasKey("Extendable") ? (nbt.getBoolean("Extendable") ? potionDuration * 2 : potionDuration) : potionDuration);
			
			int ingredientID = nbt.getInteger("IngredientID");
			int ingredientAmount = nbt.getInteger("IngredientAmount");
			int ingredientDamage = nbt.getInteger("IngredientDamage");
			
			Brewing inverted = new Brewing();
			inverted.readFromNBT(nbt.getCompoundTag("Opposite"));
			BrewingBase base = new BrewingBase();
			base.readFromNBT(nbt.getCompoundTag("Base"));
			
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
			List<Brewing> effects = ((ItemPotion2) stack.getItem()).getEffects(stack);
			float f = ((ItemPotion2) stack.getItem()).isSplash(stack.getItemDamage()) ? 0.3F : 0.2F;
			for (Brewing b : effects)
			{
				if (b.getEffect() != null)
				{
					if (b.isBadEffect())
					{
						f += 0.4F + b.getEffect().getAmplifier() * 0.1F + (b.getEffect().getDuration() / (600));
					}
					else
					{
						f += 0.5F + b.getEffect().getAmplifier() * 0.1F + (b.getEffect().getDuration() / (600));
					}
				}
			}
			return f;
		}
		return 0F;
	}
	
	/**
	 * Used to determine if it should use the actual Base or awkward when turned
	 * off in the config
	 * 
	 * @param base
	 *            Brewing Base
	 * @return base or awkward
	 */
	public static BrewingBase getBaseBrewing(BrewingBase base)
	{
		if (BrewingList.DEFAULT_AWKWARD_BREWING)
		{
			return BrewingList.awkward;
		}
		return base;
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("Brewing{");
		if (this.getEffect() != null)
			s.append("PotionEffect<").append(this.getEffect().getPotionID()).append(" [").append(this.getEffect().getDuration()).append("] x ").append(this.getEffect().getAmplifier()).append(">");
		s.append("MaxValues<[").append(this.getMaxDuration()).append("] x ").append(this.getMaxAmplifier()).append(">");
		if (this.getInverted() != null)
			s.append("Opposite<").append(this.getInverted().toString()).append(">");
		if (this.getIngredient() != null)
			s.append("Ingredient<").append(this.getIngredient().itemID).append(":").append(this.getIngredient().getItemDamage()).append(">");
		if (this.getBase() != null)
			s.append("Base<").append(this.getBase().toString()).append(">");
		if (this.getEffect() != null)
			s.append("Name<").append(this.getEffect().getEffectName()).append(">");
		s.append("}");
		return s.toString();
	}
	
	public static Collection<Brewing> removeDuplicates(Collection<Brewing> list)
	{
		if (list != null && list.size() > 0)
		{
			List<Brewing> result = new ArrayList<Brewing>();
			for (Brewing b : list)
			{
				boolean duplicate = false;
				for (Brewing b2 : result)
				{
					if (b.getEffect() != null && b2.getEffect() != null && b.getEffect().getPotionID() == b2.getEffect().getPotionID())
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
		return list;
	}
	
	public static List<Brewing> removeDuplicates(Brewing[] list)
	{
		if (list != null && list.length > 0)
		{
			List<Brewing> result = new ArrayList<Brewing>();
			for (Brewing b : list)
			{
				boolean duplicate = false;
				for (Brewing b2 : result)
				{
					if (b.getEffect().getPotionID() == b2.getEffect().getPotionID())
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
		return new LinkedList<Brewing>();
	}
	
	@Override
	public int compareTo(Brewing o)
	{
		return (this.getEffect() != null && o.getEffect() != null) ? Integer.compare(this.getEffect().getPotionID(), o.getEffect().getPotionID()) : 0;
	}
}
