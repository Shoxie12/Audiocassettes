package com.shoxie.audiocassettes.item;

import java.util.List;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.ModSoundEvents;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractAudioCassetteItem extends Item{
	
	protected String name;
	protected int maxslots;
	protected int MaxWriteTime;
	public AbstractAudioCassetteItem() {
		super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			int song = getCurrentSlot(stack);
			int max = getMaxSlots(stack);
			boolean fdots = (song > 3 ? true : false);
			boolean ldots = (song < max-3 ? true : false);
			int k = (song > 3 ? song < max-3 ? song-3 : max-6 : 1);
			int j = (song > 3 ? song < max-3 ? song+3 : max : 7);
			for(int i=k;i<=j;i++)
				if(stack.getTag().contains("Song"+i))
					if((i==k && fdots) || (i==j && ldots))
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks("..."));
					else {
						String str = stack.getTag().getString("SongName"+i);
						if(str == "--Empty--")
							str = I18n.format("sound.audiocassettes.emptysound");
						tooltip.add(net.minecraftforge.common.ForgeHooks.newChatWithLinks((i==song? "• " : "")+i+". "+str));
					}
		}
	}
	
	public static void appendSongs(ItemStack stack, ResourceLocation res, String songname) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		CompoundNBT nbt = new CompoundNBT();
	    if (stack.hasTag())
	        nbt = stack.getTag();
	    else
	    {
	        nbt = new CompoundNBT();
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
		if(stack == null) return ModSoundEvents.EMPTY;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return ModSoundEvents.EMPTY;
		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
		int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
		if(ms < 1 || ms > c.maxslots)
			return ModSoundEvents.EMPTY;
		
		CompoundNBT nbt = new CompoundNBT();
		nbt = stack.getTag();
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
		CompoundNBT nbt = new CompoundNBT();
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
		if(stack.getItem() instanceof AbstractAudioCassetteItem) {
			AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) stack.getItem();
			int ms = AbstractAudioCassetteItem.getCurrentSlot(stack);
			if(ms < 1 || ms > c.maxslots)
				return "--Empty--";
			
			CompoundNBT nbt = new CompoundNBT();
			nbt = stack.getTag();
			return nbt.getString("SongName"+ms);
		}
		else return "--Empty--";
	}
	
	public static void setActiveSlot(int song, ItemStack stack) {
		if(stack == null) return;
		if(!(stack.getItem() instanceof AbstractAudioCassetteItem)) return;
		int newsong = 1;
		CompoundNBT nbt = new CompoundNBT();
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
		CompoundNBT nbt = new CompoundNBT();
		nbt = c.getTag();
		return nbt.getString("Song"+i).equals("audiocassettes:empty");
	}
}