package clashsoft.brewingapi.network;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.cslib.minecraft.network.CSPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketSplashEffect extends CSPacket
{
	public double	x;
	public double	y;
	public double	z;
	public int		color;
	public boolean	instant;
	
	public PacketSplashEffect(double x, double y, double z, int color, boolean instant)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		this.instant = instant;
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeInt(this.color);
		buf.writeBoolean(this.instant);
	}
	
	@Override
	public void read(PacketBuffer buf)
	{
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.color = buf.readInt();
		this.instant = buf.readBoolean();
	}
	
	@Override
	public void handleClient(EntityPlayer player)
	{
		BrewingAPI.proxy.playSplashEffect(BrewingAPI.proxy.getClientWorld(), this.x, this.y, this.z, this.color, this.instant);
	}
	
	@Override
	public void handleServer(EntityPlayerMP player)
	{
		this.handleClient(player);
	}
}
