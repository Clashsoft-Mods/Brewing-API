package clashsoft.brewingapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import clashsoft.brewingapi.block.BlockBrewingStand2;
import clashsoft.brewingapi.command.CommandPotion;
import clashsoft.brewingapi.common.BAPIProxy;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.item.ItemGlassBottle2;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.lib.PotionDispenser;
import clashsoft.brewingapi.network.BAPINetHandler;
import clashsoft.brewingapi.potion.IIngredientHandler;
import clashsoft.brewingapi.potion.IPotionEffectHandler;
import clashsoft.brewingapi.potion.IPotionList;
import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import clashsoft.cslib.minecraft.CSLib;
import clashsoft.cslib.minecraft.ClashsoftMod;
import clashsoft.cslib.minecraft.block.CSBlocks;
import clashsoft.cslib.minecraft.command.CSCommand;
import clashsoft.cslib.minecraft.entity.CSEntities;
import clashsoft.cslib.minecraft.item.CSItems;
import clashsoft.cslib.minecraft.potion.CustomPotion;
import clashsoft.cslib.minecraft.update.CSUpdate;
import clashsoft.cslib.minecraft.util.CSConfig;
import clashsoft.cslib.reflect.CSReflection;
import clashsoft.cslib.util.CSLog;
import clashsoft.cslib.util.CSUtil;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

@Mod(modid = BrewingAPI.MODID, name = BrewingAPI.NAME, version = BrewingAPI.VERSION, dependencies = BrewingAPI.DEPENDENCIES)
public class BrewingAPI extends ClashsoftMod
{
	public static final String		MODID					= "brewingapi";
	public static final String		NAME					= "Brewing API";
	public static final String		ACRONYM					= "bapi";
	public static final String		VERSION					= CSUpdate.CURRENT_VERSION + "-1.0.1";
	public static final String		DEPENDENCIES			= CSLib.DEPENDENCY;
	
	@Instance(MODID)
	public static BrewingAPI		instance;
	
	@SidedProxy(clientSide = "clashsoft.brewingapi.client.BAPIClientProxy", serverSide = "clashsoft.brewingapi.common.BAPIProxy")
	public static BAPIProxy			proxy;
	
	static
	{
		if (proxy == null)
		{
			proxy = createProxy("clashsoft.brewingapi.client.BAPIClientProxy", "clashsoft.brewingapi.common.BAPIProxy");
		}
	}
	
	// API Stuff
	
	private static Boolean			mpmInstalled			= null;
	
	public static boolean			multiPotions			= false;
	public static boolean			advancedPotionInfo		= false;
	public static boolean			showAllBaseTypes		= false;
	public static boolean			defaultAwkwardBrewing	= false;
	public static int				potionStackSize			= 1;
	
	public static int				brewingStand2ID			= 11;
	public static int				splashPotion2ID			= EntityRegistry.findGlobalUniqueEntityId();
	
	public static CreativeTabs		potions;
	
	public static Block				brewingStand2;
	public static Item				brewingStandItem2;
	
	public static ItemPotion2		potion2;
	public static ItemGlassBottle2	glassBottle2;
	
	static
	{
		CustomPotion.expandPotionList(64);
	}
	
	public BrewingAPI()
	{
		super(proxy, MODID, NAME, ACRONYM, VERSION);
		this.hasConfig = true;
		this.netHandlerClass = BAPINetHandler.class;
		this.eventHandler = this;
		this.url = "https://github.com/Clashsoft/Brewing-API/wiki/";
	}
	
