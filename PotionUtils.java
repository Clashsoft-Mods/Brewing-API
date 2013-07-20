package clashsoft.brewingapi;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import clashsoft.brewingapi.brewing.Brewing;
import clashsoft.brewingapi.brewing.BrewingBase;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.clashsoftapi.util.CSUtil;

public class PotionUtils
{
	public static int getGoodEffects(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int badEffects = 0;
		
		for (Brewing b : var5)
		{
			if (b.isBadEffect())
			{
				badEffects++;
			}
		}
		
		if (var5.size() - badEffects > 1)
		{
			int goodEffects = var5.size() - badEffects;
			return goodEffects;
		}
		return 0;
	}
	
	public static int getBadEffects(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int badEffects = 0;
		
		for (Brewing b : var5)
		{
			if (b.isBadEffect())
			{
				badEffects++;
			}
		}
		
		if (badEffects > 1)
		{
			return badEffects;
		}
		return 0;
	}
	
	public static int getAverageAmplifier(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int averageAmplifier = 0;
		for (int i = 0; i < var5.size(); i++)
		{
			if (var5.get(i).getEffect() != null)
			{
				averageAmplifier += var5.get(i).getEffect().getAmplifier() + 1;
			}
		}
		averageAmplifier /= var5.size();
		averageAmplifier -= 1;

		return averageAmplifier;
	}
	
	public static int getAverageDuration(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int averageDuration = 0;
		for (int i = 0; i < var5.size(); i++)
		{
			if (var5.get(i).getEffect() != null)
			{
				averageDuration += var5.get(i).getEffect().getDuration();
			}
		}
		averageDuration /= var5.size();
		return averageDuration;
	}
	
	public static int getMaxAmplifier(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int maxAmplifier = 0;
		for (int i = 0; i < var5.size(); i++)
		{
			if (var5.get(i).getEffect() != null && var5.get(i).getEffect().getAmplifier() > maxAmplifier)
			{
				maxAmplifier = var5.get(i).getEffect().getAmplifier();
			}
		}

		return maxAmplifier;
	}
	
	public static int getMaxDuration(List<Brewing> potion)
	{
		List<Brewing> var5 = potion;
		int maxDuration = 0;
		for (int i = 0; i < var5.size(); i++)
		{
			if (var5.get(i).getEffect() != null && var5.get(i).getEffect().getDuration() > maxDuration)
			{
				maxDuration = var5.get(i).getEffect().getDuration();
			}
		}
		return maxDuration;
	}
	
	public float getValue(ItemStack potion)
	{
		return Brewing.getExperience(potion) * 100 / 223.9F;
	}
	
	public static List<String> getUsedTo(ItemStack potion)
	{
		List<String> usedTo = new LinkedList<String>();
		List<Brewing> var5 = ((ItemPotion2)potion.getItem()).getEffects(potion);
		for (Brewing b1 : Brewing.brewingList)
		{
			if (b1 != null && var5.get(0) instanceof BrewingBase)
			{
				if (((BrewingBase)var5.get(0)).basename == (b1.getBase() != null ? b1.getBase().basename : ""))
				{
					usedTo.add(" - " + b1.addBrewingToItemStack(new ItemStack(potion.getItem(), 1, 1)).getDisplayName());
				}
			}
		}
		boolean improvable = true;
		boolean extendable = true;
		for (Brewing b2 : var5)
		{
			if (!b2.isImprovable())
			{
				improvable = false;
			}
			if (!b2.isExtendable())
			{
				extendable = false;
			}
		}
		if (improvable)
		{
			usedTo.add(" - " + potion.getDisplayName() + " (" + StatCollector.translateToLocal("potion.potency." + (var5.get(0).getEffect().getAmplifier() + 1)) + ")");
		}
		if (extendable)
		{
			usedTo.add(" - " + potion.getDisplayName() + " (" + (Potion.getDurationString(var5.get(0).onExtended().getEffect())) + ")");
		}
		return usedTo;
	}
}
