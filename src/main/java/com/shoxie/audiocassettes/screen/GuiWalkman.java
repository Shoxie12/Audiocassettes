package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.CWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.networking.WalkmanPrevSongPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiWalkman extends GuiContainer {
	private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/player.png");
	private int maxtitletick = 500;
	private int lasttrack = 0;
	private int titletick = 0;
	private int tstart = 0;
	private int tend = 0;
	private WalkmanContainer container;
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;
	
    public GuiWalkman(WalkmanContainer container) {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
        this.container = container;
    }
	
    @Override
    public void initGui() {
        super.initGui();
        
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(11, guiLeft + 89, guiTop + 42, 26, 18, new TextComponentTranslation("gui.audiocassettes.stopplaybtn").getFormattedText()));
        this.buttonList.add(new GuiButton(12, guiLeft + 56, guiTop + 42, 26, 18, new TextComponentTranslation("gui.audiocassettes.startplaybtn").getFormattedText()));
        this.buttonList.add(new GuiButton(13, guiLeft + 128, guiTop + 16, 18, 18, new TextComponentTranslation(" > ").getFormattedText()));
        this.buttonList.add(new GuiButton(14, guiLeft + 32, guiTop + 16, 18, 18, new TextComponentTranslation(" < ").getFormattedText()));

    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
    	switch (button.id) {
    	
	    	case 11: {
	    		this.Control(2); break;
	    		}
	    	
	    	case 12: {
	    		this.Control(1); break;
	    		}
	    	
	    	case 13: {
	    		this.Control(3); break;
	        }
	    	
	    	case 14: {
	    		this.Control(4); break;
	        }
    	}
    }
    
    private void Control(int opt) {
    	ItemStack cassette = this.container.getSlot(0).getStack().copy();
    	if(cassette.getItem() instanceof AbstractAudioCassetteItem) {    
    		boolean isplaying = audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(this.mc.player)));
    		if(opt==1 && !isplaying) 
    			Networking.INSTANCE.sendToServer(new CWalkmanPlayPacket("-"));
    		if(opt==2 && isplaying) 
    			Networking.INSTANCE.sendToServer(new CWalkmanStopPacket());
    		if(opt==3)
    			Networking.INSTANCE.sendToServer(new WalkmanNextSongPacket("-"));

    		if(opt==4)
    			Networking.INSTANCE.sendToServer(new WalkmanPrevSongPacket());


	        this.container.stitle = AbstractAudioCassetteItem.getSongTitle(cassette);
	        this.container.cursong = AbstractAudioCassetteItem.getCurrentSlot(cassette);
	        this.container.maxsongs = AbstractAudioCassetteItem.getMaxSlots(cassette);	
    	}
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
     this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("gui.audiocassettes.walkman"), this.xSize / 2 - this.fontRenderer.getStringWidth(I18n.format("gui.audiocassettes.walkman")) / 2, 6, 0xffffff);
        String str = "-";
        if(this.container.getSlot(0).getHasStack())
        {
        int strlen = this.container.stitle.length()+2;
        

        if(strlen > 12) {
        maxtitletick = (strlen)*50;
        if(lasttrack != strlen) {
    		titletick = maxtitletick;
    		lasttrack = strlen;
        }
        tstart = (strlen - titletick / 50);
        if(tstart > strlen-1 || tstart < 0) {tstart = 0; maxtitletick = (strlen)*50; titletick = maxtitletick;}
        tend = tstart+12;
        if(tend > strlen || tend < 0 || tend < tstart) {tend = strlen;}
        titletick = (titletick < 1 ? maxtitletick : titletick-1);
        if(tstart > tend-4) titletick = maxtitletick;
        
        if(tstart < 0) tstart = 0;
        if(tend < 0) tend = 0;
        }
        else {
        	tstart = 0;
        	tend = strlen;
        }

        str = (" "+this.container.stitle+" ").substring(tstart < 0 ? 0 : tstart, tend < 0 ? 0 : tstart > tend ? tstart : tend);
        }
        drawScaledString(Minecraft.getMinecraft().fontRenderer, (this.container.getSlot(0).getHasStack()? this.container.cursong+". "+str: "- "), 69, 22, 0.7F, 0xffffff);
    }
    
    public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
