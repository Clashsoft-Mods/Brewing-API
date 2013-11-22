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
	public static String							NBTVersion					= "1.0.1";
	
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
	 * Creates a new Brewing
	 * 
	 * @param par1PotionEffect
	 *            Effect
	 * @param par2
	 *            Max Amplifier
	 * @param par3
	 *            Max Duration
	 * @param par4BrewingBase
	 *            base
	 */
	public Brewing(PotionEffect par1PotionEffect, int par2, int par3)
	{
		this(par1PotionEffect, par2, par3, null);
	}
	
	/**
	 * Creates a new Brewing with Opposite
	 * 
	 * @param par1PotionEffect
	 *            Effect
	 * @param par2
	 *            Improvable
	 * @param par3
	 *            Extendable
	 * @param par4Brewing
	 *            Opposite
	 */
	public Brewing(PotionEffect par1PotionEffect, int par2, int par3, Brewing par4Brewing)
	{
		this(par1PotionEffect, par2, par3, par4Brewing, null, null);
	}
	
	/**
	 * Creates a new Brewing with Ingredient and Base
	 * 
	 * @param par1PotionEffect
	 *            Effect
	 * @param par2
	 *            Improvable
	 * @param par3
	 *            Extendable
	 * @param par4ItemStack
	 *            Ingredient
	 */
	public Brewing(PotionEffect par1PotionEffect, int par2, int par3, ItemStack par4ItemStack, BrewingBase par5BrewingBase)
	{
		this(par1PotionEffect, par2, par3, null, par4ItemStack, par5BrewingBase);
	}
	
	/**
	 * Creates a new Brewing with Opposite, Ingredient and Base
	 * 
	 * @param par1PotionEffect
	 *            Effect
	 * @param par2
	 *            Improvable
	 * @param par3
	 *            Extendable
	 * @param par4Brewing
	 *            Opposite
	 * @param par5ItemStack
	 *            Ingredient
	 */
	public Brewing(PotionEffect par1PotionEffect, int par2, int par3, Brewing par4Brewing, ItemStack par5ItemStack, BrewingBase par6BrewingBase)
	{
		this.effect = par1PotionEffect;
		this.maxAmplifier = par2;
		this.maxDuration = par3;
		this.opposite = par4Brewing;
		this.ingredient = par5ItemStack;
		this.base = par6BrewingBase;
	}
	
	/**
	 * Determines if the Effect is bad or not
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
			if (BrewingAPI.CLASHSOFT_API())
			{
				if (Potion.potionTypes[this.getEffect().getPotionID()] instanceof clashsoft.cslib.minecraft.CustomPotion)
				{
					return ((clashsoft.cslib.minecraft.CustomPotion) Potion.potionTypes[this.getEffect().getPotionID()]).getIsGoodOrNotGoodEffect();
				}
			}
		}
		return false;
	}
	
	public PotionEffect getEffect()
	{
		return effect;
	}
	
	public int getMaxAmplifier()
	{
		return maxAmplifier;
	}
	
	public int getMaxDuration()
	{
		return maxDuration;
	}
	
	public boolean isImprovable()
	{
		return effect != null ? effect.getAmplifier() < maxAmplifier : false;
	}
	
	public boolean isExtendable()
	{
		return effect != null ? effect.getDuration() < maxDuration : false;
	}
	
	public Brewing getOpposite()
	{
		if (opposite != null && opposite.getEffect() != null)
		{
			opposite.setEffect(new PotionEffect(opposite.getEffect().getPotionID(), this.getEffect().getDuration() / 2, this.isImprovable() ? this.getEffect().getAmplifier() : 0));
		}
		return opposite;
	}
	
	public ItemStack getIngredient()
	{
		return ingredient;
	}
	
	public boolean isRandom()
	{
		return isRandom;
	}
	
	public BrewingBase getBase()
	{
		return base;
	}
	
	public boolean isBase()
	{
		return this.getEffect() == null;
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
			if (b.getEffect() != null && b.getEffect().getPotionID() == this.getEffect().getPotionID())
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
		return (T) extendedAttributes.get(name);
	}
	
	public Brewing setEffect(PotionEffect par1)
	{
		effect = par1;
		return this;
	}
	
	public Brewing setMaxAmplifier(int par1)
	{
		maxAmplifier = par1;
		return this;
	}
	
	public Brewing setMaxDuration(int par1)
	{
		maxDuration = par1;
		return this;
	}
	
	public Brewing setOpposite(Brewing par1)
	{
		opposite = par1;
		return this;
	}
	
	public Brewing setIngredient(ItemStack par1)
	{
		ingredient = par1;
		return this;
	}
	
	public Brewing setIsRandom(boolean par1)
	{
		isRandom = par1;
		return this;
	}
	
	public Brewing setBase(BrewingBase par1)
	{
		base = par1;
		return this;
	}
	
	public Object setExtendedAttribute(String name, IBrewingAttribute value)
	{
		Object old = getExtendedAttribute(name);
		extendedAttributes.put(name, value);
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
		if (isImprovable() && this.getEffect() != null)
		{
			return this.setEffect(new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration(), this.getEffect().getAmplifier() + 1)).setMaxDuration(this.getEffect().getDuration());
		}
		return this;
	}
	
	public Brewing onExtended()
	{
		if (isExtendable() && this.getEffect() != null)
		{
			return this.setEffect(new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration() * 2, this.getEffect().getAmplifier())).setMaxAmplifier(this.getEffect().getAmplifier());
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
		{
			list.add(new Brewing(this.getEffect() != null ? (new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration(), this.getEffect().getAmplifier() + 1)) : null, maxAmplifier, this.getEffect().getDuration(), this.getOpposite(), this.getIngredient(), this.getBase()));
		}
		if (this.isExtendable())
		{
			list.add(new Brewing(this.getEffect() != null ? (new PotionEffect(this.getEffect().getPotionID(), this.getEffect().getDuration() * 2, this.getEffect().getAmplifier())) : null, 0, maxDuration, this.getOpposite(), this.getIngredient(), this.getBase()));
		}
		return list;
	}
	
	/**
	 * Writes the Brewing to the ItemStack NBT
	 * 
	 * @param par1
	 *            ItemStack
	 * @return ItemStack with Brewing NBT
	 */
	public ItemStack addBrewingToItemStack(ItemStack par1)
	{
		if (par1 != null)
		{
			if (par1.stackTagCompound == null)
			{
				par1.setTagCompound(new NBTTagCompound());
			}
			
			if (!par1.stackTagCompound.hasKey("Brewing"))
			{
				par1.stackTagCompound.setTag("Brewing", new NBTTagList("Brewing"));
			}
			NBTTagList var2 = (NBTTagList) par1.stackTagCompound.getTag("Brewing");
			var2.appendTag(this.createNBT());
			return par1;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Returns the first Brewing that can be read from the ItemStack NBT
	 * 
	 * @param par1ItemStack
	 * @return First Brewing read from ItemStack NBT
	 * @deprecated Use ItemPotion2.getEffects instead to get all Brewings from
	 *             the ItemStack NBT
	 */
	@Deprecated
	public static Brewing getBrewingFromItemStack(ItemStack par1ItemStack)
	{
		if (par1ItemStack != null && par1ItemStack.hasTagCompound())
		{
			NBTTagList list = par1ItemStack.stackTagCompound.getTagList("Brewing");
			if (list != null && list.tagCount() > 0)
			{
				NBTTagCompound compound = (NBTTagCompound) list.tagAt(0);
				return readFromNBT(compound);
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
		if (getHandlerForIngredient(ingredient) != null)
		{
			return true;
		}
		return getBrewingFromIngredient(ingredient) != null || (getHandlerForIngredient(ingredient) != null);
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
		if (getBrewingFromIngredient(ingredient) != null) // The ingredient is a
															// normal ingredient
															// or a base
															// ingredient
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
	 * @param par1
	 * @return Brewing that is brewed with the ItemStack
	 */
	public static Brewing getBrewingFromIngredient(ItemStack par1)
	{
		if (par1 != null)
		{
			BrewingBase base = BrewingBase.getBrewingBaseFromIngredient(par1);
			if (base != null)
				return base;
			for (Brewing b : brewingList)
			{
				if (b.getIngredient() != null)
				{
					// Include Ore Dictionary
					if (OreDictionary.itemMatches(b.getIngredient(), par1, true))
						return b;
					if (b.getIngredient().getItem() == par1.getItem() && b.getIngredient().getItemDamage() == par1.getItemDamage())
						return b;
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates an NBTTagCompound in which the Brewing is stored
	 * 
	 * @return NBTTagCompound with Brewing data
	 */
	public NBTTagCompound createNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("VERSION", NBTVersion);
		if (isBase() && this instanceof BrewingBase)
		{
			return ((BrewingBase) this).createNBT();
		}
		if (effect != null)
		{
			if (effect.getPotionID() > 0)
			{
				nbt.setInteger("PotionID", effect.getPotionID());
			}
			if (effect.getDuration() > 0)
			{
				nbt.setInteger("PotionDuration", effect.getDuration());
			}
			if (effect.getAmplifier() > 0)
			{
				nbt.setInteger("PotionAmplifier", effect.getAmplifier());
			}
		}
		if (maxAmplifier > 0)
		{
			nbt.setInteger("MaxAmplifier", maxAmplifier);
		}
		if (effect != null && maxDuration > effect.getDuration())
		{
			nbt.setInteger("MaxDuration", maxDuration);
		}
		if (opposite != null)
		{
			nbt.setCompoundTag("Opposite", opposite.createNBT());
		}
		if (extendedAttributes != null)
		{
			NBTTagList list = new NBTTagList();
			for (String s : extendedAttributes.keySet())
			{
				list.appendTag(extendedAttributes.get(s).toNBT());
			}
			nbt.setTag("ExtendedAttributes", list);
		}
		return nbt;
	}
	
	/**
	 * Reads a Brewing from a NBTTagCompound
	 * 
	 * @param par1NBTTagCompound
	 *            NBTTagCompound to read Brewing from
	 * @return Brewing read from NBTTagCompound
	 */
	public static Brewing readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		int potionID = par1NBTTagCompound.hasKey("PotionID") ? par1NBTTagCompound.getInteger("PotionID") : 0;
		int potionDuration = par1NBTTagCompound.hasKey("PotionDuration") ? par1NBTTagCompound.getInteger("PotionDuration") : 0;
		int potionAmplifier = par1NBTTagCompound.hasKey("PotionAmplifier") ? par1NBTTagCompound.getInteger("PotionAmplifier") : 0;
		
		if (potionID == 0 || par1NBTTagCompound.hasKey("BaseName"))
		{
			return BrewingBase.readFromNBT(par1NBTTagCompound);
		}
		
		int maxamp = par1NBTTagCompound.hasKey("MaxAmplifier") ? par1NBTTagCompound.getInteger("MaxAmplifier") : (par1NBTTagCompound.hasKey("Improvable") ? (par1NBTTagCompound.getBoolean("Improvable") ? 1 : 0) : 0);
		int maxdur = par1NBTTagCompound.hasKey("MaxDuration") ? par1NBTTagCompound.getInteger("MaxDuration") : (par1NBTTagCompound.hasKey("Extendable") ? (par1NBTTagCompound.getBoolean("Extendable") ? potionDuration * 2 : potionDuration) : potionDuration);
		
		int ingredientID = par1NBTTagCompound.hasKey("IngredientID") ? par1NBTTagCompound.getInteger("IngredientID") : 0;
		int ingredientAmount = par1NBTTagCompound.hasKey("IngredientAmount") ? par1NBTTagCompound.getInteger("IngredientAmount") : 0;
		int ingredientDamage = par1NBTTagCompound.hasKey("IngredientDamage") ? par1NBTTagCompound.getInteger("IngredientDamage") : 0;
		Brewing opposite = par1NBTTagCompound.hasKey("Opposite") ? readFromNBT(par1NBTTagCompound.getCompoundTag("Opposite")) : null;
		BrewingBase base = (BrewingBase) (par1NBTTagCompound.hasKey("Base") ? BrewingBase.readFromNBT(par1NBTTagCompound) : BrewingList.awkward);
		
		Brewing brewing = new Brewing(new PotionEffect(potionID, potionDuration, potionAmplifier), maxamp, maxdur, opposite, new ItemStack(ingredientID, ingredientAmount, ingredientDamage), base);
		
		if (par1NBTTagCompound.hasKey("ExtendedAttributes"))
		{
			NBTTagList list = par1NBTTagCompound.getTagList("ExtendedAttributes");
			
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
		
		return brewing;
	}
	
	/**
	 * Returns a random potion. Used by the random potion.
	 * 
	 * @return Random Potion
	 */
	@Deprecated
	public static Brewing random()
	{
		Random rnd = new Random();
		return combinableEffects.get(rnd.nextInt(combinableEffects.size())).setIsRandom(true);
	}
	
	/**
	 * Calculates the experience given for brewing this potion
	 * 
	 * @param par1ItemStack
	 *            potion
	 * @return Experience float
	 */
	public static float getExperience(ItemStack par1ItemStack)
	{
		if (par1ItemStack.getItem() instanceof ItemPotion2)
		{
			List<Brewing> effects = ((ItemPotion2) par1ItemStack.getItem()).getEffects(par1ItemStack);
			float f = ((ItemPotion2) par1ItemStack.getItem()).isSplash(par1ItemStack.getItemDamage()) ? 0.3F : 0.2F;
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
	 * @param par1BrewingBase
	 *            Brewing Base
	 * @return par1BrewingBase or awkward
	 */
	public static BrewingBase getBaseBrewing(BrewingBase par1BrewingBase)
	{
		if (BrewingList.DEFAULT_AWKWARD_BREWING)
		{
			return BrewingList.awkward;
		}
		return par1BrewingBase;
	}
	
	public long createDataLong()
	{
		long l = 0;
		if (this.getEffect() != null)
		{
			l |= this.getEffect().getPotionID();
			l |= this.getEffect().getAmplifier() << 10L;
		}
		l |= this.getMaxAmplifier() << 18L;
		if (this.getOpposite() != null)
			l |= brewingList.indexOf(this.getOpposite()) << 26L;
		if (this.getEffect() != null)
			l |= ((long) this.getEffect().getDuration()) << 34L;
		l |= ((long) this.getMaxDuration()) << 42L;
		return l;
	}
	
	public static Brewing fromDataLong(long dataLong)
	{
		int potionId = (int) (dataLong & 1023L);
		int potionAmplifier = (int) ((dataLong >> 10L) & 255L);
		int maxAmplifier = (int) ((dataLong >> 18L) & 255L);
		int oppositeId = (int) ((dataLong >> 26L) & 255L);
		int potionDuration = (int) ((dataLong >> 34L) & 255L);
		int maxDuration = (int) ((dataLong >> 42L));
		Brewing opposite = oppositeId < brewingList.size() ? brewingList.get(oppositeId) : null;
		return new Brewing(new PotionEffect(potionId, potionDuration, potionAmplifier), maxAmplifier, maxDuration, opposite);
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("Brewing{");
		if (this.getEffect() != null)
			s.append("PotionEffect<").append(this.getEffect().getPotionID()).append(" [").append(this.getEffect().getDuration()).append("] x ").append(this.getEffect().getAmplifier()).append(">");
		s.append("MaxValues<[").append(this.getMaxDuration()).append("] x ").append(this.getMaxAmplifier()).append(">");
		if (this.getOpposite() != null)
			s.append("Opposite<").append(this.getOpposite().toString()).append(">");
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
