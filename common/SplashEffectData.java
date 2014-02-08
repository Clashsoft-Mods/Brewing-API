package clashsoft.brewingapi.common;

import io.netty.buffer.ByteBuf;
import clashsoft.cslib.minecraft.network.CSPacket;

public class SplashEffectData implements CSPacket
{
	public double	x;
	public double	y;
	public double	z;
	public int		color;
	public boolean	instant;
	
	public SplashEffectData(double x, double y, double z, int color, boolean instant)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		this.instant = instant;
	}
	
	@Override
	public void write(ByteBuf buf)
	{
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeInt(this.color);
		buf.writeBoolean(this.instant);
	}
	
	@Override
	public void read(ByteBuf buf)
	{
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.color = buf.readInt();
		this.instant = buf.readBoolean();
	}
}
