package clashsoft.brewingapi.potion.type;

import net.minecraft.potion.PotionEffect;

import clashsoft.cslib.logging.CSLog;

/**
 * A {@link IPotionType} implementation that is used when
 * {@link PotionType#getFromEffect_(PotionEffect)} returns null. In that case, a
 * new instance of this class is created and registered using
 * {@link #register()}. That ensures that {@link PotionTypeDelegates} do not
 * throw {@link NullPointerException}s because their stored potion type is null.
 * Potion types of this class do not get added to the creative inventory.
 * 
 * @author Clashsoft
 */
public class DummyPotionType extends PotionType
{
	public DummyPotionType()
	{
		super();
	}
	
	public DummyPotionType(PotionEffect effect, int maxAmplifier, int maxDuration)
	{
		super(effect, maxAmplifier, maxDuration);
	}
	
	@Override
	public DummyPotionType register()
	{
		int potionID = this.getPotionID();
		if (potionID != -1)
		{
			if (potionTypes.put(potionID, this) != null)
			{
				CSLog.warning("Registering duplicate potion ID " + potionID);
			}
		}
		
		potionTypeList.add(this);
		
		return this;
	}
}
