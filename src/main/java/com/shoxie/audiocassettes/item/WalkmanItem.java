package com.shoxie.audiocassettes.item;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.capability.WalkmanCapability;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.SWalkmanStopPacket;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

public class WalkmanItem extends Item implements INamedContainerProvider{
	private static String name = "walkman";
	public WalkmanItem() {
		super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
		setRegistryName(name);
	}
	
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    	if (!world.isRemote()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this);
        }
        return new ActionResult<>(ActionResultType.PASS, player.getHeldItem( hand ));
    }
    
    @Override
	public Container createMenu(int id, PlayerInventory inventory, PlayerEntity playerentity) {
		return new WalkmanContainer(id, inventory, playerentity);
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new WalkmanCapability();
	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	ItemStack cassette = getCassette(stack);

		if(cassette.getItem() instanceof AbstractAudioCassetteItem)
            tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks(
            		"Current Song: "+AbstractAudioCassetteItem.getCurrentSlot(cassette)
            		+". "+AbstractAudioCassetteItem.getSongTitle(cassette)));
    }
	
    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getTranslationKey());
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                handler -> {
                    nbt.put("walkman", Objects.requireNonNull(
                    		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(handler, null)));
                }
        );
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);

        if (nbt != null) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                    handler -> {
                    	CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(handler, null, nbt.get("walkman"));
                    }
            );
        }
    }
    
	public static void setTagBool(ItemStack mp, String name, boolean val) {
		if(mp.getItem() instanceof WalkmanItem) {
			int intval = (val == true ? 1 : 0);
			CompoundNBT nbt;
			if (mp.hasTag()) 
				nbt = mp.getTag();
			else nbt = new CompoundNBT();
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
	
	public static ItemStack getMPInHand(PlayerEntity player) {
		if(player == null) return ItemStack.EMPTY;
		ItemStack mp = player.getHeldItemMainhand();
		if(!(mp.getItem() instanceof WalkmanItem))
			mp = player.getHeldItemOffhand();
		return mp.getItem() instanceof WalkmanItem ? mp : ItemStack.EMPTY;
	}
	
	public static ItemStack getMPbyID(PlayerEntity p, String id) {
		if(p.inventory.getItemStack().getItem() instanceof WalkmanItem)
			if(getID(p.inventory.getItemStack()).equals(id)) return p.inventory.getItemStack();

		for(Slot s : p.container.inventorySlots)
			if(s.getHasStack()) 
				if(s.getStack().getItem() instanceof WalkmanItem) 
					if(getID(s.getStack()).equals(id)) 
						return s.getStack();
			
		return ItemStack.EMPTY;
	}

    public static void playMusic(ItemStack mp, ServerPlayerEntity sender) {
    	
    	List<ServerPlayerEntity> players = sender.getServerWorld().getPlayers();
    	WalkmanItem.setPlaying(mp, true);
    	ItemStack cassette = getCassette(mp);
    	if(cassette.getItem() instanceof AbstractAudioCassetteItem)
	    	for(ServerPlayerEntity player : players) {
	    		if(
	    				Math.abs(player.getPosX() - sender.getPosX()) < audiocassettes.WalkmanMaxSoundDistance &&
	    				Math.abs(player.getPosY() - sender.getPosY()) < audiocassettes.WalkmanMaxSoundDistance &&
	    				Math.abs(player.getPosZ() - sender.getPosZ()) < audiocassettes.WalkmanMaxSoundDistance
	    				)
	    		{
					Networking.INSTANCE.sendTo(new SWalkmanPlayPacket(getID(mp),
							sender.getUniqueID().toString(),player==sender ? true : false,cassette), 
							player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT
					);
	    		}
	    	}
    }
    
	public static String getID(ItemStack mp) {
		if(mp.getItem() instanceof WalkmanItem) {
	    if (!mp.hasTag()) { 
			CompoundNBT nbt = new CompoundNBT();
		    Random rand = new Random();
		    int randomNum = rand.nextInt(10000);
	        nbt.putString("uid", Integer.toString(randomNum));
	        mp.setTag(nbt);
	    }
	    
	    return mp.getTag().getString("uid");
		}
		else return null;
	}
		
	public static void stopMusic(String mpid, ServerPlayerEntity sender, boolean isdropped) {
    	List<ServerPlayerEntity> players = sender.getServerWorld().getPlayers();
    	for(ServerPlayerEntity player : players) {
			Networking.INSTANCE.sendTo(new SWalkmanStopPacket(mpid,
					player == sender ? isdropped ? false : true : false),
					player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    	}
	}
	
	public static ItemStack getCassette(ItemStack mp) {
		if(mp != null)
			if(mp.getItem() instanceof WalkmanItem) {
		final ItemStack[] it = new ItemStack[1];
		mp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
		    it[0] = h.getStackInSlot(0);
		});
		return it[0];
	}
		return ItemStack.EMPTY;
	}
	
	public static boolean isPlayerOwnMp(PlayerEntity p, String id) {
		List<Slot> playerslots = p.container.inventorySlots;
		if(p.inventory.getItemStack().getItem() instanceof WalkmanItem) {
			if(getID(p.inventory.getItemStack()).equals(id)) return true;
		}
		else
			for(Slot s : playerslots)
				if(s.getHasStack()) 
					if(s.getStack().getItem() instanceof WalkmanItem) 
						if(getID(s.getStack()).equals(id)) 
							return true;
		return false;
	}
}
