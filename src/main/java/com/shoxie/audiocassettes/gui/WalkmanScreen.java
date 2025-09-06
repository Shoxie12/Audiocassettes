package com.shoxie.audiocassettes.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.menu.WalkmanMenu;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.networking.CWalkmanPlayPacket;
import com.shoxie.audiocassettes.networking.WalkmanPrevSongPacket;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;

public class WalkmanScreen extends AbstractContainerScreen<WalkmanMenu> {
	private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/player.png");
	private int maxtitletick = 500;
	private int lasttrack = 0;
	private int titletick = 0;
	private int tstart = 0;
	private int tend = 0;
	private Player player;
    protected int xSize = 176;
    protected int ySize = 182;
    private WalkmanMenu container;

    public WalkmanScreen(WalkmanMenu _container, Inventory inv, Component name) {
        super(_container, inv, name);
        this.container = _container;
        this.player = inv.player;
    }

    @Override
    public void init() {
        super.init();
        clearWidgets();

        //Stop
        addRenderableWidget(
                Button.builder(Component.translatable("gui.audiocassettes.stopplaybtn"),
                        (button) -> this.Control(2)
                ).bounds(getGuiLeft() + 130,getGuiTop() + 40,26,18).build());

        //Start
        addRenderableWidget(
                Button.builder(Component.translatable("gui.audiocassettes.startplaybtn"),
                        (button) -> this.Control(1)
                ).bounds(getGuiLeft() + 20,getGuiTop() + 40,26,18).build());

        //Next
        addRenderableWidget(
                Button.builder(Component.literal(" > "),
                        (button) -> this.Control(3)
                ).bounds(getGuiLeft() + 96,getGuiTop() + 40,18,18).build());

        //Prev
        addRenderableWidget(
                Button.builder(Component.literal(" < "),
                        (button) -> this.Control(4)
                ).bounds(getGuiLeft() + 60,getGuiTop() + 40,18,18).build());

    }
    
    private void Control(int opt) {
    	ItemStack cassette = this.container.getSlot(0).getItem();
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
    	}
    }

    @Override
    public void render(GuiGraphics guigraphics, int X, int Y, float f) {
        super.render(guigraphics, X, Y, f);
        this.renderTooltip(guigraphics, X, Y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.xSize, this.ySize);
    }


	@Override
    protected void renderLabels(GuiGraphics guiGraphics, int X, int Y) {
        guiGraphics.drawString(this.font,Component.translatable("gui.audiocassettes.walkman").getString(),
                (this.xSize - 30) / 2 - Component.translatable("gui.audiocassettes.walkman").getString().length() / 2, 6, 0xffffff);
        String str = "-";

        ItemStack cassette = this.container.getSlot(0).getItem();
        if(!(cassette.getItem() instanceof AbstractAudioCassetteItem)) return;
        int curr = AbstractAudioCassetteItem.getCurrentSlot(cassette);
        String title = AbstractAudioCassetteItem.getSongTitle(cassette);

        if(!this.container.getSlot(0).getItem().isEmpty())
        {
        int strlen = title.length()+2;
        

        if(strlen > 25) {
        maxtitletick = (strlen)*500;
        if(lasttrack != strlen) {
    		titletick = maxtitletick;
    		lasttrack = strlen;
        }
        tstart = (strlen - titletick / 50);
        if(tstart > strlen-1 || tstart < 0) {tstart = 0; maxtitletick = (strlen)*50; titletick = maxtitletick;}
        tend = tstart+25;
        if(tend > strlen || tend < 0 || tend < tstart) {tend = strlen;}
        titletick = (titletick < 1 ? maxtitletick : titletick-1);
        if(tstart > tend-4) titletick = maxtitletick;

        }
        else {
        	tstart = 0;
        	tend = strlen;
        }

        str = (" "+title+" ").substring(tstart, Math.max(tend, 0));
        }
        drawScaledString(guiGraphics,
        		(!this.container.getSlot(0).getItem().isEmpty()? curr+". "+str: "- "),
        		10, 15, 0.7F, 0xffffff);
    }
    
    public void drawScaledString(GuiGraphics guiGraphics, String text, int x, int y, float size, int color) {
        guiGraphics.drawString(this.font,text,Math.round(x / size),Math.round(y / size),color);
    }
}