	@Override
	public void readConfig()
	{
		brewingStand2ID = CSConfig.getTileEntity("Brewing Stand", 11);
		
		multiPotions = CSConfig.getBool("potions", "MultiPotions", multiPotions);
		advancedPotionInfo = CSConfig.getBool("potions", "Advanced Potion Info", advancedPotionInfo);
		showAllBaseTypes = CSConfig.getBool("potions", "Show All Base Potion Types", showAllBaseTypes);
		defaultAwkwardBrewing = CSConfig.getBool("potions", "Default Awkward Brewing", defaultAwkwardBrewing);
		potionStackSize = CSConfig.getInt("potions", "PotionStackSize", potionStackSize);
	}
	
	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		
		brewingStand2 = new BlockBrewingStand2().setBlockName("brewingStand").setHardness(0.5F).setLightLevel(0.125F);
		brewingStandItem2 = new ItemReed(brewingStand2).setUnlocalizedName("brewingStand").setTextureName("brewing_stand").setCreativeTab(CreativeTabs.tabBrewing);
		potion2 = (ItemPotion2) new ItemPotion2().setUnlocalizedName("potion");
		glassBottle2 = (ItemGlassBottle2) new ItemGlassBottle2().setUnlocalizedName("glassBottle").setTextureName("potion_bottle_empty");
		
		CSBlocks.replaceBlock(Blocks.brewing_stand, brewingStand2);
		CSItems.replaceItem(Items.brewing_stand, brewingStandItem2);
		CSItems.replaceItem(Items.potionitem, potion2);
		CSItems.replaceItem(Items.glass_bottle, glassBottle2);
	}
	
	@Override
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		
		PotionList.init();
		
		if (multiPotions)
		{
			potions = new CreativeTabs("mixedpotions")
			{
				ItemStack	stack	= null;
				
				@Override
				public ItemStack getIconItemStack()
				{
					return this.stack == null ? this.stack = PotionList.damageBoost.apply(new ItemStack(BrewingAPI.potion2, 0, 1)) : this.stack;
				}
				
				@Override
				public Item getTabIconItem()
				{
					return null;
				}
			};
		}
		
		GameRegistry.registerTileEntity(TileEntityBrewingStand2.class, "BrewingStand2");
		CSEntities.replace("ThrownPotion", 16, EntityPotion2.class);
		
		CSCommand.registerCommand(new CommandPotion());
		BlockDispenser.dispenseBehaviorRegistry.putObject(potion2, new PotionDispenser());
	}
	
	@Override
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
	
	public static boolean isMorePotionsModInstalled()
	{
		if (mpmInstalled == null)
		{
			boolean b = CSUtil.checkClass("clashsoft.mods.morepotions.MorePotionsMod");
			mpmInstalled = Boolean.valueOf(b);
			return b;
		}
		return mpmInstalled.booleanValue();
	}
	
	// API
	
	public static boolean						hasLoaded			= false;
	
	public static List<IPotionEffectHandler>	effectHandlers		= new LinkedList<IPotionEffectHandler>();
	public static List<IIngredientHandler>		ingredientHandlers	= new LinkedList<IIngredientHandler>();
	
	public static IPotionType addPotionType(IPotionType potionType)
	{
		return potionType.register();
	}
	
	public static void setPotionList(IPotionList potionList)
	{
		PotionList.instance = potionList;
	}
	
	public static void registerIngredientHandler(IIngredientHandler handler)
	{
		if (ingredientHandlers.contains(handler))
		{
			CSLog.info("Ingredient handler \"" + handler + "\" registered");
			ingredientHandlers.add(handler);
		}
	}
	
	public static void registerEffectHandler(IPotionEffectHandler handler)
	{
		if (!effectHandlers.contains(handler))
		{
			CSLog.info("Effect handler \"" + handler + "\" registered");
			effectHandlers.add(handler);
		}
	}
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase living = event.entityLiving;
		if (living != null && !living.worldObj.isRemote)
		{
			Collection<PotionEffect> c = living.getActivePotionEffects();
			List<PotionEffect> potionEffects = new ArrayList(c);
			
			for (PotionEffect effect : potionEffects)
			{
				for (IPotionEffectHandler handler : effectHandlers)
				{
					if (handler.canHandle(effect))
					{
						handler.onPotionUpdate(living.deathTime, living, effect);
					}
				}
			}
		}
	}

}
