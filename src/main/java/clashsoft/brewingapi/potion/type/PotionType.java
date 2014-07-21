package clashsoft.brewingapi.potion.type;

import java.util.*;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.IIngredientHandler;
import clashsoft.brewingapi.potion.recipe.IPotionRecipe;
import clashsoft.brewingapi.potion.recipe.PotionRecipe;
import clashsoft.brewingapi.potion.recipe.PotionRecipes;
import clashsoft.cslib.minecraft.potion.CustomPotion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Clashsoft
 */
public class PotionType extends AbstractPotionType
{
	/** The effect **/
	private PotionEffect	effect;
	/** Maximum effect amplifier **/
	private int				maxAmplifier;
	/** Maximum effect duration **/
	private int				maxDuration;
	/** Fermented Spider Eye effect **/
	private IPotionType		inverted;
	
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
		this(effect, maxAmplifier, maxDuration, (IPotionType) null);
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
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration, IPotionType inverted)
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
		this(effect, maxAmplifier, maxDuration, (IPotionType) null, ingredient, base);
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
	public PotionType(PotionEffect effect, int maxAmplifier, int maxDuration, IPotionType inverted, ItemStack ingredient, PotionBase base)
	{
		this.effect = effect;
		this.maxAmplifier = maxAmplifier;
		this.maxDuration = maxDuration;
		this.inverted = inverted;
		
		PotionRecipes.addRecipe(ingredient, base, this);
	}
	
	@Override
	public PotionType copy()
	{
		PotionType potionType = new PotionType(this.getEffect(), this.getMaxAmplifier(), this.getMaxDuration(), this.getInverted(), null, this.getBase());
		return potionType;
	}
	
	@Override
	public boolean isBadEffect()
	{
		if (this.hasEffect())
		{
			Potion potion = this.getPotion();
			switch (potion.id)
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
			if (potion instanceof CustomPotion)
			{
				return ((CustomPotion) potion).isBadEffect();
			}
		}
		return false;
	}
	
	@Override
	public PotionEffect getEffect()
	{
		return this.effect;
	}
	
	@Override
	public int getMaxAmplifier()
	{
		return this.maxAmplifier;
	}
	
	@Override
	public int getMaxDuration()
	{
		return this.maxDuration;
	}
	
	@Override
	public int getDefaultDuration()
	{
		return this.getDuration();
	}
	
	@Override
	public IPotionType getInverted()
	{
		return this.inverted;
	}
	
	@Override
	public boolean isBase()
	{
		return false;
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
	
	public IPotionType setInverted(IPotionType opposite)
	{
		this.inverted = opposite;
		return this;
	}
	
	@Override
	public IPotionType onImproved()
	{
		PotionEffect effect = this.getEffect();
		if (effect != null)
		{
			effect = improve(effect);
			return new PotionTypeDelegate(effect, this);
		}
		return this;
	}
	
	@Override
	public IPotionType onExtended()
	{
		PotionEffect effect = this.getEffect();
		if (effect != null)
		{
			effect = extend(effect);
			return new PotionTypeDelegate(effect, this);
		}
		return this;
	}
	
	@Override
	public IPotionType onDiluted()
	{
		PotionEffect effect = this.getEffect();
		if (effect != null)
		{
			effect = dilute(effect);
			return new PotionTypeDelegate(effect, this);
		}
		return this;
	}
	
	@Override
	public IPotionType onGunpowderUsed()
	{
		PotionEffect effect = this.getEffect();
		if (effect != null)
		{
			effect = useGunpowder(effect);
			return new PotionTypeDelegate(effect, this);
		}
		return this;
	}
	
	@Override
	public IPotionType onInverted()
	{
		PotionEffect effect = this.getEffect();
		IPotionType inverted = this.getInverted();
		if (inverted != null)
		{
			effect = invert(effect, inverted.getEffect());
			return new PotionTypeDelegate(effect, inverted);
		}
		return this;
	}
	
	@Override
	public void apply_do(EntityLivingBase target, PotionEffect effect)
	{
		if (effect != null)
		{
			target.addPotionEffect(effect);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound effect = new NBTTagCompound();
		this.effect.writeCustomPotionEffectToNBT(effect);
		nbt.setTag("Effect", effect);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.effect == null ? 0 : this.effect.hashCode());
		result = prime * result + (this.inverted == null ? 0 : this.inverted.hashCode());
		result = prime * result + this.maxAmplifier;
		result = prime * result + this.maxDuration;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		PotionType other = (PotionType) obj;
		if (this.effect == null)
		{
			if (other.effect != null)
			{
				return false;
			}
		}
		else if (!this.effect.equals(other.effect))
		{
			return false;
		}
		if (this.inverted == null)
		{
			if (other.inverted != null)
			{
				return false;
			}
		}
		else if (!this.inverted.equals(other.inverted))
		{
			return false;
		}
		if (this.maxAmplifier != other.maxAmplifier)
		{
			return false;
		}
		if (this.maxDuration != other.maxDuration)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		String uuid = this.getUUID();
		builder.append("PotionType");
		if (uuid != null && !uuid.isEmpty())
		{
			builder.append(".").append(uuid);
		}
		builder.append(" [");
		if (this.effect != null)
		{
			builder.append("effect=").append(this.effect).append(", ");
		}
		if (this.inverted != null)
		{
			builder.append("inverted=").append(this.inverted).append(", ");
		}
		builder.append("maxAmplifier=").append(this.maxAmplifier);
		builder.append(", maxDuration=").append(this.maxDuration);
		builder.append("]");
		return builder.toString();
	}
	
	public static IPotionType getRandom(Random rng)
	{
		return combinableTypes.get(rng.nextInt(combinableTypes.size()));
	}
	
	public static IPotionType getFromEffect(PotionEffect effect)
	{
		if (effect == null)
			return null;
		
		IPotionType potionType = getFromEffect_(effect);
		if (potionType != null)
		{
			return new PotionTypeDelegate(effect, potionType);
		}
		return new PotionType(effect, effect.getAmplifier(), effect.getDuration());
	}
	
	protected static IPotionType getFromEffect_(PotionEffect effect)
	{
		return effect == null ? null : getFromID(effect.getPotionID());
	}
	
	/**
	 * Returns the first PotionType that can be read from the ItemStack NBT
	 * 
	 * @deprecated
	 * @param stack
	 *            the stack
	 * @return First PotionType read from ItemStack NBT
	 */
	@Deprecated
	public static IPotionType getFirstPotionType(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = (NBTTagList) stack.stackTagCompound.getTag(COMPOUND_NAME);
			if (list.tagCount() > 0)
			{
				NBTTagCompound nbt1 = list.getCompoundTagAt(0);
				IPotionType potionType = getFromNBT(nbt1);
				return potionType;
			}
		}
		return null;
	}
	
	public static List<IPotionType> getPotionTypes(ItemStack potion)
	{
		return ((ItemPotion2) potion.getItem()).getPotionTypes(potion);
	}
	
	public static List<IPotionType> getPotionTypes_(ItemStack stack)
	{
		if (stack != null && stack.hasTagCompound())
		{
			NBTTagList list = (NBTTagList) stack.stackTagCompound.getTag(COMPOUND_NAME);
			
			if (list != null)
			{
				int len = list.tagCount();
				if (len > 0)
				{
					List<IPotionType> types = new ArrayList(len);
					for (int i = 0; i < len; i++)
					{
						NBTTagCompound nbt1 = list.getCompoundTagAt(i);
						IPotionType potionType = getFromNBT(nbt1);
						types.add(potionType);
					}
					return types;
				}
			}
		}
		return Collections.EMPTY_LIST;
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
		return getFromIngredient(stack) != null || hasIngredientHandler(stack);
	}
	
	/**
	 * Finds the first registered {@link IIngredientHandler} that can handle the
	 * ingredient
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @return the corresponding {@link IIngredientHandler}
	 */
	public static IIngredientHandler getIngredientHandler(ItemStack ingredient)
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
	public static boolean hasIngredientHandler(ItemStack ingredient)
	{
		return getIngredientHandler(ingredient) != null;
	}
	
	/**
	 * Applies the given {@link ItemStack} {@code ingredient} to the given
	 * {@link ItemStack} {@code potionStack}.
	 * 
	 * @param ingredient
	 *            Ingredient
	 * @param potionStack
	 *            the potion ItemStack
	 * @return the potion with applied ingredients
	 */
	public static ItemStack applyIngredient(ItemStack ingredient, ItemStack potionStack)
	{
		IIngredientHandler handler = getIngredientHandler(ingredient);
		if (handler != null && handler.canApplyIngredient(ingredient, potionStack))
		{
			return handler.applyIngredient(ingredient, potionStack);
		}
		
		IPotionRecipe recipe = PotionRecipes.get(ingredient);
		if (recipe != null)
		{
			return recipe.apply(potionStack);
		}
		return potionStack;
	}
	
	public static boolean canApplyIngredient(ItemStack ingredient, ItemStack potion)
	{
		IIngredientHandler handler = getIngredientHandler(ingredient);
		
		if (handler != null)
		{
			return handler.canApplyIngredient(ingredient, potion);
		}
		else
		{
			IPotionType type = getFromIngredient(ingredient);
			if (type != null)
			{
				List<IPotionType> potionTypes = ((ItemPotion2) potion.getItem()).getPotionTypes(potion);
				return hasBase(type, potionTypes);
			}
		}
		return false;
	}
	
	public static boolean hasBase(IPotionType type, List<IPotionType> types)
	{
		PotionBase requiredBase = type.getBase();
		if (requiredBase == null)
		{
			return true;
		}
		else
		{
			for (IPotionType pt : types)
			{
				if (requiredBase.equals(pt))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static IPotionType getFromID(int potionID)
	{
		return potionTypes.get(potionID);
	}
	
	/**
	 * Returns a PotionType that is brewed with the itemstack. it doesn't check
	 * for the amount. Ignores Special Ingredient Handlers.
	 * 
	 * @param stack
	 * @return PotionType that is brewed with the ItemStack
	 */
	public static IPotionType getFromIngredient(ItemStack stack)
	{
		IPotionRecipe recipe = PotionRecipes.get(stack);
		if (recipe instanceof PotionRecipe)
		{
			return ((PotionRecipe) recipe).getOutput();
		}
		return null;
	}
	
	public static IPotionType getFromNBT(NBTTagCompound nbt)
	{
		if (nbt != null && !nbt.hasNoTags())
		{
			IPotionType result;
			if (nbt.hasKey("BaseName"))
			{
				result = new PotionBase();
			}
			else
			{
				result = new PotionTypeDelegate();
			}
			result.readFromNBT(nbt);
			return result;
		}
		return null;
	}
	
	/**
	 * Calculates the experience given for brewing this potion
	 * 
	 * @param stack
	 *            potion
	 * @return the experience
	 */
	public static float getExperience(ItemStack stack)
	{
		if (stack != null && stack.getItem() instanceof ItemPotion2)
		{
			ItemPotion2 item = (ItemPotion2) stack.getItem();
			List<IPotionType> effects = item.getPotionTypes(stack);
			float value = item.isSplashDamage(stack.getItemDamage()) ? 0.3F : 0.2F;
			for (IPotionType b : effects)
			{
				if (b.hasEffect())
				{
					float f1 = b.isBadEffect() ? 0.2F : 0.3F;
					value += f1 + b.getAmplifier() * 0.1F + b.getEffect().getDuration() / 600;
				}
			}
			return value;
		}
		return 0F;
	}
	
	public static List<IPotionType> removeDuplicates(Collection<IPotionType> list)
	{
		if (list != null && list.size() > 0)
		{
			List<IPotionType> result = new ArrayList();
			for (IPotionType b : list)
			{
				boolean duplicate = false;
				for (IPotionType b2 : result)
				{
					if (b.getPotionID() == b2.getPotionID())
					{
						duplicate = true;
						break;
					}
				}
				if (!duplicate)
				{
					result.add(b);
				}
			}
			Collections.sort(result);
			return result;
		}
		return Collections.EMPTY_LIST;
	}
	
	public static List<IPotionType> removeDuplicates(IPotionType[] list)
	{
		if (list != null && list.length > 0)
		{
			List<IPotionType> result = new ArrayList();
			for (IPotionType b : list)
			{
				boolean duplicate = false;
				for (IPotionType b2 : result)
				{
					if (b.getPotionID() == b2.getPotionID())
					{
						duplicate = true;
						break;
					}
				}
				if (!duplicate)
				{
					result.add(b);
				}
			}
			return result;
		}
		return Collections.EMPTY_LIST;
	}
}
