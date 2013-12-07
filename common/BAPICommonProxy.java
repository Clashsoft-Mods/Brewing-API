package clashsoft.brewingapi.common;

import clashsoft.brewingapi.inventory.ContainerBrewingStand2;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BAPICommonProxy implements IGuiHandler
{
	public void registerRenderInformation()
	{
		
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(X, Y, Z);
		if (tileEntity instanceof TileEntityBrewingStand2)
			return new ContainerBrewingStand2(player.inventory, (TileEntityBrewingStand2) tileEntity);
		return null;
	}
	
	public World getClientWorld()
	{
		return null;
	}
	
	public void registerRenderers()
	{
	}
	
	public void playSplashEffect(World world, double x, double y, double z, ItemStack stack)
	{
		if (stack != null && stack.getItem() instanceof ItemPotion2)
		{
			ItemPotion2 item = (ItemPotion2) stack.getItem();
			int color = item.getLiquidColor(stack);
			boolean isInstant = item.isEffectInstant(stack);
			this.playSplashEffect(world, x, y, z, color, isInstant);
		}
	}
	
	public void playSplashEffect(World world, double x, double y, double z, int color, boolean isInstant)
	{
		if (!world.isRemote)
		{
			Packet packet = new PacketPlaySplashEffect(x, y, z, color, isInstant);
			
			for (Object player : world.playerEntities)
			{
				PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
			}
		}
	}
}