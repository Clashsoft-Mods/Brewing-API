package clashsoft.brewingapi.block;

import java.util.List;
import java.util.Random;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockBrewingStand2 extends BlockBrewingStand
{
	private Random	rand	= new Random();
	private Icon	texture;
	
	public BlockBrewingStand2(int par1)
	{
		super(par1);
		this.setTextureName("brewing_stand");
	}
	
	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return 25;
	}
	
	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
	}
	
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityBrewingStand2();
	}
	
	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	/**
	 * if the specified block is in the given AABB, add its collision bounding box to the given list
	 */
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
	{
		this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		this.setBlockBoundsForItemRender();
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	}
	
	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			if (world.getBlockTileEntity(x, y, z) != null)
			{
				FMLNetworkHandler.openGui(player, BrewingAPI.instance, BrewingAPI.brewingStand2ID, world, x, y, z);
			}
			
			return true;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		double var6 = x + 0.4F + random.nextFloat() * 0.2F;
		double var8 = y + 0.7F + random.nextFloat() * 0.3F;
		double var10 = z + 0.4F + random.nextFloat() * 0.2F;
		world.spawnParticle("smoke", var6, var8, var10, 0.0D, 0.0D, 0.0D);
	}
	
	/**
	 * Ejects contained items into the world, and notifies neighbours of an update, as appropriate
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, int oldBlockID, int oldBlockMetadata)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
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
		
		super.breakBlock(world, x, y, z, oldBlockID, oldBlockMetadata);
	}
	
	@Override
	public int idDropped(int metadata, Random random, int fortuneLevel)
	{
		return Item.brewingStand.itemID;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World world, int x, int y, int z)
	{
		return Item.brewingStand.itemID;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		return Container.calcRedstoneFromInventory((IInventory) world.getBlockTileEntity(x, y, z));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		this.texture = iconRegister.registerIcon(this.getTextureName() + "_base");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBrewingStandIcon()
	{
		return this.texture;
	}
}
