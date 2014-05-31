package clashsoft.brewingapi.potion;

import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.item.CSStacks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionList implements IPotionList
{
	public static boolean		DEFAULT_AWKWARD_BREWING	= false;
	public static boolean		SHOW_ALL_BASES			= false;
	
	public static IPotionList	instance				= new PotionList();
	
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
	
	public static IPotionType	moveSlowdown;
	public static IPotionType	moveSpeed;
	public static IPotionType	digSlowdown;
	public static IPotionType	digSpeed;
	public static IPotionType	weakness;
	public static IPotionType	damageBoost;
	public static IPotionType	harm;
	public static IPotionType	heal;
	/** Health Boost added in 1.6 **/
	public static IPotionType	healthBoost;
	/** Absorption added in 1.6 **/
	public static IPotionType	absorption;
	public static IPotionType	jump;
	public static IPotionType	confusion;
	public static IPotionType	regeneration;
	public static IPotionType	resistance;
	public static IPotionType	fireResistance;
	public static IPotionType	waterBreathing;
	public static IPotionType	coldness;
	public static IPotionType	invisibility;
	public static IPotionType	blindness;
	public static IPotionType	nightVision;
	public static IPotionType	poison;
	public static IPotionType	hunger;
	/** Hunger Bar restore added in 1.6 **/
	public static IPotionType	saturation;
	public static IPotionType	wither;
	
	private PotionList()
	{
	}
	
	@Override
	public void initPotionTypes()
	{
		awkward = new PotionBase("awkward", new ItemStack(Items.nether_wart));
		
		moveSlowdown = new PotionType(new PotionEffect(Potion.moveSlowdown.id, 20 * 90, 0), 4, 20 * 240);
		moveSpeed = new PotionType(new PotionEffect(Potion.moveSpeed.id, 20 * 180, 0), 7, 20 * 360, moveSlowdown, CSStacks.sugar, dashing);
		weakness = new PotionType(new PotionEffect(Potion.weakness.id, 20 * 90, 0), 2, 20 * 240, CSStacks.fermented_spider_eye, awkward);
		damageBoost = new PotionType(new PotionEffect(Potion.damageBoost.id, 20 * 180, 0), 4, 20 * 300, weakness, CSStacks.blaze_powder, awkward);
		harm = new PotionType(new PotionEffect(Potion.harm.id, 1, 0), 1, 0);
		heal = new PotionType(new PotionEffect(Potion.heal.id, 1, 0), 1, 0, harm, CSStacks.speckled_melon, thick);
		regeneration = new PotionType(new PotionEffect(Potion.regeneration.id, 20 * 45, 0), 2, 20 * 180, moveSlowdown, CSStacks.ghast_tear, awkward);
		fireResistance = new PotionType(new PotionEffect(Potion.fireResistance.id, 20 * 180, 0), 0, 20 * 360, moveSlowdown, CSStacks.magma_cream, awkward);
		invisibility = new PotionType(new PotionEffect(Potion.invisibility.id, 20 * 180, 0), 0, 720 * 20);
		nightVision = new PotionType(new PotionEffect(Potion.nightVision.id, 20 * 180, 0), 0, 20 * 300, invisibility, CSStacks.golden_carrot, thin);
		poison = new PotionType(new PotionEffect(Potion.poison.id, 20 * 45, 0), 2, 20 * 60, CSStacks.spider_eye, acrid);
	}
	
	@Override
	public void loadPotionTypes()
	{
		awkward.register();
		
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
	
	public static void init()
	{
		instance.initPotionTypes();
		instance.loadPotionTypes();
	}
}
