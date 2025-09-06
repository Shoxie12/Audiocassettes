package com.shoxie.audiocassettes.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.menu.TapeDeckMenu;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.TapeDeckSetSongPacket;
import com.shoxie.audiocassettes.networking.TapeDeckStartWritingPacket;
import com.shoxie.audiocassettes.networking.TapeDeckStopWritePacket;


public class TapeDeckScreen extends AbstractContainerScreen<TapeDeckMenu> {

    private final TapeDeckMenu container;
    private ResourceLocation GUI = new ResourceLocation(audiocassettes.MODID, "textures/gui/td.png");
    private int last;

    public TapeDeckScreen(TapeDeckMenu _container, Inventory inv, Component name) {
        super(_container, inv, name);
        this.container = _container;
    }
    protected int xSize = 176;
    protected int ySize = 182;


    @Override
    public void init() {
        super.init();
        clearWidgets();

        //Start
        addRenderableWidget(
                Button.builder(Component.translatable("gui.audiocassettes.startripbtn"),
                        (button) ->  {
                            ItemStack stack = this.container.getSlot(1).getItem();
                            ItemStack disc = this.container.getSlot(0).getItem();
                            if(stack.getItem() instanceof AbstractAudioCassetteItem && disc.getItem() instanceof RecordItem) {
                                RecordItem mdi = (RecordItem) this.container.getSlot(0).getItem().getItem();
                                if(stack.getItem() instanceof AbstractAudioCassetteItem) {
                                    if(AbstractAudioCassetteItem.getMaxSlots(stack) > 0 && AbstractAudioCassetteItem.getCurrentSlot(stack)>0)
                                        Networking.INSTANCE.sendToServer(new TapeDeckStartWritingPacket(this.container.getPos(), mdi.getSound().getLocation(),mdi.getDisplayName().getString(), false));
                                }
                            }
                        }
                ).bounds(getGuiLeft() + 16,getGuiTop() + 55,44,20).build());

        //Prev
        addRenderableWidget(
                Button.builder(Component.literal("<"),
                        (button) ->  {
                            if(this.container.isWriting()) return;
                            ItemStack stack = this.container.getSlot(1).getItem();
                            if(stack.getItem() instanceof AbstractAudioCassetteItem) {
                                int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
                                int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
                                if(maxsongs> 0 && cursong>1 && cursong<=maxsongs)
                                    Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),--cursong));
                            }
                        }
                ).bounds(getGuiLeft() + 54,getGuiTop() + 33,14,14).build());

        //Next
        addRenderableWidget(
                Button.builder(Component.literal(">"),
                        (button) ->  {
                            if(this.container.isWriting()) return;
                            ItemStack stack = this.container.getSlot(1).getItem();
                            if(stack.getItem() instanceof AbstractAudioCassetteItem) {
                                int cursong = AbstractAudioCassetteItem.getCurrentSlot(stack);
                                int maxsongs = AbstractAudioCassetteItem.getMaxSlots(stack);
                                if(maxsongs> 0 && cursong>0 && cursong<maxsongs)
                                    Networking.INSTANCE.sendToServer(new TapeDeckSetSongPacket(this.container.getPos(),++cursong));
                            }
                        }
                ).bounds(getGuiLeft() + 100,getGuiTop() + 33,14,14).build());

        //Erase
        addRenderableWidget(
                Button.builder(Component.translatable("gui.audiocassettes.erasewrbtn"),
                        (button) ->  {
                            if(this.container.isWriting()) {
                                Networking.INSTANCE.sendToServer(new TapeDeckStopWritePacket(this.container.getPos()));
                                return;
                            }
                            ItemStack stack = this.container.getSlot(1).getItem();
                            if(stack.getItem() instanceof AbstractAudioCassetteItem) {
                                if(AbstractAudioCassetteItem.getMaxSlots(stack) > 0 && AbstractAudioCassetteItem.getCurrentSlot(stack)>0)
                                    Networking.INSTANCE.sendToServer(new TapeDeckStartWritingPacket(this.container.getPos(),
                                            new ResourceLocation("audiocassettes"+":"+"empty"),"--Empty--", true)
                                    );
                            }
                        }
                ).bounds(getGuiLeft() + 111,getGuiTop() + 55,47,20).build());

    }
    
    @Override
    public void render(GuiGraphics guigraphics, int X, int Y, float f) {
        //this.func_230446_a_(p_230430_1_);
        super.render(guigraphics, X, Y, f);
        this.renderTooltip(guigraphics, X, Y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int X, int Y) {
    	ItemStack stack = this.container.getSlot(1).getItem();
    	int max = stack.getItem() instanceof AbstractAudioCassetteItem ? AbstractAudioCassetteItem.getMaxSlots(stack) : 0;
        guiGraphics.drawString(this.font,Component.translatable("gui.audiocassettes.tapedeck"), 10, 10, 0xffffff);
    	drawScaledString(guiGraphics,
                Component.translatable("gui.audiocassettes.selectedtrack").getString()+": "+(max>0? AbstractAudioCassetteItem.getCurrentSlot(stack) : "-"),
    			55, 7, 0.7F, 0xffffff);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.xSize, this.ySize);

        int it = this.container.getWriteTime(24);
        if(this.container.isWriting()) {
            if (it > last) it = last;
            else last = it;
        } else { last = 99; }
        if(this.container.isWriting()) guiGraphics.blit(GUI, this.getGuiLeft() + 72, this.getGuiTop() + 32, 176, 14, 24 - it, 17);
		else guiGraphics.blit(GUI, this.getGuiLeft() + 72, this.getGuiTop() + 32, 176, 14, 0, 17);
    }

    public void drawScaledString(GuiGraphics guiGraphics, String text, int x, int y, float size, int color) {
        guiGraphics.drawString(this.font,text,Math.round(x / size),Math.round(y / size),color);
    }
}
