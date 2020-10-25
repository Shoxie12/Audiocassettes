package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.TapeDeckContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.TapeDeckSetSongPacket;
import com.shoxie.audiocassettes.networking.TapeDeckStartWritingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiTapeDeck extends GuiContainer {

	protected int xSize = 176;
	protected int ySize = 168;
    private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/td.png");
	private TapeDeckContainer container;
    public GuiTapeDeck(TapeDeckContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(9, guiLeft + 58, guiTop+60, 51, 20, new TextComponentTranslation("gui.audiocassettes.startripbtn").getFormattedText()));
        this.buttonList.add(new GuiButton(10, guiLeft + 54, guiTop + 33, 14, 14, " < "));
        this.buttonList.add(new GuiButton(11, guiLeft + 100, guiTop + 33, 14, 14, " > "));
        
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
     this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
    	switch (button.id) {
    	
    	case 9: {
        	ItemStack stack = this.container.getSlot(1).getStack();
        	ItemStack disc = this.container.getSlot(0).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem && disc.getItem() instanceof ItemRecord) {
        		ItemRecord mdi = (ItemRecord) this.container.getSlot(0).getStack().getItem();
	        	if(AbstractAudioCassetteItem.getMaxSlots(stack) > 0 && AbstractAudioCassetteItem.getCurrentSlot(stack)>0)
	        		Networking.INSTANCE.sendToServer(new TapeDeckStartWritingPacket(this.container.getPos(), mdi.getSound().getSoundName(),mdi.getRecordNameLocal()));
	        	}
        	break;
        }
    	
    	case 10: {
        	if(this.container.isWriting()) return;
        	ItemStack stack = this.container.getSlot(1).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
        		int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
        		int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
	        	if(maxsongs> 0 && cursong>1 && cursong<=maxsongs)
	        		Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),--cursong));
        	}
        	break;
        }
    	
    	case 11: {
        	if(this.container.isWriting()) return;
        	ItemStack stack = this.container.getSlot(1).getStack();
        	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
        		int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
        		int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
	        	if(maxsongs> 0 && cursong>0 && cursong<maxsongs)
	        		Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),++cursong));
        	}
        	break;
        	}
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	ItemStack stack = this.container.getSlot(1).getStack();
    	int max = stack.getItem() instanceof AbstractAudioCassetteItem ? AbstractAudioCassetteItem.getMaxSlots(stack) : 0;
    	drawString(Minecraft.getMinecraft().fontRenderer, I18n.format("gui.audiocassettes.tapedeck"), 10, 10, 0xffffff);
    	drawScaledString(Minecraft.getMinecraft().fontRenderer, I18n.format("gui.audiocassettes.selectedtrack")+": "+(max>0? AbstractAudioCassetteItem.getCurrentSlot(stack) : "-"), 101, 13, 0.7F, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = ((TapeDeckContainer)this.container).getWriteTime(24);
        if(this.container.isWriting()) this.drawTexturedModalRect(this.guiLeft + 72, this.guiTop + 32, 176, 14, 24 - i, 17);
        else this.drawTexturedModalRect(this.guiLeft + 72, this.guiTop + 32, 176, 14, 0, 17);
    }
    
    public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
