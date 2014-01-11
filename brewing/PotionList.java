package clashsoft.brewingapi.brewing;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.cslib.util.CSLog;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionList
{
	public static boolean		DEFAULT_AWKWARD_BREWING	= false;
	public static boolean		SHOW_ALL_BASES	= false;
	
	/** Base needed for all potions **/
	public static PotionBase	awkward;
	public static PotionBase	mundane;
	public static PotionBase	uninteresting;
	public static PotionBase	bland;
	public static PotionBase	clear;
	public static PotionBase	milky;
	public static PotionBase	diffuse;
	public static PotionBase	artless;
	/** Needed for some higher tier extendable potions **/
	public static PotionBase	thin;
	public static PotionBase	flat;
	public static PotionBase	bulky;
	public static PotionBase	bungling;
	public static PotionBase	buttered;
	public static PotionBase	smooth;
	public static PotionBase	suave;
	public static PotionBase	debonair;
	/** Needed for some higher tier improvable potions **/
	public static PotionBase	thick;
	public static PotionBase	elegant;
	public static PotionBase	fancy;
	public static PotionBase	charming;
	/** Needed for potions that change movement **/
	public static PotionBase	dashing;
	public static PotionBase	refined;
	public static PotionBase	cordial;
	public static PotionBase	sparkling;
	public static PotionBase	potent;
	public static PotionBase	foul;
	public static PotionBase	odorless;
	public static PotionBase	rank;
	public static PotionBase	harsh;
	/** Needed for potions that poison the player **/
	public static PotionBase	acrid;
	public static PotionBase	gross;
	public static PotionBase	stinky;
	
	public static PotionType	moveSlowdown;
	public static PotionType	moveSpeed;
	public static PotionType	digSlowdown;
	public static PotionType	digSpeed;
	public static PotionType	weakness;
	public static PotionType	damageBoost;
	public static PotionType	harm;
	public static PotionType	heal;
	/** Health Boost added in 1.6 **/
	public static PotionType	healthBoost;
	/** Absorption added in 1.6 **/
	public static PotionType	absorption;
	public static PotionType	jump;
	public static PotionType	confusion;
	public static PotionType	regeneration;
	public static PotionType	resistance;
	public static PotionType	fireResistance;
	public static PotionType	waterBreathing;
	public static PotionType	coldness;
	public static PotionType	invisibility;
	public static PotionType	blindness;
	public static PotionType	nightVision;
	public static PotionType	poison;
	public static PotionType	hunger;
	/** Hunger Bar restore added in 1.6 **/
	public static PotionType	saturation;
	public static PotionType	wither;
	
	public static void initializeBrewings()
	{
		if (!BrewingAPI.isMorePotionsModInstalled())
		{
			CSLog.info("Initializing BrewingAPI Potion Types");
			initializeBaseBrewings_BrewingAPI();
			initializeBrewings_BrewingAPI();
		}
		else
		{
			CSLog.info("Skipping initialization of BrewingAPI Potion Types ... More Potions Mod will do that.");
		}
	}
	
	public static void registerBrewings()
	{
		if (!BrewingAPI.isMorePotionsModInstalled())
		{
			CSLog.info("Registering BrewingAPI Brewings");
			registerBaseBrewings_BrewingAPI();
			registerBrewings_BrewingAPI();
		}
		else
		{
			CSLog.info("Skipping registration of BrewingAPI Brewings ... More Potions Mod will do that.");
		}
	}
	
	public static void initializeBaseBrewings_BrewingAPI()
	{
		SHOW_ALL_BASES = false;
		DEFAULT_AWKWARD_BREWING = true;
		awkward = new PotionBase("awkward", new ItemStack(Item.netherStalkSeeds));
	}
	
	public static void initializeBrewings_BrewingAPI()
	{
		moveSlowdown = new PotionType(new PotionEffect(Potion.moveSlowdown.id, 20 * 90, 0), 4, 20 * 240);
		moveSpeed = new PotionType(new PotionEffect(Potion.moveSpeed.id, 20 * 180, 0), 7, 20 * 360, moveSlowdown, new ItemStack(Item.sugar), PotionType.getBaseBrewing(dashing));
		weakness = new PotionType(new PotionEffect(Potion.weakness.id, 20 * 90, 0), 2, 20 * 240, new ItemStack(Item.fermentedSpiderEye), awkward);
		damageBoost = new PotionType(new PotionEffect(Potion.damageBoost.id, 20 * 180, 0), 4, 20 * 300, weakness, new ItemStack(Item.blazePowder), awkward);
		harm = new PotionType(new PotionEffect(Potion.harm.id, 1, 0), 1, 0);
		heal = new PotionType(new PotionEffect(Potion.heal.id, 1, 0), 1, 0, harm, new ItemStack(Item.speckledMelon), PotionType.getBaseBrewing(thick));
		regeneration = new PotionType(new PotionEffect(Potion.regeneration.id, 20 * 45, 0), 2, 20 * 180, moveSlowdown, new ItemStack(Item.ghastTear), awkward);
		fireResistance = new PotionType(new PotionEffect(Potion.fireResistance.id, 20 * 180, 0), 0, 20 * 360, moveSlowdown, new ItemStack(Item.magmaCream), awkward);
		invisibility = new PotionType(new PotionEffect(Potion.invisibility.id, 20 * 180, 0), 0, 720 * 20);
		nightVision = new PotionType(new PotionEffect(Potion.nightVision.id, 20 * 180, 0), 0, 20 * 300, invisibility, new ItemStack(Item.goldenCarrot), PotionType.getBaseBrewing(thin));
		poison = new PotionType(new PotionEffect(Potion.poison.id, 20 * 45, 0), 2, 20 * 60, new ItemStack(Item.spiderEye), PotionType.getBaseBrewing(acrid));
	}
	
	public static void registerBrewings_BrewingAPI()
	{
		regeneration.register();
		moveSpeed.register();
		fireResistance.register();
		poison.register();
		heal.register();
		nightVision.register();
		weakness.register();
		damageBoost.register();
		moveSlowdown.register();
		harm.register();
		invisibility.register();
	}
	
	private static void registerBaseBrewings_BrewingAPI()
	{
		awkward.register();
	}
}
