package com.shoxie.audiocassettes.item;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.capability.WalkmanCapability;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.SWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.WalkmanOnDropPacket;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class WalkmanItem extends Item{
	private static String name = "walkman";
	public static final int ID = 3;
	
	public WalkmanItem() {
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
		this.maxStackSize = 1;
	}
	
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (worldIn.isRemote) 
        	new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    	
        playerIn.openGui(audiocassettes.instance, ID, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
    
    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack mp) {
    	if(!mp.hasTagCompound()) getID(mp);

    	NBTTagCompound nbt = mp.getTagCompound();
    	IItemHandler h = mp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        nbt.setTag("walkman", Objects.requireNonNull(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(h, null)));
                

        return nbt;
    }
    
    public void readNBTShareTag(ItemStack mp, @Nullable NBTTagCompound nbt)
    {
        super.readNBTShareTag(mp, nbt);

        if (nbt != null) {
        	IItemHandler h = mp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(h, null, nbt.getTag("walkman"));
        }
    }
    
    @Override
    public boolean getShareTag() {
    	return true;
    }
    
    @Override
    public boolean onDroppedByPlayer(ItemStack mp, EntityPlayer player) {
    	Networking.INSTANCE.sendToServer(new WalkmanOnDropPacket(mp));
    	return true;
    }
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WalkmanCapability();
	}
    
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	if(getCassette(stack).getItem() instanceof AbstractAudioCassetteItem)
            tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks("Current Song: "+AbstractAudioCassetteItem.getCurrentSlot(getCassette(stack))+". "+AbstractAudioCassetteItem.getSongTitle(getCassette(stack))).getFormattedText());
    }
    
	public static void setTagBool(ItemStack mp, String name, boolean val) {
		if(mp.getItem() instanceof WalkmanItem) {
			int intval = (val == true ? 1 : 0);
			NBTTagCompound nbt;
			if (mp.hasTagCompound()) 
				nbt = mp.getTagCompound();
			else nbt = new NBTTagCompound();
            nbt.setInteger(name, intval);
            mp.setTagCompound(nbt);
		}
	}
	public static void setPlaying(ItemStack stack, boolean val) {
		setTagBool(stack,"isPlaying", val);
	}
	
	public static boolean isPlaying(ItemStack stack) {
	    if (stack.hasTagCompound()) {
	    	if(stack.getTagCompound().getInteger("isPlaying")==1) return true;
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
	
	public static ItemStack getMPInHand(EntityPlayer player) {
		if(player == null) return ItemStack.EMPTY;
		ItemStack mp = player.getHeldItemMainhand();
		if(!(mp.getItem() instanceof WalkmanItem))
			mp = player.getHeldItemOffhand();
		return mp.getItem() instanceof WalkmanItem ? mp : ItemStack.EMPTY;
	}
	
	public static ItemStack getMPbyID(EntityPlayer player, String id) {
		for(int i=0; i < 36; i++) 
			if(player.inventory.getStackInSlot(i).getItem() instanceof WalkmanItem) 
				if(WalkmanItem.getID(player.inventory.getStackInSlot(i)).equals(id)) 
					return player.inventory.getStackInSlot(i);
			
		return ItemStack.EMPTY;
	}

    public static void playMusic(ItemStack mp, WorldServer sw, EntityPlayerMP sender) {
    	
    	List<EntityPlayerMP> players = sw.getMinecraftServer().getPlayerList().getPlayers();
    	WalkmanItem.setPlaying(mp, true);
    	for(EntityPlayerMP player : players) {
    		if(
    				Math.abs(player.getPosition().getX() - sender.getPosition().getX()) < audiocassettes.WalkmanMaxSoundDistance &&
    				Math.abs(player.getPosition().getY() - sender.getPosition().getY()) < audiocassettes.WalkmanMaxSoundDistance &&
    				Math.abs(player.getPosition().getZ() - sender.getPosition().getZ()) < audiocassettes.WalkmanMaxSoundDistance
    				)
    		{
				Networking.INSTANCE.sendTo(new SWalkmanPlayPacket(getID(mp),
						sender.getUniqueID().toString(),player==sender ? true : false,getCassette(mp)), 
						player
				);
    		}
    	}
    }
    
	public static String getID(ItemStack mp) {
		if(mp.getItem() instanceof WalkmanItem) {
	    if (!mp.hasTagCompound()) { 
			NBTTagCompound nbt = new NBTTagCompound();
		    Random rand = new Random();
		    int randomNum = rand.nextInt(10000);
	        nbt.setString("uid", Integer.toString(randomNum));
	        mp.setTagCompound(nbt);
	    }
	    
	    return mp.getTagCompound().getString("uid");
		}
		else return null;
	}
		
	public static void stopMusic(ItemStack mp, WorldServer sw, EntityPlayerMP sender) {
    	List<EntityPlayerMP> players = sw.getMinecraftServer().getPlayerList().getPlayers();
    	for(EntityPlayerMP player : players) {
			Networking.INSTANCE.sendTo(new SWalkmanStopPacket(getID(mp),player == sender ? true :false), player);
    	}
	}
	
	public static ItemStack getCassette(ItemStack mp) {
		return mp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
	}
}
