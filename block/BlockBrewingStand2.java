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
import net.minecraft.tileentity.TileEntityBrewingStand;
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
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether
	 * or not to render the shared face of two adjacent blocks and also whether
	 * the player can attach torches, redstone wire, etc to this block.
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
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		super.onBlockAdded(par1World, par2, par3, par4);
		par1World.setBlockTileEntity(par2, par3, par4, this.createNewTileEntity(par1World));
	}
	
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing
	 * the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World par1World)
	{
		return new TileEntityBrewingStand2();
	}
	
	/**
	 * If this block doesn't render as an ordinary block it will return False
	 * (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	/**
	 * if the specified block is in the given AABB, add its collision bounding
	 * box to the given list
	 */
	public void addCollidingBlockToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
	{
		this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		this.setBlockBoundsForItemRender();
		super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
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
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (par1World.isRemote)
		{
			return true;
		}
		else
		{
			if (par1World.getBlockTileEntity(par2, par3, par4) != null)
			{
				FMLNetworkHandler.openGui(par5EntityPlayer, BrewingAPI.instance, BrewingAPI.BrewingStand2_TEID, par1World, par2, par3, par4);
			}
			
			return true;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		double var6 = par2 + 0.4F + par5Random.nextFloat() * 0.2F;
		double var8 = par3 + 0.7F + par5Random.nextFloat() * 0.3F;
		double var10 = par4 + 0.4F + par5Random.nextFloat() * 0.2F;
		par1World.spawnParticle("smoke", var6, var8, var10, 0.0D, 0.0D, 0.0D);
	}
	
	/**
	 * ejects contained items into the world, and notifies neighbours of an
	 * update, as appropriate
	 */
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		TileEntity var7 = par1World.getBlockTileEntity(par2, par3, par4);
		
		if (var7 instanceof TileEntityBrewingStand2)
		{
			TileEntityBrewingStand2 var8 = (TileEntityBrewingStand2) var7;
			
			for (int var9 = 0; var9 < var8.getSizeInventory(); ++var9)
			{
				ItemStack var10 = var8.getStackInSlot(var9);
				
				if (var10 != null)
				{
					float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var12 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var13 = this.rand.nextFloat() * 0.8F + 0.1F;
					
					while (var10.stackSize > 0)
					{
						int var14 = this.rand.nextInt(21) + 10;
						
						if (var14 > var10.stackSize)
						{
							var14 = var10.stackSize;
						}
						var10.stackSize -= var14;
						ItemStack is = new ItemStack(var10.itemID, var14, var10.getItemDamage());
						is.setTagCompound(var10.getTagCompound());
						EntityItem var15 = new EntityItem(par1World, par2 + var11, par3 + var12, par4 + var13, is);
						float var16 = 0.05F;
						var15.motionX = (float) this.rand.nextGaussian() * var16;
						var15.motionY = (float) this.rand.nextGaussian() * var16 + 0.2F;
						var15.motionZ = (float) this.rand.nextGaussian() * var16;
						par1World.spawnEntityInWorld(var15);
					}
				}
			}
		}
		
		else if (var7 instanceof TileEntityBrewingStand)
		{
			TileEntityBrewingStand var8 = (TileEntityBrewingStand) var7;
			
			for (int var9 = 0; var9 < var8.getSizeInventory(); ++var9)
			{
				ItemStack var10 = var8.getStackInSlot(var9);
				
				if (var10 != null)
				{
					float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var12 = this.rand.nextFloat() * 0.8F + 0.1F;
					float var13 = this.rand.nextFloat() * 0.8F + 0.1F;
					
					while (var10.stackSize > 0)
					{
						int var14 = this.rand.nextInt(21) + 10;
						
						if (var14 > var10.stackSize)
						{
							var14 = var10.stackSize;
						}
						var10.stackSize -= var14;
						ItemStack is = new ItemStack(var10.itemID, var14, var10.getItemDamage());
						is.setTagCompound(var10.getTagCompound());
						EntityItem var15 = new EntityItem(par1World, par2 + var11, par3 + var12, par4 + var13, is);
						float var16 = 0.05F;
						var15.motionX = (float) this.rand.nextGaussian() * var16;
						var15.motionY = (float) this.rand.nextGaussian() * var16 + 0.2F;
						var15.motionZ = (float) this.rand.nextGaussian() * var16;
						par1World.spawnEntityInWorld(var15);
					}
				}
			}
		}
		
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return Item.brewingStand.itemID;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return Item.brewingStand.itemID;
	}
	
	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
	{
		return Container.calcRedstoneFromInventory((IInventory) par1World.getBlockTileEntity(par2, par3, par4));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister par1IconRegister)
	{
		super.registerIcons(par1IconRegister);
		this.texture = par1IconRegister.registerIcon(this.getTextureName() + "_base");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBrewingStandIcon()
	{
		return this.texture;
	}
}
