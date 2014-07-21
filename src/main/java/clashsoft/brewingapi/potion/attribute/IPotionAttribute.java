package clashsoft.brewingapi.potion.attribute;

import java.util.HashMap;
import java.util.Map;

import clashsoft.brewingapi.potion.type.IPotionType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

public interface IPotionAttribute
{
	public static Map<String, IPotionAttribute> attributes = new HashMap();
	
	public void register();
	
	public IPotionAttribute copy();
	
	public String getName();
	
	public String getDisplayName(IPotionType type);
	
	public PotionEffect getModdedEffect(IPotionType type, PotionEffect effect);
	
	public void writeToNBT(NBTTagCompound nbt);
	
	public void readFromNBT(NBTTagCompound nbt);
}
