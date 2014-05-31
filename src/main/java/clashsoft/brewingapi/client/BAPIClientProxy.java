package clashsoft.brewingapi.client;

import java.util.Random;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.client.gui.GuiBrewingStand2;
import clashsoft.brewingapi.client.renderer.RenderPotion2;
import clashsoft.brewingapi.common.BAPIProxy;
import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BAPIClientProxy extends BAPIProxy
{
	public static int	splashpotioncolor;
	
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityPotion2.class, new RenderPotion2());
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == BrewingAPI.brewingStand2ID)
		{
			return new GuiBrewingStand2(player.inventory, (TileEntityBrewingStand2) world.getTileEntity(x, y, z));
		}
		return null;
	}
	
	@Override
	public void playSplashEffect(World world, double x, double y, double z, int color, boolean isInstant)
	{
		RenderGlobal renderGlobal = Minecraft.getMinecraft().renderGlobal;
		
		Random random = world.rand;
		String crackParticleName = "iconcrack_" + Item.getIdFromItem(BrewingAPI.potion2);
		
		double distance;
		float colorMultiplier;
		double velocityX;
		double velocityY;
		double velocityZ;
		double velocityMultiplier;
		
		for (int i = 0; i < 8; ++i)
		{
			renderGlobal.spawnParticle(crackParticleName, x, y, z, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
		}
		
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color >> 0 & 255) / 255.0F;
		String particleName = isInstant ? "instantSpell" : "spell";
		
		for (int i = 0; i < 100; ++i)
		{
			velocityMultiplier = random.nextDouble() * 4.0D;
			distance = random.nextDouble() * Math.PI * 2.0D;
			velocityX = Math.cos(distance) * velocityMultiplier;
			velocityY = 0.01D + random.nextDouble() * 0.5D;
			velocityZ = Math.sin(distance) * velocityMultiplier;
			EntityFX entityfx = renderGlobal.doSpawnParticle(particleName, x + velocityX * 0.1D, y + 0.3D, z + velocityZ * 0.1D, velocityX, velocityY, velocityZ);
			
			if (entityfx != null)
			{
				colorMultiplier = 0.75F + random.nextFloat() * 0.25F;
				entityfx.setRBGColorF(r * colorMultiplier, g * colorMultiplier, b * colorMultiplier);
				entityfx.multiplyVelocity((float) velocityMultiplier);
			}
		}
	}
}
