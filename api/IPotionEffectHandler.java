package clashsoft.brewingapi.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffectHandler
{	
	public static class PotionQueue
	{
		private List<PotionEffect> addQueue = new ArrayList();
		private List<Integer> removeQueue = new ArrayList();
		
		public void add(PotionEffect effect)
		{
			this.addQueue.add(effect);
		}
		
		public void remove(PotionEffect effect)
		{
			this.removeQueue.add(effect.getPotionID());
		}
		
		public void remove(int effectID)
		{
			this.removeQueue.add(Integer.valueOf(effectID));
		}
		
		public synchronized void updateEntity(EntityLivingBase entity)
		{
			for (PotionEffect effect : this.addQueue)
				entity.addPotionEffect(effect);
			for (Integer effect : this.removeQueue)
				entity.removePotionEffect(effect.intValue());
			
			this.addQueue.clear();
			this.removeQueue.clear();
		}
	}
	
	public void onPotionUpdate(PotionQueue queue, EntityLivingBase entity, PotionEffect effect);
	
	public boolean canHandle(PotionEffect effect);
}
