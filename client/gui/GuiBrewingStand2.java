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
	
	public GuiBrewingStand2(InventoryPlayer par1InventoryPlayer, TileEntityBrewingStand2 par2TileEntityBrewingStand2)
	{
		super(new ContainerBrewingStand2(par1InventoryPlayer, par2TileEntityBrewingStand2));
		this.brewingStand = par2TileEntityBrewingStand2;
		brewingStand.thePlayer = par1InventoryPlayer.player;
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String s = this.brewingStand.isInvNameLocalized() ? this.brewingStand.getInvName() : StatCollector.translateToLocal(this.brewingStand.getInvName());
		this.fontRenderer.drawString(s, (this.xSize - this.fontRenderer.getStringWidth(s)) / 2, 6, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiBrewingStand2.alchemy_gui);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
		int var7 = this.brewingStand.getBrewTime();
		
		if (var7 > 0)
		{
			int var8 = (int) (28.0F * (1.0F - (float) var7 / brewingStand.getMaxBrewTime()));
			
			if (var8 > 0)
			{
				this.drawTexturedModalRect(var5 + 97, var6 + 16, 176, 0, 9, var8);
			}
			
			int var9 = var7 / 2 % 7;
			
			switch (var9)
			{
			case 0:
				var8 = 29;
				break;
			case 1:
				var8 = 24;
				break;
			case 2:
				var8 = 20;
				break;
			case 3:
				var8 = 16;
				break;
			case 4:
				var8 = 11;
				break;
			case 5:
				var8 = 6;
				break;
			case 6:
				var8 = 0;
			}
			
			if (var8 > 0)
			{
				this.drawTexturedModalRect(var5 + 65, var6 + 14 + 29 - var8, 185, 29 - var8, 12, var8);
			}
		}
	}
}
