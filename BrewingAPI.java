package clashsoft.brewingapi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import clashsoft.brewingapi.block.BlockBrewingStand2;
import clashsoft.brewingapi.command.CommandGivePotion;
import clashsoft.brewingapi.common.BAPICommonProxy;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.item.ItemBrewingStand2;
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
import clashsoft.cslib.minecraft.block.CSBlocks;
import clashsoft.cslib.minecraft.item.CSItems;
import clashsoft.cslib.minecraft.update.CSUpdate;
import clashsoft.cslib.minecraft.util.CSConfig;
import clashsoft.cslib.reflect.CSReflection;
import clashsoft.cslib.util.CSLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

@Mod(modid = BrewingAPI.MODID, name = BrewingAPI.NAME, version = BrewingAPI.VERSION)
public class BrewingAPI
{
	public static final String		MODID					= "brewingapi";
	public static final String		NAME					= "Brewing API";
	public static final String		ACRONYM					= "bapi";
	public static final int			REVISION				= 0;
	public static final String		VERSION					= CSUpdate.CURRENT_VERSION + "-" + REVISION;
	
	@Instance(MODID)
	public static BrewingAPI		instance;
	
	@SidedProxy(clientSide = "clashsoft.brewingapi.client.BAPIClientProxy", serverSide = "clashsoft.brewingapi.common.BAPICommonProxy")
	public static BAPICommonProxy	proxy;
	
	public static BAPINetHandler	netHandler				= new BAPINetHandler();
	
	// API Stuff
	
	private static boolean			MORE_POTIONS_MOD		= false;
	private static boolean			CLASHSOFT_LIB			= false;
	
	public static boolean			multiPotions			= false;
	public static boolean			advancedPotionInfo		= false;
	public static boolean			showAllBaseTypes		= false;
	public static boolean			defaultAwkwardBrewing	= false;
	public static int				potionStackSize			= 1;
	
	public static int				brewingStand2ID			= 11;
	public static int				splashPotion2ID			= EntityRegistry.findGlobalUniqueEntityId();
	
	public static CreativeTabs		potions;
	
	public static Block				brewingStand2;
	public static Item				brewingStand2Item;
	
	public static ItemPotion2		potion2;
	public static ItemGlassBottle2	glassBottle2;
	
	static
	{
		expandPotionList(64);
	}
	
	// API Stuff
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CSConfig.loadConfig(event.getSuggestedConfigurationFile(), NAME);
		
		brewingStand2ID = CSConfig.getTileEntity("Brewing Stand", 11);
		
		multiPotions = CSConfig.getBool("potions", "MultiPotions", multiPotions);
		advancedPotionInfo = CSConfig.getBool("potions", "Advanced Potion Info", advancedPotionInfo);
		showAllBaseTypes = CSConfig.getBool("potions", "Show All Base Potion Types", showAllBaseTypes);
		defaultAwkwardBrewing = CSConfig.getBool("potions", "Default Awkward Brewing", defaultAwkwardBrewing);
		potionStackSize = CSConfig.getInt("potions", "PotionStackSize", potionStackSize);
		
		CSConfig.saveConfig();
		
		brewingStand2 = (new BlockBrewingStand2()).setBlockName("brewing_stand").setHardness(0.5F).setLightLevel(0.125F);
		brewingStand2Item = (new ItemBrewingStand2()).setUnlocalizedName("brewing_stand").setTextureName("brewing_stand");
		potion2 = (ItemPotion2) (new ItemPotion2()).setUnlocalizedName("potion");
		glassBottle2 = (ItemGlassBottle2) (new ItemGlassBottle2()).setUnlocalizedName("glassBottle").setTextureName("potion_bottle_empty");
		
		CSBlocks.replaceBlock(Blocks.brewing_stand, brewingStand2);
		CSItems.replaceItem(Items.brewing_stand, brewingStand2Item);
		CSItems.replaceItem(Items.potionitem, potion2);
		CSItems.replaceItem(Items.glass_bottle, glassBottle2);
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		netHandler.init();
		
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
		EntityRegistry.registerGlobalEntityID(EntityPotion2.class, "SplashPotion2", splashPotion2ID);
		EntityRegistry.registerModEntity(EntityPotion2.class, "SplashPotion2", splashPotion2ID, this, 100, 20, true);
		
		BlockDispenser.dispenseBehaviorRegistry.putObject(potion2, new PotionDispenser());
		proxy.registerRenderInformation();
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CSUpdate.updateCheckCS(NAME, ACRONYM, VERSION);
		
		netHandler.postInit();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		ServerCommandManager command = (ServerCommandManager) event.getServer().getCommandManager();
		command.registerCommand(new CommandGivePotion());
	}
	
	public static boolean isMorePotionsModInstalled()
	{
		if (MORE_POTIONS_MOD)
		{
			return true;
		}
		
		try
		{
			Class.forName("clashsoft.mods.morepotions.MorePotionsMod", false, ClassLoader.getSystemClassLoader());
			return MORE_POTIONS_MOD = true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
	
	public static boolean isClashsoftLibInstalled()
	{
		if (CLASHSOFT_LIB)
		{
			return true;
		}
		
		try
		{
			Class.forName("clashsoft.cslib.util.CSUtil");
			return CLASHSOFT_LIB = true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
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
						handler.onPotionUpdate(living.getAge(), living, effect);
					}
				}
			}
		}
	}
	
	public static void expandPotionList(int size)
	{
		if (Potion.potionTypes.length < size)
		{
			try
			{
				Field f = CSReflection.getField(Potion.class, 0);
				CSReflection.setModifier(f, Modifier.FINAL, false);
				Potion[] potionTypes = new Potion[size];
				System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, Potion.potionTypes.length);
				f.set(null, potionTypes);
			}
			catch (Exception e)
			{
				CSLog.error(e);
			}
		}
	}
}
