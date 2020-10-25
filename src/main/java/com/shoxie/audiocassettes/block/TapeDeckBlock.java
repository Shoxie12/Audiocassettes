package com.shoxie.audiocassettes.block;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TapeDeckBlock extends Block implements ITileEntityProvider {
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTapeDeck();
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
