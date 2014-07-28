package clashsoft.brewingapi.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.lib.AttributeModifierComparator;
import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.PotionUtils;
import clashsoft.brewingapi.potion.attribute.IPotionAttribute;
import clashsoft.brewingapi.potion.base.IPotionBase;
import clashsoft.brewingapi.potion.base.PotionBase;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.cslib.minecraft.lang.I18n;
import clashsoft.cslib.minecraft.potion.CustomPotion;
import clashsoft.cslib.util.CSString;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

/**
 * @author Clashsoft
 */
public class ItemPotion2 extends ItemPotion
{
	public IIcon									bottle;
	public IIcon									splashbottle;
	public IIcon									liquid;
	
	public ItemPotion2()
	{
		this.setMaxStackSize(BrewingAPI.potionStackSize);
		this.setHasSubtypes(true);
	}
	
	@Override
	public List<PotionEffect> getEffects(ItemStack stack)
	{
		if (stack == null || this.isWater(stack))
		{
			return Collections.EMPTY_LIST;
		}
		
		List<IPotionType> types = this.getPotionTypes(stack);
		List<PotionEffect> effects = new ArrayList(types.size());
		
		for (IPotionType type : types)
		{
			PotionEffect effect = type.getEffect();
			if (effect != null)
			{
				effects.add(effect);
			}
		}
		
		return effects;
	}
	
	public List<PotionEffect> getSuperEffects(ItemStack stack)
	{
		return super.getEffects(stack);
	}
	
	/**
	 * Returns a list of potion effects for the specified itemstack.
	 */
	public List<IPotionType> getPotionTypes(ItemStack stack)
	{
		return new PotionTypeList(stack);
	}
	
	public boolean hasEffects(ItemStack stack)
	{
		List<IPotionType> effects = this.getPotionTypes(stack);
		return effects != null && !effects.isEmpty();
	}
	
	/**
	 * Gets the bottle that is returned when drinking the potion.
	 * 
	 * @return the bottle item stack
	 */
	public ItemStack getGlassBottle()
	{
		return new ItemStack(Items.glass_bottle);
	}
	
	@SideOnly(Side.CLIENT)
	public static IIcon getPotionIcon(String iconName)
	{
		return iconName.equals("bottle_drinkable") ? BrewingAPI.potion2.bottle : iconName.equals("bottle_splash") ? BrewingAPI.potion2.splashbottle : iconName.equals("overlay") ? BrewingAPI.potion2.liquid : null;
	}
	
	/**
	 * Returns the icon of the splash bottle of the potion {@code stack}
	 * 
	 * @param stack
	 *            the stack
	 * @return the icon
	 */
	public IIcon getSplashIcon(ItemStack stack)
	{
		return this.splashbottle;
	}
	
	/**
	 * Returns true if this potion is a throwable splash potion.
	 * 
	 * @param metadata
	 *            the damage value
	 * @return true if this potion is a throwable splash potion
	 */
	public boolean isSplash(ItemStack stack)
	{
		return this.isSplashDamage(stack.getItemDamage());
	}
	
	public boolean isSplashDamage(int metadata)
	{
		return (metadata & 2) != 0 || ItemPotion.isSplash(metadata);
	}
	
	public int setSplash(ItemStack stack, boolean splash)
	{
		int metadata = stack.getItemDamage();
		metadata = splash ? metadata | 2 : metadata & ~2;
		stack.setItemDamage(metadata);
		return metadata;
	}
	
	public boolean isWater(ItemStack stack)
	{
		return stack.getItemDamage() == 0;
	}
	
	public int getLiquidColor(ItemStack stack)
	{
		if (this.isWater(stack))
		{
			return 0x0C0CFF;
		}
		
		List<IPotionType> effects = this.getPotionTypes(stack);
		int size = effects.size();
		
		if (size == 0)
		{
			return 0x0C0CFF;
		}
		else if (size == 1)
		{
			return effects.get(0).getLiquidColor();
		}
		
		size = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (IPotionType potionType : effects)
		{
			if (!potionType.isBase())
			{
				int c = potionType.getLiquidColor();
				r += (c >> 16) & 255;
				g += (c >> 8) & 255;
				b += (c >> 0) & 255;
				size++;
			}
		}
		
		if (size == 0)
		{
			return 0x0C0CFF;
		}
		
		r /= size;
		g /= size;
		b /= size;
		
		return (r << 16) | (g << 8) | (b << 0);
	}
	
