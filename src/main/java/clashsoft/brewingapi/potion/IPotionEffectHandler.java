package clashsoft.brewingapi.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffectHandler
{
	/**
	 * Potion update callback. This method gets called each tick to update the
	 * potion effect and apply it's effect to the given {@link EntityLivingBase}
	 * {@code entity}.
	 * 
	 * @param tick
	 *            the current game tick
	 * @param entity
	 *            the living entity
	 * @param effect
	 *            the potion effect
	 */
	public void onPotionUpdate(int tick, EntityLivingBase entity, PotionEffect effect);
	
	/**
	 * Returns true if this effect handler can handle the given
	 * {@link PotionEffect} {@code effect}.
	 * 
	 * @param effect
	 *            the effect
	 * @return true, if this can handle the effect
	 */
	public boolean canHandle(PotionEffect effect);
}
