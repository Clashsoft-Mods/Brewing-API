package clashsoft.brewingapi.block;

import java.util.List;
import java.util.Random;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBrewingStand2 extends BlockBrewingStand
{
	private Random	rand	= new Random();
	private IIcon	texture;
	
	public BlockBrewingStand2()
	{
		this.setBlockTextureName("brewing_stand");
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return 25;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityBrewingStand2();
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
	{
		this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		this.setBlockBoundsForItemRender();
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	}
	
	@Override
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			if (world.getTileEntity(x, y, z) != null)
			{
				FMLNetworkHandler.openGui(player, BrewingAPI.instance, BrewingAPI.brewingStand2ID, world, x, y, z);
			}
			
			return true;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		double x1 = x + 0.4F + random.nextFloat() * 0.2F;
		double y1 = y + 0.7F + random.nextFloat() * 0.3F;
		double z1 = z + 0.4F + random.nextFloat() * 0.2F;
		world.spawnParticle("smoke", x1, y1, z1, 0.0D, 0.0D, 0.0D);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldBlockMetadata)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		if (tileEntity instanceof IInventory)
		{
			IInventory iinventory = (IInventory) tileEntity;
			
			for (int i = 0; i < iinventory.getSizeInventory(); ++i)
			{
				ItemStack stack = iinventory.getStackInSlot(i);
				
				if (stack != null)
				{
					float offsX = this.rand.nextFloat() * 0.8F + 0.1F;
					float offsY = this.rand.nextFloat() * 0.8F + 0.1F;
					float offsZ = this.rand.nextFloat() * 0.8F + 0.1F;
					
					while (stack.stackSize > 0)
					{
						int j = this.rand.nextInt(21) + 10;
						
						if (j > stack.stackSize)
						{
							j = stack.stackSize;
						}
						stack.stackSize -= j;
						ItemStack newStack = stack.copy();
						newStack.stackSize = j;
						
						EntityItem entityItem = new EntityItem(world, x + offsX, y + offsY, z + offsZ, newStack);
						entityItem.motionX = (float) this.rand.nextGaussian() * 0.05F;
						entityItem.motionY = (float) this.rand.nextGaussian() * 0.05F + 0.2F;
						entityItem.motionZ = (float) this.rand.nextGaussian() * 0.05F;
						world.spawnEntityInWorld(entityItem);
					}
				}
			}
		}
		
		super.breakBlock(world, x, y, z, oldBlock, oldBlockMetadata);
	}
	
	@Override
	public Item getItemDropped(int metadata, Random random, int fortuneLevel)
	{
		return Items.brewing_stand;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return Items.brewing_stand;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		super.registerBlockIcons(iconRegister);
		this.texture = iconRegister.registerIcon(this.getTextureName() + "_base");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconBrewingStandBase()
	{
		return this.texture;
	}
}
