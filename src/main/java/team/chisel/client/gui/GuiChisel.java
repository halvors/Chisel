package team.chisel.client.gui;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextProperties;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;
import team.chisel.Chisel;
import team.chisel.api.IChiselItem;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.IChiselMode;
import team.chisel.common.inventory.ChiselContainer;
import team.chisel.common.item.PacketChiselMode;
import team.chisel.common.util.NBTUtil;

@ParametersAreNonnullByDefault
public class GuiChisel<T extends ChiselContainer> extends ContainerScreen<T> {

    public PlayerEntity player;

    public GuiChisel(T container, PlayerInventory iinventory, ITextComponent displayName) {
        super(container, iinventory, displayName);
        player = iinventory.player;
        xSize = 252;
        ySize = 202;
    }

    @Override
    public void func_231175_as__() {
        super.func_231175_as__();
        this.getContainer().onContainerClosed(player);
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
        super.func_231044_a_(mouseX, mouseY, mouseButton);
        
        // if this is a selection slot, no double clicking
        //Slot slot = getSlotAtPosition(mouseX, mouseY);
        //if (slot != null && slot.slotNumber < this.getContainer().getInventoryChisel().size - 1) {
        //    this.doubleClick = false;
        //}

        return false;
    }


    @Override
    public void func_231160_c_() {
        super.func_231160_c_();

        int id = 0;
        Rectangle2d area = getModeButtonArea();
        int buttonsPerRow = area.getWidth() / 20;
        int padding = (area.getWidth() - (buttonsPerRow * 20)) / buttonsPerRow;
        IChiselMode currentMode = NBTUtil.getChiselMode(this.getContainer().getChisel());
        for (IChiselMode mode : CarvingUtils.getModeRegistry().getAllModes()) {
            if (((IChiselItem) this.getContainer().getChisel().getItem()).supportsMode(player, this.getContainer().getChisel(), mode)) {
                int x = area.getX() + (padding / 2) + ((id % buttonsPerRow) * (20 + padding));
                int y = area.getY() + ((id / buttonsPerRow) * (20 + padding));
                ButtonChiselMode button = new ButtonChiselMode(x, y, mode, b -> {
                    b.field_230693_o_ = false;
                    IChiselMode m = ((ButtonChiselMode) b).getMode();
                    NBTUtil.setChiselMode(this.getContainer().getChisel(), m);
                    Chisel.network.sendToServer(new PacketChiselMode(this.getContainer().getChiselSlot(), m));
                    for (Widget other : field_230710_m_) {
                        if (other != b && other instanceof ButtonChiselMode) {
                            // TODO see if Button.enabled == Button.active
                            other.field_230693_o_ = true;
                        }
                    }
                });
                if (mode == currentMode) {
                    // TODO see if Button.enabled == Button.active
                    button.field_230693_o_ = false;
                }
                func_230480_a_(button);
                id++;
            }
        }
    }

    protected Rectangle2d getModeButtonArea() {
        int down = 73;
        int padding = 7;
        return new Rectangle2d(guiLeft + padding, guiTop + down + padding, 50, ySize - down - (padding * 2));
    }

    @Override
    public void func_230430_a_(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(mStack);
        super.func_230430_a_(mStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(mStack, mouseX, mouseY);
    }
    
    @Override
    protected void func_230451_b_(MatrixStack mStack, int j, int i) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // TODO fix String
        String line = field_230704_d_.getString();
        List<ITextProperties> lines = field_230712_o_.func_238425_b_(ITextProperties.func_240652_a_(line), 40);
        int y = 60;
        for (ITextProperties s : lines) {
            field_230712_o_.func_238421_b_(mStack, s.getString(), 32 - field_230712_o_.getStringWidth(s.getString()) / 2, y, 0x404040);
            y += 10;
        }

        drawButtonTooltips(mStack, j, i);
//        if (showMode()) {
//            line = I18n.format(this.container.inventory.getInventoryName() + ".mode");
//            fontRendererObj.drawString(line, fontRendererObj.getStringWidth(line) / 2 + 6, 85, 0x404040);
//        }
    }
    
    protected void drawButtonTooltips(MatrixStack mStack, int mx, int my) {
        for (Widget button : field_230710_m_) {
            if (button.func_231047_b_(mx, my) && button instanceof ButtonChiselMode) {
                String unloc = ((ButtonChiselMode)button).getMode().getUnlocName();
                List<ITextProperties> ttLines = Lists.newArrayList(
                        ITextProperties.func_240652_a_(I18n.format(unloc)),
                        ITextProperties.func_240652_a_(TextFormatting.GRAY + I18n.format(unloc + ".desc"))
                );
                GuiUtils.drawHoveringText(mStack, ttLines, mx - guiLeft, my - guiTop, field_230708_k_ - guiLeft, field_230709_l_ - guiTop, -1, field_230712_o_);
            }
        }
    }

    @Override
    protected void func_230450_a_(MatrixStack mStack, float f, int mx, int my) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = field_230708_k_ - xSize >> 1;
        int j = field_230709_l_ - ySize >> 1;

        String texture = "chisel:textures/chisel2gui.png";

        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(texture));
        func_238474_b_(mStack, i, j, 0, 0, xSize, ySize);

        int x = (field_230708_k_ - xSize) / 2;
        int y = (field_230709_l_ - ySize) / 2;

        Slot main = (Slot) this.getContainer().inventorySlots.get(this.getContainer().getInventoryChisel().size);
        if (main.getStack().isEmpty()) {
            drawSlotOverlay(mStack, this, x + 14, y + 14, main, 0, ySize, 60);
        }
    }

    /*@Override TODO can't override, now private
    protected void func_238746_a_(MatrixStack mStack, Slot slot) {
        if (slot instanceof SlotChiselInput) {
            mStack.push();
            mStack.scale(2, 2, 2);
            slot.xPos -= 16;
            slot.yPos -= 16;
            super.func_238746_a_(mStack, slot);
            slot.xPos += 16;
            slot.yPos += 16;
            RenderSystem.popMatrix();
        } else {
            super.func_238746_a_(mStack, slot);
        }
    }*/

    public static void drawSlotOverlay(MatrixStack mStack, ContainerScreen<?> gui, int x, int y, Slot slot, int u, int v, int padding) {
        padding /= 2;
        gui.func_238474_b_(mStack, x + (slot.xPos - padding), y + (slot.yPos - padding), u, v, 18 + padding, 18 + padding);
    }
}