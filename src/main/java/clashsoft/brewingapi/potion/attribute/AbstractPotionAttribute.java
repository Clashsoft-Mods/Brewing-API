package clashsoft.brewingapi.potion.attribute;

import net.minecraft.util.StatCollector;

import clashsoft.brewingapi.potion.type.IPotionType;

public abstract class AbstractPotionAttribute implements IPotionAttribute
{
	@Override
	public IPotionAttribute register()
	{
		attributes.put(this.getName(), this);
		return this;
	}
	
	@Override
	public String getDisplayName(IPotionType type)
	{
		return StatCollector.translateToLocal("potion.attribute." + this.getName() + ".name");
	}
}
