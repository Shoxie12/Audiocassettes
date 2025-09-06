package com.shoxie.audiocassettes.item;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.menu.WalkmanMenu;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.SWalkmanStopPacket;


import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class WalkmanItem extends Item implements MenuProvider,ICapabilityProvider {
    public int slotscnt = 1;
	public static String name = "walkman";
    public final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
	public WalkmanItem() {
        super(new Item.Properties().stacksTo(1));
	}

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new WalkmanMenu(i, inv, player);
    }

    private final ItemStackHandler inventory = new ItemStackHandler(slotscnt) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
        }
    };

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            if (player instanceof ServerPlayer sp) {
                NetworkHooks.openScreen(sp, this, player.getOnPos());
            }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return this.inventoryOptional.cast();
    }
    
	@Override
	@OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
            ItemStack cassette = getCassette(stack);

            if (cassette.getItem() instanceof AbstractAudioCassetteItem)
                tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks(
                        "Current Song: " + AbstractAudioCassetteItem.getCurrentSlot(cassette)
                                + ". " + AbstractAudioCassetteItem.getSongTitle(cassette)));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.audiocassettes.walkman");
    }
    
	public static void setTagBool(ItemStack mp, String name, boolean val) {
		if(mp.getItem() instanceof WalkmanItem) {
			int intval = (val ? 1 : 0);
            CompoundTag nbt;
			if (mp.hasTag()) 
				nbt = mp.getTag();
			else nbt = new CompoundTag();
            nbt.putInt(name, intval);
            mp.setTag(nbt);
		}
	}

	public static void setPlaying(ItemStack stack, boolean val) {
		setTagBool(stack,"isPlaying", val);
	}
	
	public static boolean isPlaying(ItemStack stack) {
	    if (stack.hasTag()) {
	    	if(stack.getTag().getInt("isPlaying")==1) return true;
	    }
	    else setPlaying(stack,false);
	    
	    return false;
	}

	public static void setSong(int s, ItemStack c) {
		AbstractAudioCassetteItem.setActiveSlot(s, c);
	}
	
	public static boolean switchSong(boolean forward, ItemStack cassette) {

		if(audiocassettes.skipemptyslots) {
			int song = AbstractAudioCassetteItem.getNonEmptySlot(cassette,forward);
			if(song != -1) setSong(song, cassette);
			else return false;
		}
	    else {
				int cur=AbstractAudioCassetteItem.getCurrentSlot(cassette);
				int max=AbstractAudioCassetteItem.getMaxSlots(cassette);
				if(forward) {
			        if(cur < max)
			            setSong(cur+1, cassette);
			        else if(cur > max)
			            setSong(max, cassette);
			        else return false;
				}
				else {
		            if(cur > 1)
		            	setSong(cur-1, cassette);
		            else if(cur < 1)
		            	setSong(1, cassette);
		            else return false;
				}
	    }
		return true;
	}
	
	public static ItemStack getMPInHand(Player player) {
		if(player == null) return ItemStack.EMPTY;
		ItemStack mp = player.getMainHandItem();
		if(!(mp.getItem() instanceof WalkmanItem))
			mp = player.getOffhandItem();
		return mp.getItem() instanceof WalkmanItem ? mp : ItemStack.EMPTY;
	}
	
	public static ItemStack getMPbyID(Player p, String id) {
		if(p.getInventory().getSelected().getItem() instanceof WalkmanItem)
			if(getID(p.getInventory().getSelected()).equals(id)) return p.getInventory().getSelected();

		for(ItemStack s : p.getInventory().items)
			if(!s.isEmpty())
				if(s.getItem() instanceof WalkmanItem)
					if(getID(s).equals(id))
						return s;
			
		return ItemStack.EMPTY;
	}

    public static void playMusic(ItemStack mp, ServerPlayer sender) {
    	
    	List<ServerPlayer> players = sender.getServer().getPlayerList().getPlayers();
    	WalkmanItem.setPlaying(mp, true);
    	ItemStack cassette = getCassette(mp);
    	if(cassette.getItem() instanceof AbstractAudioCassetteItem)
	    	for(ServerPlayer player : players) {
	    		if(
	    				Math.abs(player.getX() - sender.getX()) < audiocassettes.WalkmanMaxSoundDistance &&
	    				Math.abs(player.getY() - sender.getY()) < audiocassettes.WalkmanMaxSoundDistance &&
	    				Math.abs(player.getZ() - sender.getZ()) < audiocassettes.WalkmanMaxSoundDistance
	    				)
	    		{
					Networking.INSTANCE.sendTo(new SWalkmanPlayPacket(getID(mp),
							sender.getUUID().toString(), player == sender,cassette),
							player.connection.connection, NetworkDirection.PLAY_TO_CLIENT
					);
	    		}
	    	}
    }
    
	public static String getID(ItemStack mp) {
		if(mp.getItem() instanceof WalkmanItem) {
	    if (!mp.hasTag()) {
            CompoundTag nbt = new CompoundTag();
		    Random rand = new Random();
		    int randomNum = rand.nextInt(10000);
	        nbt.putString("uid", Integer.toString(randomNum));
	        mp.setTag(nbt);
	    }
	    
	    return mp.getTag().getString("uid");
		}
		else return null;
	}
		
	public static void stopMusic(String mpid, ServerPlayer sender, boolean isdropped) {
    	List<ServerPlayer> players = sender.getServer().getPlayerList().getPlayers();
    	for(ServerPlayer player : players) {
			Networking.INSTANCE.sendTo(new SWalkmanStopPacket(mpid,
					player == sender ? isdropped ? false : true : false),
					player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    	}
	}
	
	public static ItemStack getCassette(ItemStack mp) {
        AtomicReference<ItemStack> ret = new AtomicReference<>(ItemStack.EMPTY);
		if(mp != null)
			if(mp.getItem() instanceof WalkmanItem)
                mp.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
                    ret.set(inventory.getStackInSlot(0));
                });


		return ret.get();
	}
	
	public static boolean isPlayerOwnMp(Player p, String id) {
		NonNullList<ItemStack> playerslots = p.getInventory().items;
		if(p.getInventory().getSelected().getItem() instanceof WalkmanItem) {
			if(getID(p.getInventory().getSelected()).equals(id)) return true;
		}
		else
			for(ItemStack s : playerslots)
				if(!s.isEmpty())
					if(s.getItem() instanceof WalkmanItem)
						if(getID(s).equals(id))
							return true;
		return false;
	}
}
