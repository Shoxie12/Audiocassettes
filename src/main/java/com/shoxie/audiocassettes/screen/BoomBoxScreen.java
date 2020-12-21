package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.CBoomBoxPlayPacket;
import com.shoxie.audiocassettes.networking.BoomBoxPrevSongPacket;
import com.shoxie.audiocassettes.networking.CBoomBoxStopPacket;
import com.shoxie.audiocassettes.networking.BoomBoxNextSongPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BoomBoxScreen extends ContainerScreen<BoomBoxContainer> {
	private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/player.png");
	private int maxtitletick = 500;
	private int lasttrack = 0;
	private int titletick = 0;
	private int tstart = 0;
	private int tend = 0;
	private BoomBoxTile tile;
	
    public BoomBoxScreen(BoomBoxContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tile = (BoomBoxTile) audiocassettes.proxy.getClientWorld().getTileEntity(container.getPos());
    }
	
    @Override
    public void init() {
        super.init();
        
        buttons.clear();
        addButton(new Button(
        		guiLeft + 89, guiTop + 42, 26, 18, I18n.format("gui.audiocassettes.stopplaybtn"), (button) -> this.Control(2)));
        addButton(new Button(
        		guiLeft + 56, guiTop + 42, 26, 18, I18n.format("gui.audiocassettes.startplaybtn"), (button) -> this.Control(1)));
        addButton(new Button(
        		guiLeft + 128, guiTop + 16, 18, 18, new TranslationTextComponent(" > ").getFormattedText(), (button) ->  this.Control(3)));
        addButton(new Button(
        		guiLeft + 32, guiTop + 16, 18, 18, new TranslationTextComponent(" < ").getFormattedText(), (button) -> this.Control(4)));
    }
    private void Control(int opt) {
    	if(this.container.getSlot(0).getStack().getItem() instanceof AbstractAudioCassetteItem) 
    	{
    		if(opt==1 && !(audiocassettes.proxy.isBoomBoxPlaying(tile.getID()))) 
    			Networking.INSTANCE.sendToServer(new CBoomBoxPlayPacket(this.container.getPos()));
    		if(opt==2 && audiocassettes.proxy.isBoomBoxPlaying(tile.getID())) 
    			Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(this.container.getPos()));
    		if(opt==3) 
    			Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(this.container.getPos(),true));
    		if(opt==4) 
    			Networking.INSTANCE.sendToServer(new BoomBoxPrevSongPacket(this.container.getPos()));
    	} 	
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(I18n.format(
        		"gui.audiocassettes.boombox"),
        		this.xSize / 2 - this.font.getStringWidth(I18n.format("gui.audiocassettes.boombox")) / 2,
        		6, 0xffffff);
        int strlen = this.container.title.length()+2;

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

        String str = (" "+this.container.title+" ").substring(tstart < 0 ? 0 : tstart, tend < 0 ? 0 : tstart > tend ? tstart : tend);
        drawScaledString(minecraft.fontRenderer,
        		(this.container.max > 0 && this.container.getSlot(0).getHasStack() ? this.container.curr+". "+str: "- "),
        		69, 22, 0.7F, 0xffffff);
    }
    
    public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
