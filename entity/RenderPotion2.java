package clashsoft.brewingapi.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import clashsoft.brewingapi.item.ItemPotion2;

@SideOnly(Side.CLIENT)
public class RenderPotion2 extends Render
{
	private Item	field_94151_a;
	private int		field_94150_f;
	
	public RenderPotion2(Item par1Item, int par2)
	{
		this.field_94151_a = par1Item;
		this.field_94150_f = par2;
	}
	
	public RenderPotion2(Item par1Item)
	{
		this(par1Item, 0);
	}
	
	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void doRender(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		ItemStack stack = ((EntityPotion2) par1Entity).potionDamage;
		Icon icon = stack != null && stack.getItem() != null ? ((ItemPotion2) stack.getItem()).getSplashIcon(stack) : ItemPotion2.func_94589_d("bottle_splash");
		
		if (icon != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float) par2, (float) par4, (float) par6);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			this.func_110777_b(par1Entity);
			Tessellator tessellator = Tessellator.instance;
			
			if (icon == ItemPotion.func_94589_d("bottle_splash"))
			{
				int i = ((ItemPotion2) stack.getItem()).getColorFromItemStack(stack, 0);
				float f2 = (i >> 16 & 255) / 255.0F;
				float f3 = (i >> 8 & 255) / 255.0F;
				float f4 = (i & 255) / 255.0F;
				GL11.glColor3f(f2, f3, f4);
				GL11.glPushMatrix();
				this.func_77026_a(tessellator, ItemPotion.func_94589_d("overlay"));
				GL11.glPopMatrix();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
			}
			
			this.func_77026_a(tessellator, icon);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}
	
	@Override
	protected ResourceLocation func_110775_a(Entity par1Entity)
	{
		return TextureMap.field_110576_c;
	}
	
	private void func_77026_a(Tessellator par1Tessellator, Icon par2Icon)
	{
		float f = par2Icon.getMinU();
		float f1 = par2Icon.getMaxU();
		float f2 = par2Icon.getMinV();
		float f3 = par2Icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
		par1Tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, f, f3);
		par1Tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, f1, f3);
		par1Tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, f1, f2);
		par1Tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, f, f2);
		par1Tessellator.draw();
	}
}
