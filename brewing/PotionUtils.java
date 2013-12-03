package clashsoft.brewingapi.brewing;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;

public class PotionUtils
{
	public static int getGoodEffects(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
		int badEffects = 0;
		
		for (PotionType b : var5)
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
	
	public static int getBadEffects(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
		int badEffects = 0;
		
		for (PotionType b : var5)
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
	
	public static int getAverageAmplifier(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
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
	
	public static int getAverageDuration(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
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
	
	public static int getMaxAmplifier(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
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
	
	public static int getMaxDuration(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
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
		return PotionType.getExperience(potion) * 100 / 223.9F;
	}
	
	public static List<String> getUsedTo(ItemStack potion)
	{
		List<String> usedTo = new ArrayList<String>();
		List<PotionType> var5 = ((ItemPotion2) potion.getItem()).getEffects(potion);
		for (PotionType b1 : PotionType.potionTypeList)
		{
			if (b1 != null && var5.get(0) instanceof PotionBase)
			{
				if (((PotionBase) var5.get(0)).basename.equals(b1.getBase() != null ? b1.getBase().basename : ""))
				{
					usedTo.add(" - " + b1.addBrewingToItemStack(new ItemStack(potion.getItem(), 1, 1)).getDisplayName());
				}
			}
		}
		boolean improvable = true;
		boolean extendable = true;
		for (PotionType b2 : var5)
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
	
	public static int combineColors(int... colors)
	{
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int i : colors)
		{
			r += (i >> 16) & 255;
			g += (i >> 8) & 255;
			b += (i >> 0) & 255;
		}
		r /= colors.length;
		g /= colors.length;
		b /= colors.length;
		
		return ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255) << 0);
	}
}
