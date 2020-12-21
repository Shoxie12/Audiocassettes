package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.networking.CWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.WalkmanPrevSongPacket;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class WalkmanScreen extends ContainerScreen<WalkmanContainer> {
	private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/player.png");
	private int maxtitletick = 500;
	private int lasttrack = 0;
	private int titletick = 0;
	private int tstart = 0;
	private int tend = 0;
	private PlayerEntity player;
	
	
    public WalkmanScreen(WalkmanContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.player = inv.player;
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
        
        ItemStack cassette = this.container.getSlot(0).getStack().copy();
        if(cassette.getItem() instanceof AbstractAudioCassetteItem) {
	        this.container.stitle = AbstractAudioCassetteItem.getSongTitle(cassette);
	        this.container.cursong = AbstractAudioCassetteItem.getCurrentSlot(cassette);
	        this.container.maxsongs = AbstractAudioCassetteItem.getMaxSlots(cassette);
        }
    }
    
    private void Control(int opt) {
    	ItemStack cassette = this.container.getSlot(0).getStack().copy();
    	if(cassette.getItem() instanceof AbstractAudioCassetteItem) {    		
    		if(opt==1 && !(audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(player)))))
    			Networking.INSTANCE.sendToServer(new CWalkmanPlayPacket("-"));
    		if(opt==2 && audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(player))))
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

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(
        		I18n.format("gui.audiocassettes.walkman"),
        		this.xSize / 2 - this.font.getStringWidth(I18n.format("gui.audiocassettes.walkman")) / 2,
        		6, 0xffffff
        		);
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
        drawScaledString(minecraft.fontRenderer,
        		(this.container.getSlot(0).getHasStack()? this.container.cursong+". "+str: "- "),
        		69, 22, 0.7F, 0xffffff
        		);
    }
    
    public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
