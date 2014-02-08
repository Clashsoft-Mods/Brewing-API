package clashsoft.brewingapi.common;

import clashsoft.cslib.minecraft.network.CSCodec;
import clashsoft.cslib.minecraft.network.CSMessageHandler;
import clashsoft.cslib.minecraft.network.CSPacketHandler;

import net.minecraft.network.Packet;

public class BAPIPacketHandler extends CSPacketHandler
{
	public static final BAPIPacketHandler	instance	= new BAPIPacketHandler();
	
	private BAPIPacketHandler()
	{
		super("BAPI");
	}
	
	@Override
	public CSCodec createCodec()
	{
		return new BAPICodec();
	}
	
	private static class BAPICodec extends CSCodec
	{
		@Override
		public void addDiscriminators()
		{
			this.addDiscriminator(0, SplashEffectData.class);
		}
	}
	
	@Override
	public CSMessageHandler createMessageHandler()
	{
		return new BAPIMessageHandler();
	}
	
	private static class BAPIMessageHandler extends CSMessageHandler<SplashEffectData>
	{
		@Override
		public void process(SplashEffectData msg)
		{
			
		}
	}
	
	public static Packet getPacket(SplashEffectData data)
	{
		return instance.getServerChannel().generatePacketFrom(data);
	}
}