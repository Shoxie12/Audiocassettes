package com.shoxie.audiocassettes.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import com.shoxie.audiocassettes.menu.BoomBoxMenu;
import com.shoxie.audiocassettes.networking.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;

public class BoomBoxScreen extends AbstractContainerScreen<BoomBoxMenu> {
    private final BoomBoxMenu container;
    private final ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/player.png");
	private int maxtitletick = 500;
	private int lasttrack = 0;
	private int titletick = 0;
	private int tstart = 0;
	private int tend = 0;
	private final BoomBoxEntity entity;
    protected int xSize = 176;
    protected int ySize = 182;

    public BoomBoxScreen(BoomBoxMenu _container, Inventory inv, Component name) {
        super(_container, inv, name);
        this.container = _container;
        this.entity = container.getEntity();
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
    	if(this.container.getSlot(0).getItem().getItem() instanceof AbstractAudioCassetteItem)
    	{
    		if(opt==1 && !(audiocassettes.proxy.isBoomBoxPlaying(entity.getID()))) 
    			Networking.INSTANCE.sendToServer(new CBoomBoxPlayPacket(this.entity.getBlockPos()));
    		if(opt==2 && audiocassettes.proxy.isBoomBoxPlaying(entity.getID())) 
    			Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(this.entity.getBlockPos()));
    		if(opt==3) {
        		titletick = maxtitletick;
    			Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(this.entity.getBlockPos(),true));
    		}
    		if(opt==4) {
        		titletick = maxtitletick;
    			Networking.INSTANCE.sendToServer(new BoomBoxPrevSongPacket(this.entity.getBlockPos()));
    		}
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
        ItemStack cassette = this.container.getSlot(0).getItem();
        if(!(cassette.getItem() instanceof AbstractAudioCassetteItem)) return;
        int max = AbstractAudioCassetteItem.getMaxSlots(cassette);
        int curr = AbstractAudioCassetteItem.getCurrentSlot(cassette);
        String title = AbstractAudioCassetteItem.getSongTitle(cassette);
        guiGraphics.drawString(this.font,
        		Component.translatable("gui.audiocassettes.boombox").getString(),
                (this.xSize - 30) / 2 - Component.translatable("gui.audiocassettes.boombox").getString().length() / 2,
        		6, 0xffffff);
        int strlen = title.length()+2;

        if(strlen > 25) {
        maxtitletick = (strlen)*50;
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

        String str = (" "+title+" ").substring(tstart, Math.max(tend, 0));
        drawScaledString(guiGraphics,
        		(max>0&&!this.container.getSlot(0).getItem().isEmpty()? curr+". "+str: "- "),
                10, 15, 0.7F, 0xffffff);
    }

    public void drawScaledString(GuiGraphics guiGraphics, String text, int x, int y, float size, int color) {
        guiGraphics.drawString(this.font,text,Math.round(x / size),Math.round(y / size),color);
    }
}
