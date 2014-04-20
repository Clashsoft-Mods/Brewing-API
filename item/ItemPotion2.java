package clashsoft.brewingapi.item;

import java.util.*;

import org.lwjgl.input.Keyboard;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.lib.AttributeModifierComparator;
import clashsoft.brewingapi.potion.PotionUtils;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.lang.I18n;
import clashsoft.cslib.minecraft.potion.CustomPotion;
import clashsoft.cslib.util.CSString;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	public Map<NBTTagCompound, List<IPotionType>>	effectCache	= new HashMap();
	
	public IIcon									bottle;
	public IIcon									splashbottle;
	public IIcon									liquid;
	
	public ItemPotion2()
	{
		this.setMaxStackSize(BrewingAPI.potionStackSize);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabBrewing);
	}
	
	@Override
	public List<PotionEffect> getEffects(ItemStack stack)
	{
		if (stack == null || this.isWater(stack))
		{
			return Collections.EMPTY_LIST;
		}
		
		List<PotionEffect> effects = new LinkedList();
		List<IPotionType> types = this.getPotionTypes(stack);
		
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
	
	public List<IPotionType> getLegacyEffects(ItemStack stack)
	{
		List<PotionEffect> effects = super.getEffects(stack);
		List<IPotionType> types = new LinkedList();
		for (PotionEffect effect : effects)
		{
			types.add(PotionType.getFromEffect(effect));
		}
		return types;
	}
	
	/**
	 * Returns a list of potion effects for the specified itemstack.
	 */
	public List<IPotionType> getPotionTypes(ItemStack stack)
	{
		if (stack == null || this.isWater(stack))
		{
			return Collections.EMPTY_LIST;
		}
		
		NBTTagCompound compound = stack.getTagCompound();
		if (compound != null)
		{
			if (this.effectCache.containsKey(compound))
			{
				return this.effectCache.get(compound);
			}
			else
			{
				List<IPotionType> result = PotionType.getPotionTypes(stack);
				this.effectCache.put(compound, result);
				return result;
			}
		}
		else
		{
			return this.getLegacyEffects(stack);
		}
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
		return splash ? metadata | 2 : metadata & ~2;
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
		
		if (effects.isEmpty())
		{
			return 0x0C0CFF;
		}
		
		int[] colors = new int[effects.size()];
		
		for (int j = 0; j < effects.size(); j++)
		{
			IPotionType b = effects.get(j);
			colors[j] = b.getLiquidColor();
		}
		return PotionUtils.combineColors(colors);
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
		else
		{
			List<IPotionType> potionTypes = this.getPotionTypes(stack);
			List<IPotionType> effects = new ArrayList();
			List<PotionBase> bases = new ArrayList();
			
			StringBuilder result = new StringBuilder(potionTypes.size() * 20);
			
			if (this.isSplash(stack))
			{
				result.append(I18n.getString("potion.prefix.grenade")).append(" ");
			}
			
			if (!potionTypes.isEmpty())
			{
				if (potionTypes.size() == IPotionType.combinableTypes.size())
				{
					result.insert(0, EnumChatFormatting.BLUE.toString()).append(I18n.getString("potion.alleffects.postfix"));
				}
				else
				{
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
				}
				return result.toString();
			}
		}
		return StatCollector.translateToLocal("item.potion.name");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return /*
				 * BrewingAPI.isClashsoftLibInstalled() ?
				 * CSFontRenderer.instance :
				 */null;
	}
	
	private static int	glowPos	= 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
	{
		if (!this.isWater(stack))
		{
			List<IPotionType> potionTypes = this.getPotionTypes(stack);
			Multimap<String, AttributeModifier> hashmultimap = TreeMultimap.create(String.CASE_INSENSITIVE_ORDER, AttributeModifierComparator.instance);
			int size = potionTypes.size();
			
			if (size > 0)
			{
				glowPos++;
				if (glowPos > 100)
				{
					glowPos = 0;
				}
				
				if (size > 5)
				{
					glowPos = -1;
				}
				
				for (int i = 0; i < size; i++)
				{
					IPotionType potionType = potionTypes.get(i);
					Potion potion = potionType.getPotion();
					
					boolean isNormalEffect = !potionType.isBase();
					String effectName;
					
					if (!isNormalEffect)
					{
						if (size > 1)
						{
							continue;
						}
						else
						{
							effectName = EnumChatFormatting.GRAY + I18n.getString("potion.empty");
						}
					}
					else
					{
						effectName = I18n.getString(potionType.getEffectName());
					}
					
					StringBuilder builder = new StringBuilder(effectName);
					
					if (potion != null)
					{
						Map map = potion.func_111186_k();
						
						if (map != null && map.size() > 0)
						{
							for (Object object : map.keySet())
							{
								AttributeModifier attributemodifier = (AttributeModifier) map.get(object);
								if (attributemodifier != null)
								{
									AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potionType.getEffect().getAmplifier(), attributemodifier), attributemodifier.getOperation());
									hashmultimap.put(((BaseAttribute) object).getAttributeUnlocalizedName(), attributemodifier1);
								}
							}
						}
					}
					
					if (potionType.getAmplifier() > 0)
					{
						builder.append(" ").append(CSString.convertToRoman(potionType.getAmplifier() + 1));
					}
					if (potionType.getDuration() > 20)
					{
						builder.append(" (").append(potionType.getDuration() >= 1000000 ? I18n.getString("potion.infinite") : Potion.getDurationString(potionType.getEffect())).append(")");
					}
					
					int glowPosInt = glowPos / 2;
					
					if (isNormalEffect)
					{
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
							colorLight = EnumChatFormatting.RED.toString();
							colorDark = EnumChatFormatting.DARK_RED.toString();
						}
						else
						{
							colorLight = EnumChatFormatting.GREEN.toString();
							colorDark = EnumChatFormatting.DARK_GREEN.toString();
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
						
					}
					list.add(builder.toString());
				}
				
				if (BrewingAPI.advancedPotionInfo && Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))
				{
					if (potionTypes.size() == 1 && BrewingAPI.isMorePotionsModInstalled())
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
										list.add(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC.toString() + line);
									}
								}
								else
								{
									list.add(EnumChatFormatting.RED.toString() + EnumChatFormatting.ITALIC.toString() + I18n.getString("potion.description.missing"));
								}
							}
						}
					}
					if (potionTypes.size() > 1)
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
						
						builder.append(I18n.getString("potion.goodeffects")).append(": ").append(EnumChatFormatting.GREEN).append(goodEffects);
						builder.append(" (").append(String.format("%.2f", goodEffectsPercentage)).append("%)");
						list.add(builder.toString());
						
						builder.delete(4, builder.length());
						builder.append(I18n.getString("potion.badeffects")).append(": ").append(EnumChatFormatting.RED).append(badEffects);
						builder.append(" (").append(String.format("%.2f", badEffectsPercentage)).append("%)");
						list.add(builder.toString());
						
						builder.delete(4, builder.length());
						builder.append(I18n.getString("potion.averageamplifier")).append(": ").append(CSString.convertToRoman(averageAmplifier));
						list.add(builder.toString());
						
						builder.delete(4, builder.length());
						builder.append(I18n.getString("potion.maxamplifier")).append(": ").append(CSString.convertToRoman(averageAmplifier));
						list.add(builder.toString());
						
						builder.delete(4, builder.length());
						builder.append(I18n.getString("potion.averageduration")).append(": ").append(StringUtils.ticksToElapsedTime(averageDuration));
						list.add(builder.toString());
						
						builder.delete(4, builder.length());
						builder.append(I18n.getString("potion.maxduration")).append(": ").append(StringUtils.ticksToElapsedTime(maxDuration));
						list.add(builder.toString());
						
					}
					if (PotionType.getExperience(stack) > 0.3F)
					{
						StringBuilder value = new StringBuilder(20);
						value.append(EnumChatFormatting.GRAY).append(EnumChatFormatting.ITALIC);
						value.append(I18n.getString("potion.value"));
						value.append(": ");
						value.append(EnumChatFormatting.YELLOW).append(EnumChatFormatting.ITALIC);
						value.append(ItemStack.field_111284_a.format(PotionUtils.getValue(stack)));
						
						list.add(value.toString());
					}
				}
				
				if (!hashmultimap.isEmpty())
				{
					list.add("");
					list.add(EnumChatFormatting.DARK_PURPLE + I18n.getString("potion.effects.whenDrank"));
					
					for (String key : hashmultimap.keys())
					{
						for (AttributeModifier modifier : hashmultimap.get(key))
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
			else
			{
				String empty = I18n.getString("potion.empty").trim();
				list.add("\u00a77" + empty);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass)
	{
		if (pass == 0 && stack.getItemDamage() > 0)
		{
			List<IPotionType> list = this.getPotionTypes(stack);
			return list != null && !list.isEmpty() && list.get(0).getEffect() != null;
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
			
			for (PotionBase pt : PotionBase.baseList)
			{
				list.add(pt.apply(new ItemStack(this, 1, 1)));
				list.add(pt.apply(new ItemStack(this, 1, 2)));
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
			
			if (BrewingAPI.isMorePotionsModInstalled() && BrewingAPI.multiPotions && tab == BrewingAPI.potions)
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
	
	@Override
	public Entity createEntity(World world, Entity entity, ItemStack stack)
	{
		if (entity instanceof EntityPlayer && this.isSplash(stack))
		{
			if (!((EntityPlayer) entity).capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}
			
			world.playSoundAtEntity(entity, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			Entity e = new EntityPotion2(world, (EntityPlayer) entity, stack);
			
			if (!world.isRemote)
			{
				world.spawnEntityInWorld(e);
			}
			
			return e;
		}
		return null;
	}
}
