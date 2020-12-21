package com.shoxie.audiocassettes.screen;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.text.StringTextComponent;
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
    public void func_231160_c_() {
        super.func_231160_c_();
        field_230710_m_.clear();
        func_230480_a_(new Button(guiLeft + 89, guiTop + 42, 26, 18, new TranslationTextComponent("gui.audiocassettes.stopplaybtn"), (button) -> this.Control(2)));
        func_230480_a_(new Button(guiLeft + 56, guiTop + 42, 26, 18, new TranslationTextComponent("gui.audiocassettes.startplaybtn"), (button) -> this.Control(1)));
        func_230480_a_(new Button(guiLeft + 128, guiTop + 16, 18, 18, new StringTextComponent(" > "), (button) ->  this.Control(3)));
        func_230480_a_(new Button(guiLeft + 32, guiTop + 16, 18, 18, new StringTextComponent(" < "), (button) -> this.Control(4)));
    }
    
    private void Control(int opt) {
    	ItemStack cassette = this.container.getSlot(0).getStack().copy();
    	if(cassette.getItem() instanceof AbstractAudioCassetteItem) {    		
    		boolean isplaying = audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(this.player)));
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
    	this.field_230712_o_.func_238405_a_(p_230450_1_,new TranslationTextComponent("gui.audiocassettes.walkman").getString(),
    			this.xSize / 2 - this.field_230712_o_.getStringWidth(I18n.format("gui.audiocassettes.walkman")) / 2, 6, 0xffffff);
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
        drawScaledString(p_230450_1_,field_230706_i_.fontRenderer,
        		(this.container.getSlot(0).getHasStack()? this.container.cursong+". "+str: "- "),
        		69, 22, 0.7F, 0xffffff);
    }
    
    public void drawScaledString(MatrixStack p_230450_1_, FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.func_238476_c_(p_230450_1_, fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
}
