package clashsoft.brewingapi.item;

import java.util.*;

import org.lwjgl.input.Keyboard;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.brewing.PotionBase;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.brewing.PotionUtils;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.lib.AttributeModifierComparator;
import clashsoft.cslib.minecraft.client.CSFontRenderer;
import clashsoft.cslib.minecraft.lang.I18n;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author Clashsoft
 */
public class ItemPotion2 extends Item
{
	public static Comparator<AttributeModifier>		MODIFIER_COMPARATOR	= new AttributeModifierComparator();
	
	public Map<NBTTagCompound, List<PotionType>>	effectCache			= new HashMap();
	
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
	public CreativeTabs[] getCreativeTabs()
	{
		return new CreativeTabs[] {
				BrewingAPI.potions,
				CreativeTabs.tabBrewing,
				CreativeTabs.tabAllSearch };
	}
	
	public List<PotionType> getLegacyEffects(ItemStack stack)
	{
		List<PotionEffect> effects = Items.potionitem.getEffects(stack);
		List<PotionType> potionTypes = new ArrayList(effects.size());
		for (PotionEffect effect : effects)
		{
			potionTypes.add(PotionType.getLegacyPotionType(effect));
		}
		return potionTypes;
	}
	
	/**
	 * Returns a list of potion effects for the specified itemstack.
	 */
	public List<PotionType> getEffects(ItemStack stack)
	{
		List<PotionType> result = new ArrayList();
		if (stack != null && !this.isWater(stack))
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (compound != null)
			{
				if (this.effectCache.containsKey(compound))
				{
					return this.effectCache.get(compound);
				}
				else
				{
					NBTTagList tagList = compound.getTagList("Brewing", Constants.NBT.TAG_COMPOUND);
					
					for (int index = 0; index < tagList.tagCount(); ++index)
					{
						NBTTagCompound potionTypeNBT = tagList.getCompoundTagAt(index);
						PotionType potionType = PotionType.getPotionTypeFromNBT(potionTypeNBT);
						result.add(potionType);
					}
					
					this.effectCache.put(compound, result);
				}
			}
			else
				result = this.getLegacyEffects(stack);
		}
		
