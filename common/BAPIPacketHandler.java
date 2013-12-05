package clashsoft.brewingapi.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import clashsoft.brewingapi.BrewingAPI;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class BAPIPacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if ("BrewingAPI".equals(packet.channel))
		{
			EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
			
			if (entityplayermp.worldObj.isRemote)
			{
				ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
				DataInputStream dis = new DataInputStream(bis);
				try
				{
					double x = dis.readDouble();
					double y = dis.readDouble();
					double z = dis.readDouble();
					int color = dis.readInt();
					boolean isInstant = dis.readBoolean();
					
					BrewingAPI.proxy.playSplashEffect(entityplayermp.worldObj, x, y, z, color, isInstant);
				}
				catch (Exception ex)
				{
				}
			}
		}
	}
}
