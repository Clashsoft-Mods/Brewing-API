package clashsoft.brewingapi.item;

import java.util.*;

import org.lwjgl.input.Keyboard;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.brewing.PotionBase;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.brewing.PotionUtils;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.lib.AttributeModifierComparator;
import clashsoft.cslib.util.CSString;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * @author Clashsoft
 */
public class ItemPotion2 extends Item
{
	public static Comparator<AttributeModifier>		MODIFIER_COMPARATOR	= new AttributeModifierComparator();
	
	public Map<NBTTagCompound, List<PotionType>>	effectCache			= new HashMap();
	
	public Icon										bottle;
	public Icon										splashbottle;
	public Icon										liquid;
	
	public ItemPotion2(int par1)
	{
		super(par1);
		this.setMaxStackSize(BrewingAPI.potionStackSize);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabBrewing);
		this.setTextureName("potion");
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return new CreativeTabs[] { BrewingAPI.potions, CreativeTabs.tabBrewing, CreativeTabs.tabAllSearch };
	}
	
	public List<PotionType> getLegacyEffects(ItemStack stack)
	{
		List<PotionEffect> effects = Item.potion.getEffects(stack);
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
		if (stack != null && !this.isWater(stack.getItemDamage()))
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (compound != null)
			{	
				if (this.effectCache.containsKey(compound))
					return this.effectCache.get(compound);
				else
				{
					NBTTagList tagList = compound.getTagList("Brewing");
					
					for (int index = 0; index < tagList.tagCount(); ++index)
					{
						NBTTagCompound potionTypeNBT = (NBTTagCompound) tagList.tagAt(index);
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
	
	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (!par3EntityPlayer.capabilities.isCreativeMode)
		{
			--par1ItemStack.stackSize;
		}
		
		if (!par2World.isRemote)
		{
			List var4 = this.getEffects(par1ItemStack);
			
			if (var4 != null)
			{
				Iterator var5 = var4.iterator();
				
				while (var5.hasNext())
				{
					PotionType var6 = (PotionType) var5.next();
					if (var6.getEffect() != null)
					{
						par3EntityPlayer.addPotionEffect(var6.getEffect());
					}
				}
			}
		}
		
		if (!par3EntityPlayer.capabilities.isCreativeMode)
		{
			if (par1ItemStack.stackSize <= 0)
			{
				return new ItemStack(Item.glassBottle);
			}
			
			par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.glassBottle));
		}
		
		return par1ItemStack;
	}
	
	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 32;
	}
	
	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.drink;
	}
	
	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (this.isSplash(par1ItemStack.getItemDamage()))
		{
			if (!par3EntityPlayer.capabilities.isCreativeMode)
			{
				--par1ItemStack.stackSize;
			}
			
			par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			
			if (!par2World.isRemote)
			{
				par2World.spawnEntityInWorld(new EntityPotion2(par2World, par3EntityPlayer, par1ItemStack));
			}
			
			return par1ItemStack;
		}
		else
		{
			par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
			return par1ItemStack;
		}
	}
	
	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public Icon getIconFromDamage(int par1)
	{
		return this.isSplash(par1) ? this.splashbottle : this.bottle;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(ItemStack par1ItemStack, int par2)
	{
		return par2 == 0 ? this.liquid : (this.isSplash(par1ItemStack.getItemDamage()) ? this.splashbottle : this.bottle);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = this.bottle = par1IconRegister.registerIcon(this.getIconString() + "_bottle_drinkable");
		this.splashbottle = par1IconRegister.registerIcon(this.getIconString() + "_bottle_splash");
		this.liquid = par1IconRegister.registerIcon(this.getIconString() + "_overlay");
	}
	
	@SideOnly(Side.CLIENT)
	public static Icon func_94589_d(String par0Str)
	{
		return par0Str.equals("bottle_drinkable") ? BrewingAPI.potion2.bottle : (par0Str.equals("bottle_splash") ? BrewingAPI.potion2.splashbottle : (par0Str.equals("overlay") ? BrewingAPI.potion2.liquid : null));
	}
	
	/**
	 * returns wether or not a potion is a throwable splash potion based on
	 * damage value
	 */
	public boolean isSplash(int par1)
	{
		return par1 == 2 ? true : par1 == 1 ? false : ItemPotion.isSplash(par1);
	}
	
	public boolean isWater(int par1)
	{
		return par1 == 0;
	}
	
	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		if (par2 == 0 && par1ItemStack != null)
		{
			if (this.isWater(par1ItemStack.getItemDamage()))
			{
				return 0x0C0CFF;
			}
			List<PotionType> effects = this.getEffects(par1ItemStack);
			if (effects != null && effects.size() > 0)
			{
				int[] i1 = new int[effects.size()];
				
				for (int j = 0; j < effects.size(); j++)
				{
					PotionType b = effects.get(j);
					i1[j] = b instanceof PotionBase ? 0x0C0CFF : b.getLiquidColor();
				}
				return PotionUtils.combineColors(i1);
			}
			else
			{
				return 0x0C0CFF;
			}
		}
		else
		{
			return 16777215;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int i)
	{
		return 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack)
	{
		List effects = this.getEffects(par1ItemStack);
		if (this.isWater(par1ItemStack.getItemDamage()))
		{
			return StatCollector.translateToLocal("item.emptyPotion.name").trim();
		}
		else
		{
			String var2 = "";
			
			if (this.isSplash(par1ItemStack.getItemDamage()))
			{
				var2 = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
			}
			
			List<PotionType> var3 = this.getEffects(par1ItemStack);
			String var4 = "";
			
			if (var3 != null && !var3.isEmpty())
			{
				if (var3.size() == PotionType.combinableEffects.size())
				{
					return "\u00a7b" + var2 + StatCollector.translateToLocal("potion.alleffects.postfix");
				}
				else if (var3.size() > 3)
				{
					return var2 + StatCollector.translateToLocal("potion.potionof") + " " + var3.size() + " " + StatCollector.translateToLocal("potion.effects");
				}
				else if (var3.get(0).isBase())
				{
					return StatCollector.translateToLocal("potion.prefix." + ((PotionBase) var3.get(0)).basename).trim() + " " + var2 + super.getItemDisplayName(par1ItemStack);
				}
				for (int i = 0; i < var3.size(); i++)
				{
					if (i == 0)
					{
						var4 = StatCollector.translateToLocal(var3.get(i).getEffect() != null && var3.get(i).getEffect().getPotionID() > 0 ? (var3.get(i).getEffect().getEffectName() + ".postfix") : "");
						var2 += StatCollector.translateToLocal(var4).trim();
					}
					else if (i + 1 == var3.size())
					{
						var4 = var3.get(i).getEffect().getEffectName();
						var2 += " " + StatCollector.translateToLocal("potion.and") + " " + StatCollector.translateToLocal(var4).trim();
					}
					else
					{
						var4 = var3.get(i).getEffect().getEffectName();
						var2 += ", " + StatCollector.translateToLocal(var4).trim();
					}
				}
				return var2;
			}
			else
			{
				return super.getItemDisplayName(par1ItemStack);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return super.getFontRenderer(stack);
	}
	
	private static int	glowPos	= 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		if (!this.isWater(par1ItemStack.getItemDamage()))
		{
			List<PotionType> potionTypes = this.getEffects(par1ItemStack);
			Multimap<String, AttributeModifier> hashmultimap = TreeMultimap.create(String.CASE_INSENSITIVE_ORDER, MODIFIER_COMPARATOR);
			
			if (potionTypes != null && !potionTypes.isEmpty())
			{
				int maxGlowPos = this.getItemDisplayName(par1ItemStack).length() + 10;
				glowPos++;
				if (glowPos > maxGlowPos * 4)
					glowPos = 0;
				for (int i = 0; i < potionTypes.size(); i++)
				{
					PotionType potionType = potionTypes.get(i);
					boolean isNormalEffect = potionType.getEffect() != null && potionType.getEffect().getPotionID() > 0;
					String effectName = (isNormalEffect ? StatCollector.translateToLocal(potionType.getEffect().getEffectName()) : "\u00a77" + StatCollector.translateToLocal("potion.empty")).trim();
					StringBuilder builder = new StringBuilder(effectName);
					
					if (potionType.getEffect() != null)
					{
						Potion potion = Potion.potionTypes[potionType.getEffect().getPotionID()];
						Map map = potion.func_111186_k();
						
						if (map != null && map.size() > 0)
						{
							for (Object object : map.keySet())
							{
								AttributeModifier attributemodifier = (AttributeModifier) map.get(object);
								if (attributemodifier != null)
								{
									AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potionType.getEffect().getAmplifier(), attributemodifier), attributemodifier.getOperation());
									hashmultimap.put(((Attribute) object).getAttributeUnlocalizedName(), attributemodifier1);
								}
							}
						}
					}
					
					if (potionType.getEffect() != null && potionType.getEffect().getAmplifier() > 0)
						builder.append(" ").append(CSString.convertToRoman(potionType.getEffect().getAmplifier() + 1));
					if (potionType.getEffect() != null && potionType.getEffect().getDuration() > 20)
						builder.append(" (").append(potionType.getEffect().getDuration() >= 1000000 ? StatCollector.translateToLocal("potion.infinite") : Potion.getDurationString(potionType.getEffect())).append(")");
					
					int glowPosInt = glowPos / 4;
					if (glowPosInt >= builder.length())
						glowPosInt = builder.length();
					
					String glowString1 = builder.substring(0, glowPosInt);
					String glowString2 = glowPosInt < builder.length() ? String.valueOf(builder.charAt(glowPosInt)) : "";
					String glowString3 = glowPosInt + 1 < builder.length() ? builder.substring(glowPosInt + 1) : "";
					
					if (isNormalEffect)
					{
						builder.delete(0, builder.length());
						String colorLight = "";
						String colorDark = "";
						if (BrewingAPI.isClashsoftLibInstalled() && potionType.getEffect() != null && Potion.potionTypes[potionType.getEffect().getPotionID()] instanceof clashsoft.cslib.minecraft.CustomPotion && ((clashsoft.cslib.minecraft.CustomPotion) (Potion.potionTypes[potionType.getEffect().getPotionID()])).getCustomColor() >= 0)
						{
							int c = ((clashsoft.cslib.minecraft.CustomPotion) Potion.potionTypes[potionType.getEffect().getPotionID()]).getCustomColor();
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
						builder.append(colorDark).append(glowString1).append(colorLight).append(glowString2).append(colorDark).append(glowString3);
					}
					list.add(builder.toString());
				}
				/*
				 * Advanced Potion Info
				 */
				if (BrewingAPI.advancedPotionInfo && Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))
				{
					List<String> usedTo = PotionUtils.getUsedTo(par1ItemStack);
					if (!usedTo.isEmpty() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
					{
						list.add(StatCollector.translateToLocal("potion.useto") + ":");
						list.addAll(usedTo);
					}
					else
					{
						if (potionTypes.size() == 1 && BrewingAPI.isClashsoftLibInstalled() && BrewingAPI.isMorePotionsModInstalled())
						{
							for (PotionType b : potionTypes)
							{
								if (b.getEffect() != null)
								{
									String desc = b.getEffect().getEffectName() + ".description";
									String s = StatCollector.translateToLocal(desc);
									if (s != desc)
									{
										s = clashsoft.cslib.util.CSString.cutString(s, par1ItemStack.getDisplayName().length());
										for (String s1 : clashsoft.cslib.util.CSString.makeLineList(s))
										{
											list.add(EnumChatFormatting.BLUE + "" + EnumChatFormatting.ITALIC + s1);
										}
									}
									else
									{
										list.add(EnumChatFormatting.RED + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal("potion.description.missing"));
									}
								}
							}
						}
						if (potionTypes.size() > 1)
						{
							String green = (EnumChatFormatting.GREEN) + "\u00a7o";
							String red = (EnumChatFormatting.RED) + "\u00a7o";
							
							int goodEffects = PotionUtils.getGoodEffects(potionTypes);
							float goodEffectsPercentage = (float) goodEffects / (float) potionTypes.size() * 100;
							int badEffects = PotionUtils.getBadEffects(potionTypes);
							float badEffectsPercentage = (float) badEffects / (float) potionTypes.size() * 100;
							int averageAmplifier = PotionUtils.getAverageAmplifier(potionTypes);
							int averageDuration = PotionUtils.getAverageDuration(potionTypes);
							int maxAmplifier = PotionUtils.getMaxAmplifier(potionTypes);
							int maxDuration = PotionUtils.getMaxDuration(potionTypes);
							
							if (goodEffects > 1)
								list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.goodeffects") + ": " + green + goodEffects + " (" + String.format("%.1f", goodEffectsPercentage) + "%)");
							if (badEffects > 1)
								list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.negativeEffects") + ": " + red + badEffects + " (" + String.format("%.1f", badEffectsPercentage) + "%)");
							if (averageAmplifier > 0)
								list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.averageamplifier") + ": " + (EnumChatFormatting.DARK_GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.potency." + averageAmplifier));
							list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.averageduration") + ": " + (EnumChatFormatting.DARK_GRAY) + "\u00a7o" + Potion.getDurationString(new PotionEffect(0, averageDuration, 0)));
							if (maxAmplifier > 0)
								list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.highestamplifier") + ": " + (EnumChatFormatting.DARK_GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.potency." + maxAmplifier));
							list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.highestduration") + ": " + (EnumChatFormatting.DARK_GRAY) + "\u00a7o" + Potion.getDurationString(new PotionEffect(0, maxDuration, 0)));
						}
						if (PotionType.getExperience(par1ItemStack) > 0.3F)
						{
							list.add((EnumChatFormatting.GRAY) + "\u00a7o" + StatCollector.translateToLocal("potion.value") + ": " + (EnumChatFormatting.YELLOW) + "\u00a7o" + String.format("%.2f", (PotionType.getExperience(par1ItemStack) * 100F) / 270.870F));
						}
					}
				}
				/*
				 * Attribute List
				 */
				if (!hashmultimap.isEmpty())
				{
					list.add("");
					list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
					
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
								list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + operation, new Object[] { ItemStack.field_111284_a.format(amount), StatCollector.translateToLocal("attribute.name." + key) }));
							else if (amount < 0.0D)
							{
								amount *= -1.0D;
								list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + operation, new Object[] { ItemStack.field_111284_a.format(amount), StatCollector.translateToLocal("attribute.name." + key) }));
							}
						}
					}
				}
			}
			else
			{
				String var6 = StatCollector.translateToLocal("potion.empty").trim();
				list.add("\u00a77" + var6);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack, int par1)
	{
		List var2 = this.getEffects(par1ItemStack);
		return var2 != null && !var2.isEmpty() && ((PotionType) var2.get(0)).getEffect() != null && par1 == 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	public void getSubItems(int itemID, CreativeTabs tab, List list)
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
					list.add(brewing.addBrewingToItemStack(new ItemStack(this, 1, i)));
				}
			}
			for (PotionType potionType : PotionType.effectMap.values())
			{
				for (int i = 1; i <= 2; i++)
				{
					for (PotionType brewing2 : potionType.getSubTypes())
					{
						PotionType var1 = new PotionType(brewing2.getEffect(), brewing2.getMaxAmplifier(), brewing2.getMaxDuration(), brewing2.getInverted(), brewing2.getIngredient(), brewing2.getBase());
						if (i == 2 && var1 != null && var1.getEffect() != null && var1.getEffect().getPotionID() > 0)
						{
							var1.setEffect(new PotionEffect(var1.getEffect().getPotionID(), MathHelper.ceiling_double_int(var1.getEffect().getDuration() * 0.75D), var1.getEffect().getAmplifier()));
						}
						list.add(var1.addBrewingToItemStack(new ItemStack(this, 1, i)));
					}
				}
			}
			
			if (BrewingAPI.isMorePotionsModInstalled())
			{
				for (PotionType potionType : PotionType.combinableEffects)
				{
					if (!potionType.isBadEffect())
					{
						potionType.addBrewingToItemStack(good1);
						potionType.addBrewingToItemStack(good2);
					}
					else
					{
						potionType.addBrewingToItemStack(bad1);
						potionType.addBrewingToItemStack(bad2);
					}
					
					potionType.addBrewingToItemStack(allEffects1);
					potionType.addBrewingToItemStack(allEffects2);
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
				for (PotionType brewing1 : PotionType.combinableEffects)
				{
					for (PotionType brewing2 : PotionType.combinableEffects)
					{
						if (brewing1 != brewing2)
						{
							PotionType var1 = new PotionType(brewing1.getEffect(), brewing1.getMaxAmplifier(), brewing1.getMaxDuration(), brewing1.getInverted(), brewing1.getIngredient(), brewing1.getBase());
							PotionType var2 = new PotionType(brewing2.getEffect(), brewing2.getMaxAmplifier(), brewing2.getMaxDuration(), brewing2.getInverted(), brewing2.getIngredient(), brewing2.getBase());
							if (i == 2 && var1 != null && var1.getEffect() != null && var1.getEffect().getPotionID() > 0)
							{
								var1.setEffect(new PotionEffect(var1.getEffect().getPotionID(), MathHelper.ceiling_double_int(var1.getEffect().getDuration() * 0.75D), var1.getEffect().getAmplifier()));
							}
							if (i == 2 && var2 != null && var2.getEffect() != null && var2.getEffect().getPotionID() > 0)
							{
								var2.setEffect(new PotionEffect(var2.getEffect().getPotionID(), MathHelper.ceiling_double_int(var2.getEffect().getDuration() * 0.75D), var2.getEffect().getAmplifier()));
							}
							list.add(var2.addBrewingToItemStack(var1.addBrewingToItemStack(new ItemStack(this, 1, i))));
						}
					}
				}
			}
		}
	}
	
	public boolean isEffectInstant(ItemStack stack)
	{
		List<PotionType> effects = this.getEffects(stack);
		if (effects.size() == 0)
			return false;
		boolean flag = true;
		for (PotionType b : effects)
			flag &= (b.getEffect() != null ? Potion.potionTypes[b.getEffect().getPotionID()].isInstant() : true);
		return flag;
	}
	
	@Override
	public Entity createEntity(World world, Entity entity, ItemStack itemstack)
	{
		if (entity instanceof EntityPlayer && this.isSplash(itemstack.getItemDamage()))
		{
			if (!((EntityPlayer) entity).capabilities.isCreativeMode)
			{
				--itemstack.stackSize;
			}
			
			world.playSoundAtEntity((entity), "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			Entity e = new EntityPotion2(world, ((EntityPlayer) entity), itemstack);
			
			if (!world.isRemote)
			{
				world.spawnEntityInWorld(e);
			}
			
			return e;
		}
		return null;
	}
	
	public Icon getSplashIcon(ItemStack stack)
	{
		return this.splashbottle;
	}
}
