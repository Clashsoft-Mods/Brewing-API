package clashsoft.brewingapi.brewing;

import java.util.List;

import net.minecraft.item.ItemStack;

public class PotionUtils
{
	public static int getGoodEffects(List<PotionType> potion)
	{
		return potion.size() - getBadEffects(potion);
	}
	
	public static int getBadEffects(List<PotionType> potion)
	{
		int badEffects = 0;
		
		for (PotionType pt : potion)
		{
			if (pt.isBadEffect())
				badEffects++;
		}
		
		return badEffects;
	}
	
	public static int getAverageAmplifier(List<PotionType> potion)
	{
		int averageAmplifier = 0;
		for (PotionType pt : potion)
			averageAmplifier += pt.getAmplifier() + 1;
		averageAmplifier /= potion.size();
		averageAmplifier -= 1;
		
		return averageAmplifier;
	}
	
	public static int getAverageDuration(List<PotionType> potion)
	{
		int averageDuration = 0;
		for (PotionType pt : potion)
			averageDuration += pt.getDuration();
		averageDuration /= potion.size();
		return averageDuration;
	}
	
	public static int getMaxAmplifier(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
		int maxAmplifier = 0;
		for (PotionType pt : potion)
			if (maxAmplifier < pt.getAmplifier())
				maxAmplifier = pt.getAmplifier();
		return maxAmplifier;
	}
	
	public static int getMaxDuration(List<PotionType> potion)
	{
		List<PotionType> var5 = potion;
		int maxDuration = 0;
		for (PotionType pt : potion)
			if (maxDuration < pt.getDuration())
				maxDuration = pt.getDuration();
		return maxDuration;
	}
	
	public static float getValue(ItemStack potion)
	{
		return PotionType.getExperience(potion) * 100 / 223.9F;
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
