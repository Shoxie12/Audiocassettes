package com.shoxie.audiocassettes.proxy;

import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.container.TapeDeckContainer;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.screen.GuiBoomBox;
import com.shoxie.audiocassettes.screen.GuiTapeDeck;
import com.shoxie.audiocassettes.screen.GuiWalkman;
import com.shoxie.audiocassettes.tile.TileBoomBox;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTapeDeck) {
            return new TapeDeckContainer(player.inventory, (TileTapeDeck) tile);
        }
        if (tile instanceof TileBoomBox) {
            return new BoomBoxContainer(player.inventory, (TileBoomBox) tile);
        }
        ItemStack mp = WalkmanItem.getMPInHand(player);
        if (mp.getItem() instanceof WalkmanItem) {
        	return new WalkmanContainer(player);
        }
        
        
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTapeDeck) {
        	TileTapeDeck containerTileEntity = (TileTapeDeck) tile;
            return new GuiTapeDeck(new TapeDeckContainer(player.inventory, containerTileEntity));
        }
        if (tile instanceof TileBoomBox) {
        	TileBoomBox containerTileEntity = (TileBoomBox) tile;
            return new GuiBoomBox(containerTileEntity, new BoomBoxContainer(player.inventory, containerTileEntity));
        }
        ItemStack mp = WalkmanItem.getMPInHand(player);
        if (mp.getItem() instanceof WalkmanItem) {
            return new GuiWalkman(new WalkmanContainer(player));
        }
        return null;
    }
}