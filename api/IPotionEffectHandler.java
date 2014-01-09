package clashsoft.brewingapi.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffectHandler
{	
	public void onPotionUpdate(int tick, EntityLivingBase entity, PotionEffect effect);
	
	public boolean canHandle(PotionEffect effect);
}
