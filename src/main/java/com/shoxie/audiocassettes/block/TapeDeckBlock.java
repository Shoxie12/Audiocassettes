package com.shoxie.audiocassettes.block;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class TapeDeckBlock extends BlockHorizontal implements ITileEntityProvider {
	public static String name = "tapedeck";
	public static final int ID = 2;
	
    public TapeDeckBlock () {
        super(Material.IRON);
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(3);
    }

	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, new IProperty[] { FACING });
	}
	
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
            enumfacing = EnumFacing.NORTH;

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
	    float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
	return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing());
	}
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTapeDeck();
    }
	
    @Override
    public void onBlockHarvested(World w, BlockPos pos, IBlockState state, EntityPlayer player) {
		TileTapeDeck tile = (TileTapeDeck) w.getTileEntity(pos);
		ItemStackHandler h = tile.getHandler();
		if (!w.isRemote) 
			for (int i = 0; i < h.getSlots(); i++) 
				if (!h.getStackInSlot(i).isEmpty()) 
					w.spawnEntity(new EntityItem(w, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)));
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) 
            return true;
        
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileTapeDeck)) 
            return false;
        
        player.openGui(audiocassettes.instance, ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
