package clashsoft.brewingapi.api;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffectHandler
{
	public List<PotionEffect> addEffectQueue = new LinkedList<PotionEffect>();
	public List<Integer> removeEffectQueue = new LinkedList<Integer>();
	
	public void onPotionUpdate(EntityLivingBase entity, PotionEffect effect);
	public boolean canHandle(PotionEffect effect);
}
