package clashsoft.brewingapi;

import java.util.Random;

import clashsoft.brewingapi.entity.EntityPotion2;
import clashsoft.brewingapi.entity.RenderPotion2;
import clashsoft.brewingapi.item.ItemPotion2;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy
{
	public static int	splashpotioncolor;
	
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityPotion2.class, new RenderPotion2(BrewingAPI.potion2, 154));
		setCustomRenderers();
	}
	
	public static void setCustomRenderers()
	{
	}
	
	@Override
	public void playSplashEffect(World par0World, int par1, int par2, int par3, ItemStack par4ItemStack)
	{
		Random random = par0World.rand;
		double d0;
		double d1;
		double d2;
		String s;
		int j1;
		int k1;
		double d3;
		double d4;
		double d5;
		double d6;
		double d7;
		
		d0 = par1;
		d1 = par2;
		d2 = par3;
		s = "iconcrack_" + Item.potion.itemID;
		
		for (j1 = 0; j1 < 8; ++j1)
		{
			par0World.spawnParticle(s, d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
		}
		
		j1 = ((ItemPotion2) par4ItemStack.getItem()).getColorFromItemStack(par4ItemStack, 0);
		float f = (j1 >> 16 & 255) / 255.0F;
		float f1 = (j1 >> 8 & 255) / 255.0F;
		float f2 = (j1 >> 0 & 255) / 255.0F;
		String s1 = "spell";
		
		if (BrewingAPI.potion2.isEffectInstant(par4ItemStack))
		{
			s1 = "instantSpell";
		}
		
		for (k1 = 0; k1 < 100; ++k1)
		{
			d7 = random.nextDouble() * 4.0D;
			d3 = random.nextDouble() * Math.PI * 2.0D;
			d4 = Math.cos(d3) * d7;
			d5 = 0.01D + random.nextDouble() * 0.5D;
			d6 = Math.sin(d3) * d7;
			EntityFX entityfx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(s1, d0 + d4 * 0.1D, d1 + 0.3D, d2 + d6 * 0.1D, d4, d5, d6);
			
			if (entityfx != null)
			{
				float f3 = 0.75F + random.nextFloat() * 0.25F;
				entityfx.setRBGColorF(f * f3, f1 * f3, f2 * f3);
				entityfx.multiplyVelocity((float) d7);
			}
		}
	}
}
