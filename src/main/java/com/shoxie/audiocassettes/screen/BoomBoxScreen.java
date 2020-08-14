package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
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

import net.minecraft.client.Minecraft;
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
        this.tile = (BoomBoxTile) audiocassettes.proxy.getClientPlayer().world.getTileEntity(container.getPos());
    }
	
    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        field_230710_m_.clear();
        func_230480_a_(new Button(guiLeft + 89, guiTop + 42, 26, 18, new TranslationTextComponent("gui.audiocassettes.stopplaybtn"), (button) -> this.Control(2)));
        func_230480_a_(new Button(guiLeft + 56, guiTop + 42, 26, 18, new TranslationTextComponent("gui.audiocassettes.startplaybtn"), (button) -> this.Control(1)));
        func_230480_a_(new Button(guiLeft + 128, guiTop + 16, 18, 18, new TranslationTextComponent(" > "), (button) ->  this.Control(3)));
        func_230480_a_(new Button(guiLeft + 32, guiTop + 16, 18, 18, new TranslationTextComponent(" < "), (button) -> this.Control(4)));
    }
    private void Control(int opt) {
    	if(this.container.getSlot(0).getStack().getItem() instanceof AbstractAudioCassetteItem) 
    	{
    		if(opt==1 && !(audiocassettes.proxy.isBoomBoxPlaying(tile.getID()))) Networking.INSTANCE.sendToServer(new CBoomBoxPlayPacket(this.container.getPos()));
    		if(opt==2 && audiocassettes.proxy.isBoomBoxPlaying(tile.getID())) Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(this.container.getPos()));
    		if(opt==3) Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(this.container.getPos(),true));
    		if(opt==4) Networking.INSTANCE.sendToServer(new BoomBoxPrevSongPacket(this.container.getPos()));
    	} 	
    }

    @Override
    public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.func_230446_a_(p_230430_1_);
        super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.func_230459_a_(p_230430_1_,p_230430_2_, p_230430_3_);
    }

	@Override
	protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(GUI);
        this.func_238474_b_(p_230450_1_,this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
    protected void func_230451_b_(MatrixStack p_230450_1_, int mouseX, int mouseY) {
    	//super.func_230451_b_(p_230450_1_, mouseY, mouseY);
        this.field_230712_o_.func_238405_a_(p_230450_1_,new TranslationTextComponent("gui.audiocassettes.boombox").getString(), this.xSize / 2 - this.field_230712_o_.getStringWidth(I18n.format("gui.audiocassettes.boombox")) / 2, 6, 0xffffff);
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
        drawScaledString(p_230450_1_, Minecraft.getInstance().fontRenderer, (this.container.max>0&&this.container.getSlot(0).getHasStack()? this.container.curr+". "+str: "- "), 69, 22, 0.7F, 0xffffff);
    }
    
    public void drawScaledString(MatrixStack p_230450_1_, FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.func_238476_c_(p_230450_1_, fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
