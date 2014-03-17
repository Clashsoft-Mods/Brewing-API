package clashsoft.brewingapi.network;

import clashsoft.brewingapi.common.SplashEffectData;
import clashsoft.cslib.minecraft.network.CSNetHandler;

public class BAPINetHandler extends CSNetHandler
{
	public BAPINetHandler()
	{
		super("BAPI");
		this.registerPacket(SplashEffectData.class);
	}
}
