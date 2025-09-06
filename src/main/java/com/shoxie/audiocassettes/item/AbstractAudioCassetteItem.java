package com.shoxie.audiocassettes.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.shoxie.audiocassettes.init.Init.BLANK_RECORD_SOUND_EVENT;

public abstract class AbstractAudioCassetteItem extends Item {
	
	protected String name;
	protected int maxslots;
	protected int MaxWriteTime;
    public AbstractAudioCassetteItem() {
        super(new Item.Properties());
    }
	
	@Override
	@OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.hasTag()) {
			int song = getCurrentSlot(stack);
			int max = getMaxSlots(stack);
			boolean fdots = (song > 3);
			boolean ldots = (song < max - 3);
			int k = (song > 3 ? song < max-3 ? song-3 : max-6 : 1);
			int j = (song > 3 ? song < max-3 ? song+3 : max : 7);
			for(int i=k;i<=j;i++)
				if(stack.getTag().contains("Song"+i))
					if((i==k && fdots) || (i==j && ldots))
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks("..."));
					else {
						String str = stack.getTag().getString("SongName"+i);
						if(str.equals("--Empty--"))
							str = Component.translatable("sound.audiocassettes.emptysound").getString();
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks((i==song? "EW " : "")+i+". "+str));
					}
		}
	}
	
	public static void appendSongs(ItemStack stack, ResourceLocation res, String songname) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		CompoundTag nbt = new CompoundTag();
	    if (stack.hasTag())
	        nbt = stack.getTag();
	    else
	    {
	        nbt = new CompoundTag();
			for(int i=1;i<=c.maxslots;i++)
			{
				nbt.putString("Song"+i, ("audiocassettes"+":"+"empty"));
				nbt.putString("SongName"+i, "--Empty--");
			}
	    }
	    int curslot = AbstractAudioCassetteItem.getCurrentSlot(stack);
	    nbt.putString("Song"+curslot, (res.getNamespace()+":"+res.getPath()));
		nbt.putString("SongName"+curslot, songname);
		nbt.putInt("ms", curslot);
		nbt.putInt("max", c.maxslots);
		
		stack.setTag(nbt);
	}
	
	public static SoundEvent getCurrentSong(ItemStack stack) {
		if(stack == null) return BLANK_RECORD_SOUND_EVENT.get();
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return BLANK_RECORD_SOUND_EVENT.get();
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
		if(ms < 1 || ms > c.maxslots)
			return BLANK_RECORD_SOUND_EVENT.get();
		
		CompoundTag nbt;
		nbt = stack.getTag();
        var snd = SoundEvent.createFixedRangeEvent(new ResourceLocation(nbt.getString("Song"+ms)),128);
        snd.getRange(5);
		return snd;
	}
	
	public static int getMaxSlots(ItemStack stack) {
		if(stack == null) return 0;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return 0;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		return c.maxslots;
	}
	
	public static int getCurrentSlot(ItemStack stack) {
		if(stack == null) return 0;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return 0;
		CompoundTag nbt = new CompoundTag();
		if (stack.hasTag()) {
		    nbt = stack.getTag();
		    return nbt.getInt("ms");
		}
		return 0;
	}
	
	public int getMaxWriteTime() {
		return this.MaxWriteTime;
	}
	
	public static String getSongTitle(ItemStack stack) {
		if(stack == null) return "--Empty--";
		if(stack.getItem() instanceof AbstractAudioCassetteItem c) {
            int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
			if(ms < 1 || ms > c.maxslots)
				return "--Empty--";
			
			CompoundTag nbt = stack.getTag();
			return nbt.getString("SongName"+ms);
		}
		else return "--Empty--";
	}
	
	public static void setActiveSlot(int song, ItemStack stack) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		int newsong = 1;
		CompoundTag nbt;
		    if (stack.hasTag()) {
		    	int maxsongs = 0;
		    	int cursong = 0;
		    	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
		    		cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
		    		maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
		    	}
		    	
		    	if(maxsongs> 0 && (song>0 && song<=maxsongs))
		    		newsong = song;
		    	else if(song > maxsongs)
		    		newsong = maxsongs;
		    		
		    	if(cursong != newsong) {
			        nbt = stack.getTag();
			    	nbt.putInt("ms", newsong);
			    	stack.setTag(nbt);
		    	}
		  }
	}
	
	public static int getNonEmptySlot(ItemStack c, boolean direction) {
		int cur=getCurrentSlot(c);
		int max=getMaxSlots(c);
		if(direction) {
			cur = cur < max ? cur+1 : cur;
			for(int i = cur;i<=max;i++) {
				if(!isSlotEmpty(i, c)) return i;
			}
		}
		else {
			cur = cur > 1 ? cur-1 : cur;
			for(int i = cur;i>=1;i--) {
				if(!isSlotEmpty(i, c)) return i;
			}
		}
		return -1;
	}
	
	public static boolean isSlotEmpty(int i, ItemStack c) {
		if(c == null || c == ItemStack.EMPTY || !c.hasTag()) return true;
		CompoundTag nbt;
		nbt = c.getTag();
		return nbt.getString("Song"+i).equals("audiocassettes:empty");
	}
}