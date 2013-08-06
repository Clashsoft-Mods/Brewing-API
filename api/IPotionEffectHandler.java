package clashsoft.brewingapi.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffectHandler
{
	public List<PotionEffect> getAddQueue();
	public List<Integer> getRemoveQueue();
	
	public void clearAddQueue();
	public void clearRemoveQueue();
	
	public void onPotionUpdate(EntityLivingBase entity, PotionEffect effect);
	public boolean canHandle(PotionEffect effect);
}
