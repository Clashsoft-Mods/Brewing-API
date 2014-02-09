package clashsoft.brewingapi.client.gui;

import org.lwjgl.opengl.GL11;

import clashsoft.brewingapi.inventory.ContainerBrewingStand2;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiBrewingStand2 extends GuiContainer
{
	private TileEntityBrewingStand2	brewingStand;
	public static ResourceLocation	alchemy_gui	= new ResourceLocation("textures/gui/container/brewing_stand.png");
	
	public GuiBrewingStand2(InventoryPlayer inventory, TileEntityBrewingStand2 brewingStand)
	{
		super(new ContainerBrewingStand2(inventory, brewingStand));
		this.brewingStand = brewingStand;
		brewingStand.thePlayer = inventory.player;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = this.brewingStand.hasCustomInventoryName() ? this.brewingStand.getInventoryName() : StatCollector.translateToLocal(this.brewingStand.getInventoryName());
		this.fontRendererObj.drawString(s, (this.xSize - this.fontRendererObj.getStringWidth(s)) / 2, 6, 4210752);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
		
		this.fontRendererObj.drawString("BREWING API", 0, 0, 0xFF0000);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiBrewingStand2.alchemy_gui);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int time = this.brewingStand.getBrewTime();
		
		if (time > 0)
		{
			int progress = (int) (28.0F * (1.0F - (float) time / this.brewingStand.getMaxBrewTime()));
			
			if (progress > 0)
			{
				this.drawTexturedModalRect(x + 97, y + 16, 176, 0, 9, progress);
			}
			
			int bubbles = time / 2 % 7;
			
			switch (bubbles)
			{
				case 0:
					progress = 29;
					break;
				case 1:
					progress = 24;
					break;
				case 2:
					progress = 20;
					break;
				case 3:
					progress = 16;
					break;
				case 4:
					progress = 11;
					break;
				case 5:
					progress = 6;
					break;
				case 6:
					progress = 0;
			}
			
			if (progress > 0)
			{
				this.drawTexturedModalRect(x + 65, y + 14 + 29 - progress, 185, 29 - progress, 12, progress);
			}
		}
	}
}
