package clashsoft.brewingapi.potion;

import java.util.List;

import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.item.ItemStack;

public class PotionUtils
{
	public static int getGoodEffects(List<IPotionType> potion)
	{
		int goodEffects = 0;
		
		for (IPotionType pt : potion)
		{
			if (!pt.isBadEffect())
			{
				goodEffects++;
			}
		}
		
		return goodEffects;
	}
	
	public static int getBadEffects(List<IPotionType> potion)
	{
		int badEffects = 0;
		
		for (IPotionType pt : potion)
		{
			if (pt.isBadEffect())
			{
				badEffects++;
			}
		}
		
		return badEffects;
	}
	
	public static int getAverageAmplifier(List<IPotionType> potion)
	{
		int averageAmplifier = 0;
		for (IPotionType pt : potion)
		{
			averageAmplifier += pt.getAmplifier() + 1;
		}
		
		averageAmplifier = (averageAmplifier / potion.size()) - 1;
		return averageAmplifier;
	}
	
	public static int getAverageDuration(List<IPotionType> potion)
	{
		int averageDuration = 0;
		for (IPotionType pt : potion)
		{
			averageDuration += pt.getDuration();
		}
		averageDuration /= potion.size();
		return averageDuration;
	}
	
	public static int getMaxAmplifier(List<IPotionType> potion)
	{
		int maxAmplifier = 0;
		for (IPotionType pt : potion)
		{
			if (maxAmplifier < pt.getAmplifier())
			{
				maxAmplifier = pt.getAmplifier();
			}
		}
		return maxAmplifier;
	}
	
	public static int getMaxDuration(List<IPotionType> potion)
	{
		int maxDuration = 0;
		for (IPotionType pt : potion)
		{
			if (maxDuration < pt.getDuration())
			{
				maxDuration = pt.getDuration();
			}
		}
		return maxDuration;
	}
	
	public static float getValue(ItemStack potion)
	{
		return PotionType.getExperience(potion) * 2.25F;
	}
	
	public static int combineColors(int... colors)
	{
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int i : colors)
		{
			r += i >> 16 & 255;
			g += i >> 8 & 255;
			b += i >> 0 & 255;
		}
		r /= colors.length;
		g /= colors.length;
		b /= colors.length;
		
		return (r & 255) << 16 | (g & 255) << 8 | (b & 255) << 0;
	}
}
