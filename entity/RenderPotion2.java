package clashsoft.brewingapi.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import clashsoft.brewingapi.item.ItemPotion2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderPotion2 extends Render
{
	public void renderPotion(EntityPotion2 potion, double x, double y, double z, float f1, float f2)
	{
		ItemStack stack = potion.getPotion();
		if (stack != null)
		{
			ItemPotion2 item = (ItemPotion2) stack.getItem();
			IIcon icon = item.getSplashIcon(stack);
			
			if (icon != null)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x, (float) y, (float) z);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				this.bindEntityTexture(potion);
				Tessellator tessellator = Tessellator.instance;
				
				if (stack != null)
				{
					int color = item.getColorFromItemStack(stack, 0);
					float r = (color >> 16 & 255) / 255.0F;
					float g = (color >> 8 & 255) / 255.0F;
					float b = (color & 255) / 255.0F;
					GL11.glColor3f(r, g, b);
					GL11.glPushMatrix();
					this.drawIcon(tessellator, ItemPotion2.getPotionIcon("overlay"));
					GL11.glPopMatrix();
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
				}
				
				this.drawIcon(tessellator, icon);
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
				GL11.glPopMatrix();
			}
		}
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f1, float f2)
	{
		this.renderPotion((EntityPotion2) entity, x, y, z, f1, f2);
	}
	
	private void drawIcon(Tessellator tessellator, IIcon icon)
	{
		float f = icon.getMinU();
		float f1 = icon.getMaxU();
		float f2 = icon.getMinV();
		float f3 = icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, f, f3);
		tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, f1, f3);
		tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, f1, f2);
		tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, f, f2);
		tessellator.draw();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return TextureMap.locationItemsTexture;
	}
}
