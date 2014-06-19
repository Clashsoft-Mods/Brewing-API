package clashsoft.brewingapi.potion;

public interface IPotionList
{
	/**
	 * Constructs and initializes the {@link PotionType PotionTypes}.
	 */
	public void initPotionTypes();
	
	/**
	 * Registers the {@link PotionType PotionTypes}.
	 */
	public void loadPotionTypes();
}
