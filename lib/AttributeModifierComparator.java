package clashsoft.brewingapi.lib;

import java.util.Comparator;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class AttributeModifierComparator implements Comparator<AttributeModifier>
{
	@Override
	public int compare(AttributeModifier o1, AttributeModifier o2)
	{
		return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
	}
}