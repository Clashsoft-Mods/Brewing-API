package clashsoft.brewingapi.brewing;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class BrewingList
{
	public static boolean		DEFAULT_AWKWARD_BREWING	= false;
	public static boolean		SHOW_ALL_BASEBREWINGS	= false;
	
	/** Base needed for all potions **/
	public static BrewingBase	awkward;
	public static BrewingBase	mundane;
	public static BrewingBase	uninteresting;
	public static BrewingBase	bland;
	public static BrewingBase	clear;
	public static BrewingBase	milky;
	public static BrewingBase	diffuse;
	public static BrewingBase	artless;
	/** Needed for some higher tier extendable potions **/
	public static BrewingBase	thin;
	public static BrewingBase	flat;
	public static BrewingBase	bulky;
	public static BrewingBase	bungling;
	public static BrewingBase	buttered;
	public static BrewingBase	smooth;
	public static BrewingBase	suave;
	public static BrewingBase	debonair;
	/** Needed for some higher tier improvable potions **/
	public static BrewingBase	thick;
	public static BrewingBase	elegant;
	public static BrewingBase	fancy;
	public static BrewingBase	charming;
	/** Needed for potions that change movement **/
	public static BrewingBase	dashing;
	public static BrewingBase	refined;
	public static BrewingBase	cordial;
	public static BrewingBase	sparkling;
	public static BrewingBase	potent;
	public static BrewingBase	foul;
	public static BrewingBase	odorless;
	public static BrewingBase	rank;
	public static BrewingBase	harsh;
	/** Needed for potions that poison the player **/
	public static BrewingBase	acrid;
	public static BrewingBase	gross;
	public static BrewingBase	stinky;
	
	public static Brewing		moveSlowdown;
	public static Brewing		moveSpeed;
	public static Brewing		digSlowdown;
	public static Brewing		digSpeed;
	public static Brewing		weakness;
	public static Brewing		damageBoost;
	public static Brewing		harm;
	public static Brewing		heal;
	public static Brewing		doubleLife;
	/** Health Boost added in 1.6 **/
	public static Brewing		healthBoost;
	/** Absorption added in 1.6 **/
	public static Brewing		absorption;
	public static Brewing		jump;
	public static Brewing		confusion;
	public static Brewing		regeneration;
	public static Brewing		resistance;
	public static Brewing		ironSkin;
	public static Brewing		obsidianSkin;
	public static Brewing		fireResistance;
	public static Brewing		waterWalking;
	public static Brewing		waterBreathing;
	public static Brewing		coldness;
	public static Brewing		invisibility;
	public static Brewing		blindness;
	public static Brewing		nightVision;
	public static Brewing		poison;
	public static Brewing		hunger;
	/** Hunger Bar restore added in 1.6 **/
	public static Brewing		saturation;
	public static Brewing		wither;
	public static Brewing		explosiveness;
	public static Brewing		fire;
	public static Brewing		random;
	public static Brewing		effectRemove;
	
	public static void initializeBrewings()
	{
		if (!BrewingAPI.MORE_POTIONS_MOD())
		{
			System.out.println("Initializing BrewingAPI Brewings");
			initializeBaseBrewings_BrewingAPI();
			initializeBrewings_BrewingAPI();
		}
		else
		{
			System.out.println("Skipping initialization of BrewingAPI Brewings ... More Potions Mod will do that.");
		}
	}
	
	public static void registerBrewings()
	{
		if (!BrewingAPI.MORE_POTIONS_MOD())
		{
			System.out.println("Registering BrewingAPI Brewings");
			registerBaseBrewings_BrewingAPI();
			registerBrewings_BrewingAPI();
		}
		else
		{
			System.out.println("Skipping registration of BrewingAPI Brewings ... More Potions Mod will do that.");
		}
	}
	
	public static void initializeBaseBrewings_BrewingAPI()
	{
		SHOW_ALL_BASEBREWINGS = false;
		DEFAULT_AWKWARD_BREWING = true;
		awkward = new BrewingBase("awkward", new ItemStack(Item.netherStalkSeeds));
	}
	
	public static void initializeBrewings_BrewingAPI()
	{
		moveSlowdown = new Brewing(new PotionEffect(Potion.moveSlowdown.id, 20 * 90, 0), 4, 20 * 240, Brewing.getBaseBrewing(dashing));
		moveSpeed = new Brewing(new PotionEffect(Potion.moveSpeed.id, 20 * 180, 0), 7, 20 * 360, moveSlowdown, new ItemStack(Item.sugar), Brewing.getBaseBrewing(dashing));
		weakness = new Brewing(new PotionEffect(Potion.weakness.id, 20 * 90, 0), 2, 20 * 240, new ItemStack(Item.fermentedSpiderEye), awkward);
		damageBoost = new Brewing(new PotionEffect(Potion.damageBoost.id, 20 * 180, 0), 4, 20 * 300, weakness, new ItemStack(Item.blazePowder), awkward);
		harm = new Brewing(new PotionEffect(Potion.harm.id, 1, 0), 1, 0, Brewing.getBaseBrewing(thick));
		heal = new Brewing(new PotionEffect(Potion.heal.id, 1, 0), 1, 0, harm, new ItemStack(Item.speckledMelon), Brewing.getBaseBrewing(thick));
		regeneration = new Brewing(new PotionEffect(Potion.regeneration.id, 20 * 45, 0), 2, 20 * 180, moveSlowdown, new ItemStack(Item.ghastTear), awkward);
		fireResistance = new Brewing(new PotionEffect(Potion.fireResistance.id, 20 * 180, 0), 0, 20 * 360, moveSlowdown, new ItemStack(Item.magmaCream), awkward);
		invisibility = new Brewing(new PotionEffect(Potion.invisibility.id, 20 * 180, 0), 0, 720 * 20);
		nightVision = new Brewing(new PotionEffect(Potion.nightVision.id, 20 * 180, 0), 0, 20 * 300, invisibility, new ItemStack(Item.goldenCarrot), Brewing.getBaseBrewing(thin));
		poison = new Brewing(new PotionEffect(Potion.poison.id, 20 * 45, 0), 2, 20 * 60, new ItemStack(Item.spiderEye), Brewing.getBaseBrewing(acrid));
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