	/**
	 * Returns true if all effects of the potion {@code stack} are instant.
	 * 
	 * @param stack
	 *            the stack
	 * @return true if all effects of the potion are instant.
	 */
	public boolean isEffectInstant(ItemStack stack)
	{
		List<IPotionType> effects = this.getPotionTypes(stack);
		if (effects.size() == 0)
		{
			return false;
		}
		boolean flag = true;
		for (IPotionType b : effects)
		{
			if (b.hasEffect())
			{
				flag &= b.isInstant();
			}
		}
		return flag;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			for (IPotionType potionType : this.getPotionTypes(stack))
			{
				potionType.apply(player);
			}
		}
		
		if (!player.capabilities.isCreativeMode)
		{
			--stack.stackSize;
			
			if (stack.stackSize <= 0)
			{
				return this.getGlassBottle();
			}
			else
			{
				player.inventory.addItemStackToInventory(this.getGlassBottle());
			}
		}
		
		return stack;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.drink;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (this.isSplash(stack))
		{
			if (!player.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}
			
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			
			if (!world.isRemote)
			{
				world.spawnEntityInWorld(new EntityPotion2(world, player, stack));
			}
			
			return stack;
		}
		else
		{
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			return stack;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int metadata)
	{
		return this.isSplashDamage(metadata) ? this.splashbottle : this.bottle;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return pass == 0 ? this.liquid : this.getIconFromDamage(stack.getItemDamage());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = this.bottle = iconRegister.registerIcon("minecraft:potion_bottle_drinkable");
		this.splashbottle = iconRegister.registerIcon("minecraft:potion_bottle_splash");
		this.liquid = iconRegister.registerIcon("minecraft:potion_overlay");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		return pass == 0 ? this.getLiquidColor(stack) : 0xFFFFFF;
	}
	
	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		if (this.isWater(stack))
		{
			return I18n.getString("item.emptyPotion.name");
		}
		
		List<IPotionType> potionTypes = this.getPotionTypes(stack);
		List<IPotionType> effects = new ArrayList();
		List<PotionBase> bases = new ArrayList();
		
		StringBuilder result = new StringBuilder(potionTypes.size() * 20);
		
		if (this.isSplash(stack))
		{
			result.append(I18n.getString("potion.prefix.grenade")).append(" ");
		}
		
		if (potionTypes.isEmpty())
		{
			return StatCollector.translateToLocal("item.potion.name");
		}
		
		if (potionTypes.size() == IPotionType.combinableTypes.size())
		{
			result.insert(0, EnumChatFormatting.BLUE.toString()).append(I18n.getString("potion.alleffects.postfix"));
			return result.toString();
		}
		
		for (IPotionType pt : potionTypes)
		{
			if (pt.isBase())
			{
				bases.add((PotionBase) pt);
			}
			else
			{
				effects.add(pt);
			}
		}
		