		return result;
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
		return iconName.equals("bottle_drinkable") ? BrewingAPI.potion2.bottle : (iconName.equals("bottle_splash") ? BrewingAPI.potion2.splashbottle : (iconName.equals("overlay") ? BrewingAPI.potion2.liquid : null));
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
		return isSplash(stack.getItemDamage());
	}
	
	public boolean isSplash(int metadata)
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
		
		List<PotionType> effects = this.getEffects(stack);
		
		if (effects.isEmpty())
			return 0x0C0CFF;
		
		int[] colors = new int[effects.size()];
		
		for (int j = 0; j < effects.size(); j++)
		{
			PotionType b = effects.get(j);
			colors[j] = b instanceof PotionBase ? 0x0C0CFF : b.getLiquidColor();
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
		List<PotionType> effects = this.getEffects(stack);
		if (effects.size() == 0)
		{
			return false;
		}
		boolean flag = true;
		for (PotionType b : effects)
		{
			flag &= (b.getEffect() != null ? Potion.potionTypes[b.getEffect().getPotionID()].isInstant() : true);
		}
		return flag;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			for (PotionType potionType : this.getEffects(stack))
			{
				if (potionType.hasEffect())
					player.addPotionEffect(potionType.getEffect());
			}
		}
		
		if (!player.capabilities.isCreativeMode)
		{
			--stack.stackSize;
			
			if (stack.stackSize <= 0)
				return this.getGlassBottle();
			else
				player.inventory.addItemStackToInventory(this.getGlassBottle());
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
		return this.isSplash(metadata) ? this.splashbottle : this.bottle;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int metadata)
	{
		return metadata == 0 ? this.liquid : this.getIconFromDamage(metadata);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = this.bottle = iconRegister.registerIcon(this.getIconString() + "_bottle_drinkable");
		this.splashbottle = iconRegister.registerIcon(this.getIconString() + "_bottle_splash");
		this.liquid = iconRegister.registerIcon(this.getIconString() + "_overlay");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		if (pass == 0)
			return this.getLiquidColor(stack);
		else
			return super.getColorFromItemStack(stack, pass);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
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
			List<PotionType> potionTypes = this.getEffects(stack);
			List<PotionBase> baseEffects = new ArrayList(3);
			List<PotionType> effects = new ArrayList(potionTypes.size());
			
			StringBuilder result = new StringBuilder(potionTypes.size() * 20);
			
			if (this.isSplash(stack))
			{
				result.append(I18n.getString("potion.prefix.grenade")).append(" ");
			}
			
			if (!potionTypes.isEmpty())
			{
				if (potionTypes.size() == PotionType.combinableEffects.size())
				{
					result.insert(0, EnumChatFormatting.BLUE.toString()).append(I18n.getString("potion.alleffects.postfix"));
				}
				else
				{
					for (PotionType pt : potionTypes)
					{
						if (pt.isBase())
						{
							baseEffects.add((PotionBase) pt);
						}
						else
						{
							effects.add(pt);
						}
					}
					
					for (PotionBase base : baseEffects)
					{
						result.append(I18n.getString(base.getEffectName())).append(" ");
					}
					if (effects.isEmpty())
					{
						result.append(super.getItemStackDisplayName(stack));
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
							PotionType type = effects.get(i);
							
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
			else
			{
				return super.getItemStackDisplayName(stack);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return BrewingAPI.isClashsoftLibInstalled() ? CSFontRenderer.instance : super.getFontRenderer(stack);
	}
	
	private static int	glowPos	= 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
	{
		if (!this.isWater(stack))
		{
			List<PotionType> potionTypes = this.getEffects(stack);
			Multimap<String, AttributeModifier> hashmultimap = TreeMultimap.create(String.CASE_INSENSITIVE_ORDER, MODIFIER_COMPARATOR);
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
					PotionType potionType = potionTypes.get(i);
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
						builder.append(" ").append(CSString.convertToRoman(potionType.getAmplifier() + 1));
					if (potionType.getDuration() > 20)
						builder.append(" (").append(potionType.getDuration() >= 1000000 ? I18n.getString("potion.infinite") : Potion.getDurationString(potionType.getEffect())).append(")");
					
					int glowPosInt = glowPos / 2;
					
					if (isNormalEffect)
					{
						String colorLight = "";
						String colorDark = "";
						
						if (BrewingAPI.isClashsoftLibInstalled() && potion instanceof clashsoft.cslib.minecraft.potion.CustomPotion && ((clashsoft.cslib.minecraft.potion.CustomPotion) potion).getCustomColor() != -1)
						{
							int c = ((clashsoft.cslib.minecraft.potion.CustomPotion) potion).getCustomColor();
							colorLight = "\u00a7" + Integer.toHexString((c + 8) & 15);
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
					if (potionTypes.size() == 1 && BrewingAPI.isClashsoftLibInstalled() && BrewingAPI.isMorePotionsModInstalled())
					{
						for (PotionType pt : potionTypes)
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
		if (pass == 0)
		{
			List<PotionType> list = this.getEffects(stack);
			return list != null && !list.isEmpty() && list.get(0).getEffect() != null;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if (tab == CreativeTabs.tabBrewing || tab == CreativeTabs.tabAllSearch)
		{
			list.add(new ItemStack(this, 1, 0));
			ItemStack allEffects1 = new ItemStack(this, 1, 1);
			ItemStack allEffects2 = new ItemStack(this, 1, 2);
			ItemStack good1 = new ItemStack(this, 1, 1);
			ItemStack good2 = new ItemStack(this, 1, 2);
			ItemStack bad1 = new ItemStack(this, 1, 1);
			ItemStack bad2 = new ItemStack(this, 1, 2);
			
			for (PotionBase brewing : PotionBase.baseList)
			{
				for (int i = 1; i <= 2; i++)
				{
					list.add(brewing.addPotionTypeToItemStack(new ItemStack(this, 1, i)));
				}
			}
			
			for (PotionType potionType : PotionType.effectMap.values())
			{
				for (int i = 1; i <= 2; i++)
				{
					for (PotionType potionType2 : potionType.getSubTypes())
					{
						PotionType type = new PotionType(potionType2.getEffect(), potionType2.getMaxAmplifier(), potionType2.getMaxDuration(), potionType2.getInverted(), potionType2.getIngredient(), potionType2.getBase());
						if (i == 2 && type != null && type.getEffect() != null && type.getEffect().getPotionID() > 0)
						{
							type.setEffect(new PotionEffect(type.getEffect().getPotionID(), MathHelper.ceiling_double_int(type.getEffect().getDuration() * 0.75D), type.getEffect().getAmplifier()));
						}
						list.add(type.addPotionTypeToItemStack(new ItemStack(this, 1, i)));
					}
				}
			}
			
			if (BrewingAPI.isMorePotionsModInstalled())
			{
				for (PotionType potionType : PotionType.combinableEffects)
				{
					if (!potionType.isBadEffect())
					{
						potionType.addPotionTypeToItemStack(good1);
						potionType.addPotionTypeToItemStack(good2);
					}
					else
					{
						potionType.addPotionTypeToItemStack(bad1);
						potionType.addPotionTypeToItemStack(bad2);
					}
					
					potionType.addPotionTypeToItemStack(allEffects1);
					potionType.addPotionTypeToItemStack(allEffects2);
				}
				
				list.add(allEffects1);
				list.add(allEffects2);
				list.add(good1);
				list.add(good2);
				list.add(bad1);
				list.add(bad2);
			}
		}
		else if (BrewingAPI.multiPotions && BrewingAPI.isMorePotionsModInstalled() && tab == BrewingAPI.potions)
		{
			for (int i = 1; i <= 2; i++)
			{
				for (PotionType pt1 : PotionType.combinableEffects)
				{
					for (PotionType pt2 : PotionType.combinableEffects)
					{
						if (pt1 != pt2)
						{
							PotionType copy1 = pt1.copy();
							PotionType copy2 = pt2.copy();
							if (i == 2 && copy1 != null && copy1.getEffect() != null && copy1.getEffect().getPotionID() > 0)
							{
								copy1.setEffect(new PotionEffect(copy1.getEffect().getPotionID(), MathHelper.ceiling_double_int(copy1.getEffect().getDuration() * 0.75D), copy1.getEffect().getAmplifier()));
							}
							if (i == 2 && copy2 != null && copy2.getEffect() != null && copy2.getEffect().getPotionID() > 0)
							{
								copy2.setEffect(new PotionEffect(copy2.getEffect().getPotionID(), MathHelper.ceiling_double_int(copy2.getEffect().getDuration() * 0.75D), copy2.getEffect().getAmplifier()));
							}
							list.add(copy2.addPotionTypeToItemStack(copy1.addPotionTypeToItemStack(new ItemStack(this, 1, i))));
						}
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
			
			world.playSoundAtEntity((entity), "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			Entity e = new EntityPotion2(world, ((EntityPlayer) entity), stack);
			
			if (!world.isRemote)
			{
				world.spawnEntityInWorld(e);
			}
			
			return e;
		}
		return null;
	}
}
