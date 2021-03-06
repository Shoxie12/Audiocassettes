package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.TapeDeckContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.TapeDeckSetSongPacket;
import com.shoxie.audiocassettes.networking.TapeDeckStartWritingPacket;
import com.shoxie.audiocassettes.networking.TapeDeckStopWritePacket;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TapeDeckScreen extends ContainerScreen<TapeDeckContainer> {

    private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/td.png");
    public TapeDeckScreen(TapeDeckContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        field_230710_m_.clear();
        func_230480_a_(new Button(guiLeft + 16, guiTop + 55, 44, 20, new TranslationTextComponent("gui.audiocassettes.startripbtn"), (button) -> {
        	ItemStack stack = this.container.getSlot(1).getStack();
        	ItemStack disc = this.container.getSlot(0).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem && disc.getItem() instanceof MusicDiscItem) {
	        	MusicDiscItem mdi = (MusicDiscItem) this.container.getSlot(0).getStack().getItem();
	        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
		        	if(AbstractAudioCassetteItem.getMaxSlots(stack) > 0 && AbstractAudioCassetteItem.getCurrentSlot(stack)>0)
		        		Networking.INSTANCE.sendToServer(new TapeDeckStartWritingPacket(this.container.getPos(), mdi.getSound().getName(),mdi.func_234801_g_().getString(), false));
	        	}
        	}
        }));
        
        func_230480_a_(new Button(guiLeft + 54, guiTop + 33, 14, 14, new TranslationTextComponent(" < "), (button) -> {
        	if(this.container.isWriting()) return;
        	ItemStack stack = this.container.getSlot(1).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
        		int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
        		int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
	        	if(maxsongs> 0 && cursong>1 && cursong<=maxsongs)
	        		Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),--cursong));
        	}
        }));
        
        func_230480_a_(new Button(guiLeft + 100, guiTop + 33, 14, 14, new TranslationTextComponent(" > "), (button) -> {
        	if(this.container.isWriting()) return;
        	ItemStack stack = this.container.getSlot(1).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
        		int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
        		int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
	        	if(maxsongs> 0 && cursong>0 && cursong<maxsongs)
	        		Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),++cursong));
        	}
        }));
        
        func_230480_a_(new Button(guiLeft + 111, guiTop + 55, 47, 20, new TranslationTextComponent("gui.audiocassettes.erasewrbtn"), (button) -> {
        	if(this.container.isWriting()) {
        		Networking.INSTANCE.sendToServer(new TapeDeckStopWritePacket(this.container.getPos()));
        		return;
        	}
        	ItemStack stack = this.container.getSlot(1).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
	        	if(AbstractAudioCassetteItem.getMaxSlots(stack) > 0 && AbstractAudioCassetteItem.getCurrentSlot(stack)>0)
	        		Networking.INSTANCE.sendToServer(new TapeDeckStartWritingPacket(this.container.getPos(),
	        				new ResourceLocation("audiocassettes"+":"+"empty"),"--Empty--", true)
	        				);
	        	}
        }));
    }
    
    @Override
    public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.func_230446_a_(p_230430_1_);
        super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.func_230459_a_(p_230430_1_,p_230430_2_, p_230430_3_);
    }

	@Override
    protected void func_230451_b_(MatrixStack p_230450_1_, int mouseX, int mouseY) {
    	ItemStack stack = this.container.getSlot(1).getStack();
    	int max = stack.getItem() instanceof AbstractAudioCassetteItem ? AbstractAudioCassetteItem.getMaxSlots(stack) : 0;
    	func_238476_c_(p_230450_1_,field_230706_i_.fontRenderer, I18n.format("gui.audiocassettes.tapedeck"), 10, 10, 0xffffff);
    	drawScaledString(p_230450_1_, field_230706_i_.fontRenderer, 
    			I18n.format("gui.audiocassettes.selectedtrack")+": "+(max>0? AbstractAudioCassetteItem.getCurrentSlot(stack) : "-"),
    			101, 13, 0.7F, 0xffffff);
    }

    @Override
    protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(GUI);
        this.func_238474_b_(p_230450_1_, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = ((TapeDeckContainer)this.container).getWriteTime(24);
        if(this.container.isWriting()) this.func_238474_b_(p_230450_1_, this.guiLeft + 72, this.guiTop + 32, 176, 14, 24 - i, 17);
        else this.func_238474_b_(p_230450_1_, this.guiLeft + 72, this.guiTop + 32, 176, 14, 0, 17);
    }
    
    public void drawScaledString(MatrixStack p_230450_1_, FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.func_238476_c_(p_230450_1_, fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
