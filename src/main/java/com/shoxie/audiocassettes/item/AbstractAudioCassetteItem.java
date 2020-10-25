package com.shoxie.audiocassettes.item;

import java.util.List;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.ModSoundEvents;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractAudioCassetteItem extends Item{
	
	protected String name;
	protected int maxslots;
	protected int MaxWriteTime;

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		if (stack.hasTagCompound()) {
			int song = getCurrentSlot(stack);
			int max = getMaxSlots(stack);
			boolean fdots = (song > 3 ? true : false);
			boolean ldots = (song < max-3 ? true : false);
			int k = (song > 3 ? song < max-3 ? song-3 : max-6 : 1);
			int j = (song > 3 ? song < max-3 ? song+3 : max : 7);
			for(int i=k;i<=j;i++)
				if(stack.getTagCompound().hasKey("Song"+1))
					if((i==k && fdots) || (i==j && ldots))
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks("...").getFormattedText());
					else {
						String str = stack.getTagCompound().getString("SongName"+i);
						if(str == "--Empty--")
							str = I18n.format("sound.audiocassettes.emptysound");
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks((i==song? "* " : "")+i+". "+str).getUnformattedText());
					}
		}
    }
	
	public static void appendSongs(ItemStack stack, String song, String songname) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		NBTTagCompound nbt = new NBTTagCompound();
	    if (stack.hasTagCompound())
	        nbt = stack.getTagCompound();
	    else
	    {
	        nbt = new NBTTagCompound();
			for(int i=1;i<=c.maxslots;i++)
			{
				nbt.setString("Song"+i, ("audiocassettes"+":"+"empty"));
				nbt.setString("SongName"+i, "--Empty--");
			}
	    }
	    int curslot = AbstractAudioCassetteItem.getCurrentSlot(stack);
	    nbt.setString("Song"+curslot, song);
		nbt.setString("SongName"+curslot, songname);
		nbt.setInteger("ms", curslot);
		nbt.setInteger("max", c.maxslots);
		
		stack.setTagCompound(nbt);
	}
	
	public static SoundEvent getCurrentSong(ItemStack stack) {
		if(stack == null) return ModSoundEvents.EMPTY;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return ModSoundEvents.EMPTY;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
		if(ms < 1 || ms > c.maxslots)
			return ModSoundEvents.EMPTY;
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = stack.getTagCompound();
		return new SoundEvent(new ResourceLocation(nbt.getString("Song"+ms)));
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
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack.hasTagCompound()) {
		    nbt = stack.getTagCompound();
		    return nbt.getInteger("ms");
		}
		return 0;
	}
	
	public int getMaxWriteTime() {
		return this.MaxWriteTime;
	}
	
	public static String getSongTitle(ItemStack stack) {
		if(stack == null) return "--Empty--";
		if(stack.getItem() instanceof AbstractAudioCassetteItem) {
			AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
			int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
			if(ms < 1 || ms > c.maxslots)
				return "--Empty--";
			
			NBTTagCompound nbt = new NBTTagCompound();
			nbt = stack.getTagCompound();
			return nbt.getString("SongName"+ms);
		}
		else return "--Empty--";
	}

	public static void setActiveSlot(int song, ItemStack stack) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		int newsong = 1;
		NBTTagCompound nbt = new NBTTagCompound();
		    if (stack.hasTagCompound()) {
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
			        nbt = stack.getTagCompound();
			    	nbt.setInteger("ms", newsong);
			    	stack.setTagCompound(nbt);
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
		if(c == null || c == ItemStack.EMPTY || !c.hasTagCompound()) return true;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = c.getTagCompound();
		return nbt.getString("Song"+i).equals("audiocassettes:empty");
	}
}