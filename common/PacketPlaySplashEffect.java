package clashsoft.brewingapi.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketPlaySplashEffect extends Packet250CustomPayload
{
	public PacketPlaySplashEffect(double x, double y, double z, int color, boolean isInstant)
	{
		super("BrewingAPI", getData(x, y, z, color, isInstant));
	}
	
	public static byte[] getData(double x, double y, double z, int color, boolean isInstant)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		try
		{
			dos.writeDouble(x);
			dos.writeDouble(y);
			dos.writeDouble(z);
			dos.writeInt(color);
			dos.writeBoolean(isInstant);
		}
		catch (Exception ex)
		{
		}
		
		return bos.toByteArray();
	}
}
