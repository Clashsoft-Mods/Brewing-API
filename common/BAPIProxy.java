package clashsoft.brewingapi.common;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.inventory.ContainerBrewingStand2;
import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.network.PacketSplashEffect;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import clashsoft.cslib.minecraft.common.BaseProxy;
import clashsoft.cslib.minecraft.network.CSPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BAPIProxy extends BaseProxy
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == BrewingAPI.brewingStand2ID)
		{
			return new ContainerBrewingStand2(player.inventory, (TileEntityBrewingStand2) world.getTileEntity(x, y, z));
		}
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
			CSPacket packet = new PacketSplashEffect(x, y, z, color, isInstant);
			BrewingAPI.instance.netHandler.sendToAll(packet);
		}
	}
}