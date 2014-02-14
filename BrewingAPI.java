package clashsoft.brewingapi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import clashsoft.brewingapi.api.IIngredientHandler;
import clashsoft.brewingapi.api.IPotionEffectHandler;
import clashsoft.brewingapi.block.BlockBrewingStand2;
import clashsoft.brewingapi.brewing.PotionList;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.command.CommandGivePotion;
import clashsoft.brewingapi.common.BAPICommonProxy;
import clashsoft.brewingapi.common.SplashEffectData;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.item.ItemBrewingStand2;
import clashsoft.brewingapi.item.ItemGlassBottle2;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.lib.DispenserBehaviorPotion2;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import clashsoft.cslib.minecraft.block.CSBlocks;
import clashsoft.cslib.minecraft.item.CSItems;
import clashsoft.cslib.minecraft.network.CSNetHandler;
import clashsoft.cslib.minecraft.update.CSUpdate;
import clashsoft.cslib.minecraft.util.CSConfig;
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
	
	public static CSNetHandler	netHandler = new CSNetHandler("BAPI");
	
	// API Stuff
	
	public static final int			POTION_LIST_LENGTH		= 1024;
	
	public static boolean			MORE_POTIONS_MOD		= false;
	public static boolean			CLASHSOFT_LIB			= false;
	
	public static boolean			multiPotions			= false;
	public static boolean			advancedPotionInfo		= false;
	public static boolean			animatedPotionLiquid	= true;
	public static boolean			showAllBaseBrewings		= false;
	public static boolean			defaultAwkwardBrewing	= false;
	public static int				potionStackSize			= 1;
	
	public static int				brewingStand2ID			= 11;
	public static int				splashPotion2ID			= EntityRegistry.findGlobalUniqueEntityId();
	
	public static CreativeTabs		potions;
	
	public static Block				brewingStand2;
	public static Item				brewingStand2Item;
	
	public static ItemPotion2		potion2;
	public static ItemGlassBottle2	glassBottle2;
	
	// API Stuff
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CSUpdate.updateCheckCS(NAME, ACRONYM, VERSION);
		
		CSConfig.loadConfig(event.getSuggestedConfigurationFile(), NAME);
		
		brewingStand2ID = CSConfig.getInt("TileEntityIDs", "BrewingStand2TEID", 11);
		
		multiPotions = CSConfig.getBool("potions", "MultiPotions", "If true, potions with 2 different effects are shown in the creative inventory.", false);
		advancedPotionInfo = CSConfig.getBool("potions", "AdvancedPotionInfo", true);
		animatedPotionLiquid = CSConfig.getBool("potions", "AnimatedPotionLiquid", true);
		showAllBaseBrewings = CSConfig.getBool("potions", "ShowAllBaseBrewings", "If true, all base potions are shown in creative inventory.", false);
		defaultAwkwardBrewing = CSConfig.getBool("potions", "DefaultAwkwardBrewing", "If true, all potions can be brewed with an awkward potion.", false);
		potionStackSize = CSConfig.getInt("potions", "PotionStackSize", 1);
		
		CSConfig.saveConfig();
		
		brewingStand2 = (new BlockBrewingStand2()).setBlockName("brewing_stand").setHardness(0.5F).setLightLevel(0.125F);
		CSBlocks.overrideBlock(brewingStand2, "brewing_stand");
		
		brewingStand2Item = (new ItemBrewingStand2()).setUnlocalizedName("brewing_stand").setTextureName("brewing_stand");
		CSItems.overrideItem(brewingStand2Item, "brewing_stand");
		
		potion2 = (ItemPotion2) (new ItemPotion2()).setUnlocalizedName("potion");
		CSItems.overrideItem(potion2, "potion");
		
		glassBottle2 = (ItemGlassBottle2) (new ItemGlassBottle2()).setUnlocalizedName("glass_bottle");
		CSItems.overrideItem(glassBottle2, "glass_bottle");
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		netHandler.init();
		netHandler.registerPacket(SplashEffectData.class);
		
		load();
		
		if (multiPotions)
		{
			potions = new CreativeTabs("morepotions")
			{
				ItemStack	stack	= null;
				
				@Override
				public ItemStack getIconItemStack()
				{
					return this.stack == null ? this.stack = PotionList.damageBoost.addPotionTypeToItemStack(new ItemStack(BrewingAPI.potion2, 0, 1)) : this.stack;
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
		
		BlockDispenser.dispenseBehaviorRegistry.putObject(potion2, new DispenserBehaviorPotion2());
		proxy.registerRenderInformation();
		proxy.registerRenderers();
		
		// CSLang.addLocalizationUS("commands.givepotion.usage",
		// "/givepotion <player> <effect> [seconds] [amplifier] [splash]");
		// CSLang.addLocalizationUS("commands.givepotion.success",
		// "Given Potion (%1\u0024s (ID %2\u0024d) level %3\u0024d for %5\u0024d seconds) to %4\u0024s.");
		// CSLang.addLocalizationUS("commands.givepotion.success.splash",
		// "Given Splash Potion (%1\u0024s (ID %2\u0024d) level %3\u0024d for %5\u0024d seconds) to %4\u0024s.");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		netHandler.postInit();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		ServerCommandManager command = (ServerCommandManager) event.getServer().getCommandManager();
		command.registerCommand(new CommandGivePotion());
	}
	
	public static void load()
	{
		if (!hasLoaded)
		{
			expandPotionList();
			PotionList.initializeBrewings();
			PotionList.registerBrewings();
			hasLoaded = true;
		}
	}
	
	// API Stuff
	
	public static boolean isMorePotionsModInstalled()
	{
		if (MORE_POTIONS_MOD)
			return true;
		
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
			return true;
		
		try
		{
			Class.forName("clashsoft.cslib.util.CSUtil", false, ClassLoader.getSystemClassLoader());
			return CLASHSOFT_LIB = true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
	
	public static boolean						hasLoaded			= false;
	
	public static List<IPotionEffectHandler>	effectHandlers		= new LinkedList<IPotionEffectHandler>();
	public static List<IIngredientHandler>		ingredientHandlers	= new LinkedList<IIngredientHandler>();
	
	public static PotionType addBrewing(PotionType potionType)
	{
		return potionType.register();
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
	
	private int	tick	= 0;
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event)
	{
		if (event.entityLiving != null && !event.entityLiving.worldObj.isRemote)
		{
			Collection<PotionEffect> c = event.entityLiving.getActivePotionEffects();
			List<PotionEffect> potionEffects = new ArrayList(c);
			
			for (int i = 0; i < potionEffects.size(); i++)
			{
				PotionEffect effect = potionEffects.get(i);
				for (IPotionEffectHandler handler : effectHandlers)
				{
					if (handler.canHandle(effect))
						handler.onPotionUpdate(this.tick, event.entityLiving, effect);
				}
			}
			
			this.tick++;
			
			if (this.tick > 1023)
			{
				this.tick = 0;
			}
		}
	}
	
	public static void expandPotionList()
	{
		Potion[] potionTypes = null;
		
		if (Potion.potionTypes.length != POTION_LIST_LENGTH)
		{
			for (Field f : Potion.class.getDeclaredFields())
			{
				f.setAccessible(true);
				try
				{
					if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a"))
					{
						Field modfield = Field.class.getDeclaredField("modifiers");
						modfield.setAccessible(true);
						modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
						
						potionTypes = (Potion[]) f.get(null);
						final Potion[] newPotionTypes = new Potion[POTION_LIST_LENGTH];
						for (int i = 0; i < newPotionTypes.length; i++)
						{
							if (i < Potion.potionTypes.length)
								newPotionTypes[i] = Potion.potionTypes[i];
							else
								newPotionTypes[i] = null;
						}
						f.set(null, newPotionTypes);
					}
				}
				catch (Exception e)
				{
					CSLog.error(e);
				}
			}
		}
	}
}