		for (PotionBase base : bases)
		{
			result.append(I18n.getString(base.getEffectName())).append(" ");
		}
		if (effects.isEmpty())
		{
			result.append(I18n.getString(this.getUnlocalizedName() + ".name"));
		}
		else if (effects.size() > 4)
		{
			result.append(I18n.getString("potion.potionof")).append(" ").append(effects.size()).append(" ").append(I18n.getString("potion.effects"));
		}
		else
		{
			int size = effects.size();
			for (int i = 0; i < size; i++)
			{
				IPotionType type = effects.get(i);
				
				boolean hasPrevious = i > 0;
				boolean isLast = i == size - 1;
				
				if (!hasPrevious)
				{
					result.append(I18n.getString(type.getEffectName() + ".postfix"));
				}
				else
				{
					if (isLast)
					{
						result.append(" ").append(I18n.getString("potion.and")).append(" ");
					}
					else
					{
						result.append(", ");
					}
					result.append(I18n.getString(type.getEffectName()));
				}
			}
		}
		return result.toString();
	}
	
	private static int	glowPos	= 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
	{
		if (this.isWater(stack))
		{
			return;
		}
		
		List<IPotionType> potionTypes = this.getPotionTypes(stack);
		int size = potionTypes.size();
		Multimap<String, AttributeModifier> attributeMap = TreeMultimap.create(String.CASE_INSENSITIVE_ORDER, AttributeModifierComparator.instance);
		
		if (size == 0)
		{
			list.add(EnumChatFormatting.GRAY + I18n.getString("potion.empty"));
			return;
		}
		
		glowPos++;
		if (glowPos > 100)
		{
			glowPos = 0;
		}
		
		if (size > 5)
		{
			glowPos = -1;
		}
		
		for (IPotionType potionType : potionTypes)
		{
			Potion potion = potionType.getPotion();
			
			if (potionType.isBase() || potion == null)
			{
				if (size == 1)
				{
					list.add(EnumChatFormatting.GRAY + I18n.getString("potion.empty"));
				}
				continue;
			}
			
			Map map = potion.func_111186_k();
			if (map != null && map.size() > 0)
			{
				for (Object object : map.keySet())
				{
					AttributeModifier attributemodifier = (AttributeModifier) map.get(object);
					if (attributemodifier != null)
					{
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potionType.getEffect().getAmplifier(), attributemodifier), attributemodifier.getOperation());
						attributeMap.put(((BaseAttribute) object).getAttributeUnlocalizedName(), attributemodifier1);
					}
				}
			}
			
			StringBuilder builder = potionType.getDisplayName();
			int glowPosInt = glowPos / 2;
			String colorLight = "";
			String colorDark = "";
			
			if (potion instanceof CustomPotion && ((CustomPotion) potion).getCustomColor() != -1)
			{
				int c = ((CustomPotion) potion).getCustomColor();
				colorLight = "\u00a7" + Integer.toHexString(c + 8 & 15);
				colorDark = "\u00a7" + Integer.toHexString(c);
			}
			else if (potionType.isBadEffect())
			{
				colorDark = BrewingAPI.badEffectColor1;
				colorLight = BrewingAPI.badEffectColor2;
			}
			else
			{
				colorDark = BrewingAPI.goodEffectColor1;
				colorLight = BrewingAPI.goodEffectColor2;
			}
			
			builder.insert(0, colorDark);
			
			if (glowPos >= 0)
			{
				glowPosInt += colorDark.length();
				if (glowPosInt < builder.length())
				{
					builder.insert(glowPosInt, colorLight);
				}
				
				glowPosInt += colorLight.length() + 1;
				if (glowPosInt < builder.length())
				{
					builder.insert(glowPosInt, colorDark);
				}
			}
			
			list.add(builder.toString());
			
			for (IPotionAttribute attribute : potionType.getAttributes())
			{
				list.add(attribute.getDisplayName(potionType));
			}
		}
		
		if (BrewingAPI.advancedPotionInfo && size > 0 && Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))
		{
			if (size == 1)
			{
				for (IPotionType pt : potionTypes)
				{
					if (pt.hasEffect())
					{
						String description = pt.getEffectName() + ".description";
						String localizedDescription = I18n.getString(description);
						if (localizedDescription != description)
						{
							localizedDescription = CSString.cutString(localizedDescription, stack.getDisplayName().length());
							for (String line : CSString.lineArray(localizedDescription))
							{
								list.add("\u00a79\u00a7k" + line);
							}
						}
						else
						{
							list.add("\u00a7c\u00a7k" + I18n.getString("potion.description.missing"));
						}
					}
				}
			}
			else
			{
				int goodEffects = PotionUtils.getGoodEffects(potionTypes);
				float goodEffectsPercentage = (float) goodEffects / (float) potionTypes.size() * 100;
				int badEffects = PotionUtils.getBadEffects(potionTypes);
				float badEffectsPercentage = (float) badEffects / (float) potionTypes.size() * 100;
				int averageAmplifier = PotionUtils.getAverageAmplifier(potionTypes);
				int averageDuration = PotionUtils.getAverageDuration(potionTypes);
				int maxAmplifier = PotionUtils.getMaxAmplifier(potionTypes);
				int maxDuration = PotionUtils.getMaxDuration(potionTypes);
				
				StringBuilder builder = new StringBuilder(20);
				builder.append(EnumChatFormatting.GRAY).append(EnumChatFormatting.ITALIC);
				
				builder.append(I18n.getString("potion.goodeffects")).append(": \u00a79").append(goodEffects);
				builder.append(" (").append(String.format("%.2f", goodEffectsPercentage)).append("%)");
				list.add(builder.toString());
				
				builder.delete(4, builder.length());
				builder.append(I18n.getString("potion.badeffects")).append(": \u00a7a").append(badEffects);
				builder.append(" (").append(String.format("%.2f", badEffectsPercentage)).append("%)");
				list.add(builder.toString());
				
				builder.delete(4, builder.length());
				builder.append(I18n.getString("potion.averageamplifier")).append(": ").append(CSString.convertToRoman(averageAmplifier));
				list.add(builder.toString());
				
				builder.delete(4, builder.length());
				builder.append(I18n.getString("potion.maxamplifier")).append(": ").append(CSString.convertToRoman(maxAmplifier));
				list.add(builder.toString());
				
				builder.delete(4, builder.length());
				builder.append(I18n.getString("potion.averageduration")).append(": ").append(StringUtils.ticksToElapsedTime(averageDuration));
				list.add(builder.toString());
				
				builder.delete(4, builder.length());
				builder.append(I18n.getString("potion.maxduration")).append(": ").append(StringUtils.ticksToElapsedTime(maxDuration));
				list.add(builder.toString());
				
			}
			float f = PotionUtils.getValue(stack);
			if (f > 1F)
			{
				StringBuilder value = new StringBuilder("\u00a77\u00a7k");
				value.append(I18n.getString("potion.value"));
				value.append(": \u00a7e\u00a7k");
				value.append(ItemStack.field_111284_a.format(f));
				
				list.add(value.toString());
			}
		}
		
		if (!attributeMap.isEmpty())
		{
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + I18n.getString("potion.effects.whenDrank"));
			
			for (String key : attributeMap.keys())
			{
				for (AttributeModifier modifier : attributeMap.get(key))
				{
					int operation = modifier.getOperation();
					double amount = modifier.getAmount();
					
					if (operation == 1 || operation == 2)
					{
						amount *= 100.0D;
					}
					
					if (amount > 0.0D)
					{
						list.add(EnumChatFormatting.BLUE + I18n.getStringParams("attribute.modifier.plus." + operation, ItemStack.field_111284_a.format(amount), I18n.getString("attribute.name." + key)));
					}
					else if (amount < 0.0D)
					{
						amount = -amount;
						list.add(EnumChatFormatting.RED + I18n.getStringParams("attribute.modifier.take." + operation, ItemStack.field_111284_a.format(amount), I18n.getString("attribute.name." + key)));
					}
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass)
	{
		if (pass == 0 && !this.isWater(stack))
		{
			List<IPotionType> list = this.getPotionTypes(stack);
			if (list == null || list.isEmpty())
			{
				return false;
			}
			for (IPotionType type : list)
			{
				if (type.hasEffect())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return new CreativeTabs[] { BrewingAPI.potions, CreativeTabs.tabBrewing };
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if (tab == CreativeTabs.tabBrewing || tab == null)
		{
			list.add(new ItemStack(this, 1, 0));
			
			for (IPotionBase base : IPotionBase.bases.values())
			{
				list.add(base.apply(new ItemStack(this, 1, 1)));
				list.add(base.apply(new ItemStack(this, 1, 2)));
			}
			
			for (IPotionType type : IPotionType.effectTypes)
			{
				List<IPotionType> subTypes = type.getSubTypes();
				for (IPotionType subType : subTypes)
				{
					list.add(subType.apply(new ItemStack(this, 1, 1)));
				}
				subTypes = type.onGunpowderUsed().getSubTypes();
				for (IPotionType subType : subTypes)
				{
					list.add(subType.apply(new ItemStack(this, 1, 2)));
				}
			}
			
			if (BrewingAPI.multiPotions && tab == BrewingAPI.potions)
			{
				this.addMultiPotions(list);
			}
		}
	}
	
	public void addMultiPotions(List list)
	{
		ItemStack allEffects1 = new ItemStack(this, 1, 1);
		ItemStack allEffects2 = new ItemStack(this, 1, 2);
		ItemStack good1 = new ItemStack(this, 1, 1);
		ItemStack good2 = new ItemStack(this, 1, 2);
		ItemStack bad1 = new ItemStack(this, 1, 1);
		ItemStack bad2 = new ItemStack(this, 1, 2);
		
		for (IPotionType potionType : IPotionType.combinableTypes)
		{
			if (!potionType.isBadEffect())
			{
				potionType.apply(good1);
				potionType.apply(good2);
			}
			else
			{
				potionType.apply(bad1);
				potionType.apply(bad2);
			}
			
			potionType.apply(allEffects1);
			potionType.apply(allEffects2);
		}
		
		list.add(allEffects1);
		list.add(allEffects2);
		list.add(good1);
		list.add(good2);
		list.add(bad1);
		list.add(bad2);
		
		for (int i = 1; i <= 2; i++)
		{
			for (IPotionType pt1 : IPotionType.combinableTypes)
			{
				for (IPotionType pt2 : IPotionType.combinableTypes)
				{
					if (pt1 != pt2)
					{
						if (this.isSplashDamage(i))
						{
							pt1 = pt1.onGunpowderUsed();
							pt2 = pt2.onGunpowderUsed();
						}
						ItemStack stack = new ItemStack(this, 1, i);
						pt1.apply(stack);
						pt2.apply(stack);
						list.add(stack);
					}
				}
			}
		}
	}
}
