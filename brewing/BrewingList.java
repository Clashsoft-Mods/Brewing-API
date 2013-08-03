package clashsoft.brewingapi.brewing;

import clashsoft.brewingapi.BrewingAPI;
import net.minecraft.block.Block;
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
	public static Brewing		doubleJump;
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
	public static Brewing		saturation;
	public static Brewing		wither;
	public static Brewing		explosiveness;
	public static Brewing		fire;
	public static Brewing		random;
	public static Brewing		effectRemove;

	public static void initializeBrewings()
	{
		if (BrewingAPI.MORE_POTIONS_MOD())
		{
			System.out.println("Initializing MorePotionsMod Brewings");
			initializeBaseBrewings_MorePotionsMod();
			initializeBrewings_MorePotionsMod();
		}
		else
		{
			System.out.println("Initializing BrewingAPI Brewings");
			initializeBaseBrewings_BrewingAPI();
			initializeBrewings_BrewingAPI();
		}
	}

	public static void registerBrewings()
	{
		if (BrewingAPI.MORE_POTIONS_MOD())
		{
			System.out.println("Registering MorePotionsMod Brewings");
			registerBaseBrewings_MorePotionsMod();
			registerBrewings_MorePotionsMod();
		}
		else
		{
			System.out.println("Registering BrewingAPI Brewings");
			registerBaseBrewings_BrewingAPI();
			registerBrewings_BrewingAPI();
		}
	}

	private static void initializeBaseBrewings_MorePotionsMod()
	{
		awkward = new BrewingBase("awkward", new ItemStack(Item.netherStalkSeeds));
		mundane = new BrewingBase("mundane", new ItemStack(Block.mushroomBrown));
		uninteresting = new BrewingBase("uninteresting", new ItemStack(Item.paper));
		bland = new BrewingBase("bland", new ItemStack(Item.melon));
		clear = new BrewingBase("clear", clashsoft.mods.morepotions.MorePotionsMod.dustClay);
		milky = new BrewingBase("milky", new ItemStack(Block.sapling));
		diffuse = new BrewingBase("diffuse", new ItemStack(Item.wheat));
		artless = new BrewingBase("artless", new ItemStack(Item.reed));
		thin = new BrewingBase("thin", new ItemStack(Item.redstone));
		flat = new BrewingBase("flat", new ItemStack(Block.waterlily));
		bulky = new BrewingBase("bulky", new ItemStack(Block.sand));
		bungling = new BrewingBase("bungling", new ItemStack(Block.mushroomRed));
		buttered = new BrewingBase("buttered", new ItemStack(Item.dyePowder, 1, 11));
		smooth = new BrewingBase("smooth", new ItemStack(Item.seeds));
		suave = new BrewingBase("suave", new ItemStack(Item.dyePowder, 1, 3));
		debonair = new BrewingBase("debonair", new ItemStack(Item.dyePowder, 1, 2));
		thick = new BrewingBase("thick", new ItemStack(Item.glowstone));
		elegant = new BrewingBase("elegant", new ItemStack(Item.enderPearl));
		fancy = new BrewingBase("fancy", new ItemStack(Item.flint));
		charming = new BrewingBase("charming", new ItemStack(Block.plantRed));
		dashing = new BrewingBase("dashing", new ItemStack(Item.silk));
		refined = new BrewingBase("refined", new ItemStack(Item.slimeBall));
		cordial = new BrewingBase("cordial", new ItemStack(Item.dyePowder, 1, 15));
		sparkling = new BrewingBase("sparkling", new ItemStack(Item.goldNugget));
		potent = new BrewingBase("potent", new ItemStack(Item.blazePowder));
		foul = new BrewingBase("foul", new ItemStack(Item.rottenFlesh));
		odorless = new BrewingBase("odorless", new ItemStack(Item.bread));
		rank = new BrewingBase("rank", new ItemStack(Item.egg));
		harsh = new BrewingBase("harsh", new ItemStack(Block.slowSand));
		acrid = new BrewingBase("acrid", new ItemStack(Item.fermentedSpiderEye));
		gross = new BrewingBase("gross", new ItemStack(Item.pumpkinSeeds));
		stinky = new BrewingBase("stinky", new ItemStack(Item.fishRaw));

	}

	public static void initializeBrewings_MorePotionsMod()
	{
		moveSlowdown = new Brewing(new PotionEffect(Potion.moveSlowdown.id, 20 * 90, 0), 4, 20 * 240, Brewing.getBaseBrewing(dashing));
		moveSpeed = new Brewing(new PotionEffect(Potion.moveSpeed.id, 20 * 180, 0), 7, 20 * 360, moveSlowdown, new ItemStack(Item.sugar), Brewing.getBaseBrewing(dashing));
		digSlowdown = new Brewing(new PotionEffect(Potion.digSlowdown.id, 20 * 90, 0), 4, 20 * 240, Brewing.getBaseBrewing(dashing));
		digSpeed = new Brewing(new PotionEffect(Potion.digSpeed.id, 20 * 180, 0), 7, 20 * 360, digSlowdown, clashsoft.mods.morepotions.MorePotionsMod.dustGold, Brewing.getBaseBrewing(dashing));
		weakness = new Brewing(new PotionEffect(Potion.weakness.id, 20 * 90, 0), 2, 20 * 240, new ItemStack(Item.fermentedSpiderEye), awkward);
		damageBoost = new Brewing(new PotionEffect(Potion.damageBoost.id, 20 * 180, 0), 4, 20 * 300, weakness, new ItemStack(Item.blazePowder), awkward);
		harm = new Brewing(new PotionEffect(Potion.harm.id, 1, 0), 1, 0, Brewing.getBaseBrewing(thick));
		heal = new Brewing(new PotionEffect(Potion.heal.id, 1, 0), 1, 0, harm, new ItemStack(Item.speckledMelon), Brewing.getBaseBrewing(thick));
		doubleLife = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.doubleLife.id, 1625000, 0), 0, 0, harm, clashsoft.mods.morepotions.MorePotionsMod.dustNetherstar, Brewing.getBaseBrewing(thick));
		healthBoost = new Brewing(new PotionEffect(Potion.field_76434_w.id, 45 * 20, 0), 4, 120 * 20, Brewing.getBaseBrewing(thick));
		absorption = new Brewing(new PotionEffect(Potion.field_76444_x.id, 45 * 20, 0), 4, 120 * 20, healthBoost, new ItemStack(Item.appleGold), Brewing.getBaseBrewing(thick));
		jump = new Brewing(new PotionEffect(Potion.jump.id, 20 * 180, 0), 4, 20 * 300, Brewing.getBaseBrewing(dashing));
		doubleJump = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.doubleJump.id, 20 * 180, 0), 4, 20 * 3000, jump, new ItemStack(Item.feather), Brewing.getBaseBrewing(dashing));
		confusion = new Brewing(new PotionEffect(Potion.confusion.id, 20 * 90, 0), 2, 20 * 180, new ItemStack(Item.poisonousPotato), awkward);
		regeneration = new Brewing(new PotionEffect(Potion.regeneration.id, 20 * 45, 0), 2, 20 * 180, moveSlowdown, new ItemStack(Item.ghastTear), awkward);
		resistance = new Brewing(new PotionEffect(Potion.resistance.id, 20 * 180, 0), 3, 20 * 240, clashsoft.mods.morepotions.MorePotionsMod.dustDiamond, Brewing.getBaseBrewing(thick));
		ironSkin = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.ironSkin.id, 20 * 120, 0), 1, 20 * 240, clashsoft.mods.morepotions.MorePotionsMod.dustIron, Brewing.getBaseBrewing(thick));
		obsidianSkin = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.obsidianSkin.id, 20 * 120, 0), 1, 20 * 240, clashsoft.mods.morepotions.MorePotionsMod.dustObsidian, Brewing.getBaseBrewing(thick));
		fireResistance = new Brewing(new PotionEffect(Potion.fireResistance.id, 20 * 180, 0), 0, 20 * 360, moveSlowdown, new ItemStack(Item.magmaCream), awkward);
		waterWalking = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.waterWalking.id, 20 * 120, 0), 0, 240 * 20, awkward);
		waterBreathing = new Brewing(new PotionEffect(Potion.waterBreathing.id, 20 * 180, 0), 2, 20 * 360, waterWalking, clashsoft.mods.morepotions.MorePotionsMod.dustClay, awkward);
		coldness = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.coldness.id, 20 * 180, 0), 1, 20 * 360, new ItemStack(Item.snowball), awkward);
		invisibility = new Brewing(new PotionEffect(Potion.invisibility.id, 20 * 180, 0), 0, 720 * 20);
		blindness = new Brewing(new PotionEffect(Potion.blindness.id, 20 * 90, 0), 0, 20 * 240, new ItemStack(Item.dyePowder, 1, 0), Brewing.getBaseBrewing(thin));
		nightVision = new Brewing(new PotionEffect(Potion.nightVision.id, 20 * 180, 0), 0, 20 * 300, invisibility, new ItemStack(Item.goldenCarrot), Brewing.getBaseBrewing(thin));
		poison = new Brewing(new PotionEffect(Potion.poison.id, 20 * 45, 0), 2, 20 * 60, new ItemStack(Item.spiderEye), Brewing.getBaseBrewing(acrid));
		hunger = new Brewing(new PotionEffect(Potion.hunger.id, 20 * 45, 0), 3, 20 * 60, Brewing.getBaseBrewing(acrid));
		saturation = new Brewing(new PotionEffect(Potion.field_76443_y.id, 20 * 45, 0), 3, 20 * 60, hunger, new ItemStack(Item.bread), Brewing.getBaseBrewing(awkward));
		wither = new Brewing(new PotionEffect(Potion.wither.id, 450, 0), 1, 20 * 60, clashsoft.mods.morepotions.MorePotionsMod.dustWither, Brewing.getBaseBrewing(acrid));
		explosiveness = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.explosiveness.id, 20 * 10, 0), 4, 20 * 20, awkward);
		fire = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.fire.id, 20 * 10, 0), 0, 20 * 20, explosiveness, new ItemStack(Item.fireballCharge), awkward);
		random = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.random.id, clashsoft.mods.morepotions.MorePotionsMod.randomMode == 0 ? 1 : 20 * 45, 0), 0, clashsoft.mods.morepotions.MorePotionsMod.randomMode == 0 ? 1 : 20 * 90, awkward);
		effectRemove = new Brewing(new PotionEffect(clashsoft.mods.morepotions.MorePotionsMod.effectRemove.id, 20 * 45, 0), 0, 20 * 90, random, new ItemStack(Item.bucketMilk), awkward);
	}

	public static void initializeBaseBrewings_BrewingAPI()
	{
		SHOW_ALL_BASEBREWINGS = false;
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

	public static void registerBrewings_MorePotionsMod()
	{
		regeneration.register();
		moveSpeed.register();
		moveSlowdown.register();
		digSpeed.register();
		digSlowdown.register();
		fireResistance.register();
		waterBreathing.register();
		waterWalking.register(); //
		coldness.register(); //
		doubleLife.register(); //
		heal.register();
		harm.register();
		healthBoost.register();
		absorption.register();
		poison.register();
		fire.register();
		explosiveness.register();
		wither.register();
		saturation.register();
		hunger.register();
		confusion.register();
		nightVision.register();
		invisibility.register();
		blindness.register();
		damageBoost.register();
		weakness.register();
		jump.register();
		doubleJump.register(); //
		resistance.register();
		ironSkin.register(); //
		obsidianSkin.register(); //
		effectRemove.register(); //
		random.register();
	}

	public static void registerBaseBrewings_MorePotionsMod()
	{
		awkward.register();
		thick.register();
		thin.register();
		dashing.register();
		acrid.register();

		if (SHOW_ALL_BASEBREWINGS)
		{
			mundane.register();
			elegant.register();
			uninteresting.register();
			bland.register();
			clear.register();
			milky.register();
			diffuse.register();
			artless.register();
			flat.register();
			bulky.register();
			bungling.register();
			buttered.register();
			smooth.register();
			suave.register();
			debonair.register();
			fancy.register();
			charming.register();
			dashing.register();
			refined.register();
			cordial.register();
			sparkling.register();
			potent.register();
			foul.register();
			odorless.register();
			rank.register();
			harsh.register();
			gross.register();
			stinky.register();
		}
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
