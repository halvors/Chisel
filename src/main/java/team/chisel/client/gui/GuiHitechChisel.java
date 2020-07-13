package team.chisel.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import team.chisel.Chisel;
import team.chisel.api.IChiselItem;
import team.chisel.client.util.ChiselLangKeys;
import team.chisel.common.inventory.ContainerChiselHitech;
import team.chisel.common.util.NBTUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public class GuiHitechChisel extends GuiChisel<ContainerChiselHitech> {

    private class PreviewModeButton extends Button {

        @Getter
        private PreviewType type;
        
        public PreviewModeButton(int x, int y, int widthIn, int heightIn) {
            super(x, y, widthIn, heightIn, ITextComponent.func_241827_a_(""), b -> {});
            setType(PreviewType.values()[0]);
        }

        @Override
        public boolean func_231044_a_(double mouseX, double mouseY, int i) {
            if (super.func_231044_a_(mouseX, mouseY, i)) {
                setType(PreviewType.values()[(type.ordinal() + 1) % PreviewType.values().length]);
                updateChiselData();
                return true;
            }
            return false;
        }
        
        private final void setType(PreviewType type) {
            this.type = type;
            this.func_238482_a_(ITextComponent.func_241827_a_("< " + type.getLocalizedName().getString() + " >"));
            GuiHitechChisel.this.fakeworld = new FakeBlockAccess(GuiHitechChisel.this); // Invalidate region cache data
        }
    }
    
    private class RotateButton extends Button {

        @Getter
        @Accessors(fluent = true)
        private boolean rotate = true;
        
        public RotateButton(int x, int y) {
            super(x, y, 16, 16, ITextComponent.func_241827_a_(""), b -> {});
        }

        @Override
        public void func_230431_b_(MatrixStack mStack, int mouseX, int mouseY, float partialTick) {
            this.field_230692_n_ = mouseX >= this.field_230690_l_ && mouseY >= this.field_230691_m_ && mouseX < field_230690_l_ + field_230688_j_ && mouseY < field_230691_m_ + field_230689_k_;

            GuiHitechChisel.this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            float a = func_231047_b_(mouseX, mouseY) ? 1 : 0.2f;
            int u = rotate ? 0 : 16;
            int v = 238;

            RenderSystem.color4f(1, 1, 1, a);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            this.func_230926_e_(1000);
            func_238474_b_(mStack, field_230690_l_, field_230691_m_, u, v, 16, 16);
            this.func_230926_e_(0);
            RenderSystem.color4f(1, 1, 1, 1);
        }

        @Override
        public boolean func_231044_a_(double mouseX, double mouseY, int i) {
            if (super.func_231044_a_(mouseX, mouseY, i)) {
                rotate = !rotate;
                updateChiselData();
                return true;
            }
            return false;
        }
    }

    @RequiredArgsConstructor
    private static class FakeBlockAccess implements IBlockDisplayReader {

        private final GuiHitechChisel gui;

        @Setter
        private BlockState state = Blocks.AIR.getDefaultState();

        private final WorldLightManager light = new WorldLightManager(new IChunkLightProvider() {

            @Override
            public IBlockReader getWorld() {
                return FakeBlockAccess.this;
            }

            @Override
            @Nullable
            public IBlockReader getChunkForLight(int p_217202_1_, int p_217202_2_) {
                return FakeBlockAccess.this;
            }
        }, true, true);

        @Override
        public @Nullable TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        //@Override
        //public int getCombinedLight(BlockPos pos, int lightValue) {
        //    return 0xF000F0;
        //}

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return gui.buttonPreview.getType().getPositions().contains(pos) ? state : Blocks.AIR.getDefaultState();
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return Fluids.EMPTY.getDefaultState();
        }

        @Override
        public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
            return 0; //TODO ?
        }

        @Override
        public WorldLightManager getLightManager() {
            return light;
        }

        @Override
        public int getBlockColor(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
            return -1;
        }

        @Override
        public int getLightValue(BlockPos p_217298_1_) {
            return 15;
        }

//        @Override
//        public boolean isAirBlock(BlockPos pos) {
//            return getBlockState(pos).getBlock() == Blocks.AIR;
//        }

//        @Override
//        public Biome getBiome(BlockPos pos) {
//            return Biomes.PLAINS;
//        }

//        // @Override 1.9 only. Not actually ever called, just here for compilation.
//        public boolean extendedLevelsInChunkCache() {
//            return false;
//        }

//        @Override
//        public int getStrongPower(BlockPos pos, Direction direction) {
//            return 0;
//        }

//        @Override
//        public WorldType getWorldType() {
//            return WorldType.DEFAULT;
//        }

//        @Override
//        public boolean isSideSolid(BlockPos pos, Direction side, boolean _default) {
//            return getBlockState(pos).isSideSolid(this, pos, side);
//        }
    }

    private static final Rectangle2d panel = new Rectangle2d(8, 14, 74, 74);

    private static final ResourceLocation TEXTURE = new ResourceLocation("chisel", "textures/chiselguihitech.png");

    private final ContainerChiselHitech containerHitech;

    private FakeBlockAccess fakeworld = new FakeBlockAccess(this);

    private boolean panelClicked;
    private int clickButton;
    private long lastDragTime;
    private int clickX, clickY;
    private double initRotX, initRotY, initZoom;
    private double prevRotX, prevRotY;
    private double momentumX, momentumY;
    private float momentumDampening = 0.98f;
    private double rotX = 165, rotY, zoom = 1;

    private int scrollAcc;

    private @Nullable PreviewModeButton buttonPreview;
    private @Nullable Button buttonChisel;
    private @Nullable RotateButton buttonRotate;

    private @Nullable BlockState erroredState;

    public GuiHitechChisel(ContainerChiselHitech container, PlayerInventory iinventory, ITextComponent displayName) {
        super(container, iinventory, displayName);
        containerHitech = container;
        xSize = 256;
        ySize = 220;
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        int x = guiLeft + panel.getX() - 1;
        int y = guiTop + panel.getY() + panel.getHeight() + 3;
        int w = 76, h = 20;

        boolean firstInit = buttonPreview == null;

        func_230480_a_(buttonPreview = new PreviewModeButton(x, y, w, h));

        func_230480_a_(buttonChisel = new Button(x, y += h + 2, w, h, ITextComponent.func_241827_a_(ChiselLangKeys.BUTTON_CHISEL.getLocalizedText()), b -> {
            Slot target = containerHitech.getTarget();
            Slot selected = containerHitech.getSelection();
            if (target != null && target.getHasStack() && selected != null && selected.getHasStack()) {
                if (ItemStack.areItemsEqual(target.getStack(), selected.getStack())) {
                    return;
                }
                ItemStack converted = target.getStack().copy();
                converted.setCount(selected.getStack().getCount());
                int[] slots = new int[] { selected.getSlotIndex() };
                if (func_231173_s_()) {
                    slots = ArrayUtils.addAll(slots, containerHitech.getSelectionDuplicates().stream().mapToInt(Slot::getSlotIndex).toArray());
                }

                Chisel.network.sendToServer(new PacketChiselButton(slots));

                PacketChiselButton.chiselAll(player, slots);

                if (!func_231173_s_()) {
                    List<Slot> dupes = containerHitech.getSelectionDuplicates();
                    Slot next = selected;
                    for (Slot s : dupes) {
                        if (s.slotNumber > selected.slotNumber) {
                            next = s;
                            break;
                        }
                    }
                    if (next == selected && dupes.size() > 0) {
                        next = dupes.get(0);
                    }
                    containerHitech.setSelection(next);
                } else {
                    containerHitech.setSelection(selected); // Force duplicate recalc
                }
            }
        }));
        func_230480_a_(buttonRotate = new RotateButton(guiLeft + panel.getX() + panel.getWidth() - 16, guiTop + panel.getY() + panel.getHeight() - 16));

        ItemStack chisel = containerHitech.getChisel();

        if (firstInit) {
            buttonPreview.setType(NBTUtil.getHitechType(chisel));
            buttonRotate.rotate = NBTUtil.getHitechRotate(chisel);
        }

        func_231023_e_();
    }

    @Override
    protected Rectangle2d getModeButtonArea() {
        int down = 133;
        int padding = 7;
        return new Rectangle2d(guiLeft + padding, guiTop + down + padding, 76, ySize - down - (padding * 2));
    }

    @Override
    public void func_231023_e_() {
        super.func_231023_e_();

        buttonChisel.field_230693_o_ = containerHitech.getSelection() != null && containerHitech.getSelection().getHasStack() && containerHitech.getTarget() != null && containerHitech.getTarget().getHasStack();

        if (!panelClicked) {
            initRotX = rotX;
            initRotY = rotY;
            initZoom = zoom;
        }

        if (func_231173_s_()) {
            buttonChisel.func_238482_a_(ChiselLangKeys.BUTTON_CHISEL_ALL.getComponent().func_240701_a_(TextFormatting.YELLOW));
        } else {
            buttonChisel.func_238482_a_(ITextComponent.func_241827_a_(ChiselLangKeys.BUTTON_CHISEL.getLocalizedText()));
        }
    }

    private void updateChiselData() {
        ItemStack stack = containerHitech.getChisel();
        if (!(stack.getItem() instanceof IChiselItem)) {
            return;
        }

        NBTUtil.setHitechType(stack, buttonPreview.getType().ordinal());
        NBTUtil.setHitechRotate(stack, buttonRotate.rotate());

        Chisel.network.sendToServer(new PacketHitechSettings(containerHitech.getChisel(), containerHitech.getChiselSlot()));
    }

    @Override
    protected void func_230450_a_(MatrixStack mStack, float f, int mx, int my) {
        RenderSystem.color4f(1, 1, 1, 1);

        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        func_238474_b_(mStack, guiLeft, guiTop, 0, 0, xSize, ySize);

        if (containerHitech.getSelection() != null) {
            Slot sel = containerHitech.getSelection();
            if (sel.getHasStack()) {
                drawSlotHighlight(mStack, sel, 0);
                for (Slot s : containerHitech.getSelectionDuplicates()) {
                    drawSlotHighlight(mStack, s, func_231173_s_() ? 0 : 18);
                }
            }
        }
        if (containerHitech.getTarget() != null && !containerHitech.getTarget().getStack().isEmpty()) {
            drawSlotHighlight(mStack, containerHitech.getTarget(), 36);
        }

        if (buttonRotate.rotate() && momentumX == 0 && momentumY == 0 && !panelClicked && System.currentTimeMillis() - lastDragTime > 2000) {
            rotY = initRotY + (f * 2);
        }

        if (panelClicked && clickButton == 0) {
            momentumX = rotX - prevRotX;
            momentumY = rotY - prevRotY;
            prevRotX = rotX;
            prevRotY = rotY;
        }

//        scrollAcc += Mouse.getDWheel();
//        if (Math.abs(scrollAcc) >= 120) {
//            int idx = -1;
//            if (containerHitech.getTarget() != null) {
//                idx = containerHitech.getTarget().getSlotIndex();
//            }
//            while (Math.abs(scrollAcc) >= 120) {
//                if (scrollAcc > 0) {
//                    idx--;
//                    scrollAcc -= 120;
//                } else {
//                    idx++;
//                    scrollAcc += 120;
//                }
//            }
//            if (idx < 0) {
//                for (int i = containerHitech.getInventoryChisel().size - 1; i >= 0; i--) {
//                    if (containerHitech.getSlot(i).getHasStack()) {
//                        idx = i;
//                        break;
//                    }
//                    if (i == 0) {
//                        idx = 0;
//                    }
//                }
//            } else if (idx >= containerHitech.getInventoryChisel().size || !containerHitech.getSlot(idx).getHasStack()) {
//                idx = 0;
//            }
//
//            containerHitech.setTarget(containerHitech.getSlot(idx));
//        }

        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();
        if (containerHitech.getTarget() != null) {

            ItemStack stack = containerHitech.getTarget().getStack();

            if (!stack.isEmpty()) {
                RenderSystem.pushMatrix();

                RenderSystem.translatef(panel.getX() + (panel.getWidth() / 2), panel.getY() + (panel.getHeight() / 2), 100);

                RenderSystem.matrixMode(GL11.GL_PROJECTION);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                int scale = (int) getMinecraft().getMainWindow().getGuiScaleFactor();
                RenderSystem.multMatrix(Matrix4f.perspective(60, (float) panel.getWidth() / panel.getHeight(), 0.01F, 4000));
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
                RenderSystem.translated(-panel.getX() - panel.getWidth() / 2, -panel.getY() - panel.getHeight() / 2, 0);
                RenderSystem.viewport((guiLeft + panel.getX()) * scale, getMinecraft().getMainWindow().getHeight() - (guiTop + panel.getY() + panel.getHeight()) * scale, panel.getWidth() * scale, panel.getHeight() * scale);
                RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);

                // Makes zooming slower as zoom increases, but leaves 1 as the default zoom.
                double sc = 300 + 8 * buttonPreview.getType().getScale() * (Math.sqrt(zoom + 99) - 9);
                RenderSystem.scaled(-sc, -sc, sc);

                RenderSystem.rotatef((float) -rotX, 1, 0, 0);
                RenderSystem.rotatef((float) rotY, 0, 1, 0);
                RenderSystem.translated(-1.5, -2.5, -0.5);

                RenderSystem.enableDepthTest();

                Block block = Block.getBlockFromItem(stack.getItem());
                BlockState state = block.getDefaultState();

                if (state != null && state != erroredState) {
                    erroredState = null;

                    fakeworld.setState(state);

                    getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                    Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    try {
                        MatrixStack ms = new MatrixStack();
                        for (BlockPos pos : buttonPreview.getType().getPositions()) {
                            ms.push();
                            ms.translate(pos.getX(), pos.getY(), pos.getZ());
                            brd.renderModel(state, pos, fakeworld, ms, Tessellator.getInstance().getBuffer(), true, new Random());
                            ms.pop();
                        }
                    } catch (Exception e) {
                        erroredState = state;
                        Chisel.logger.error("Exception rendering block {}", state, e);
                    } finally {
                        if (erroredState == null) {
                            Tessellator.getInstance().draw();
                        } else {
                            Tessellator.getInstance().getBuffer().finishDrawing();
                        }
                    }
                }

                RenderSystem.popMatrix();
                RenderSystem.matrixMode(GL11.GL_PROJECTION);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
                RenderSystem.viewport(0, 0, getMinecraft().getMainWindow().getWidth(), getMinecraft().getMainWindow().getHeight());
            }
        }
    }

    private void drawSlotHighlight(MatrixStack mStack, Slot slot, int u) {
        func_238474_b_(mStack, guiLeft + slot.xPos - 1, guiTop + slot.yPos - 1, u, 220, 18, 18);
    }
    
    private boolean doMomentum = false;
    
    @Override
    protected void func_230451_b_(MatrixStack mStack, int j, int i) {
        if (doMomentum) {
            rotX += momentumX;
            rotX = MathHelper.clamp(rotX, 90, 270);
            rotY += momentumY;
            momentumX *= momentumDampening;
            momentumY *= momentumDampening;
            if (Math.abs(momentumX) < 0.2) {
                if (Math.abs(momentumX) < 0.05) {
                    momentumX = 0;
                } else {
                    momentumX *= momentumDampening * momentumDampening;
                }
            }
            if (Math.abs(momentumY) < 0.2) {
                if (Math.abs(momentumY) < 0.05) {
                    momentumY = 0;
                } else {
                    momentumY *= momentumDampening * momentumDampening;
                }
            }
        }

        String s = ChiselLangKeys.PREVIEW.getLocalizedText();
        field_230712_o_.func_238421_b_(mStack, s, panel.getX() + (panel.getWidth() / 2) - (field_230712_o_.getStringWidth(s) / 2), panel.getY() - 9, 0x404040);
        RenderSystem.disableAlphaTest();
        
        drawButtonTooltips(mStack, j, i);
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
        boolean ret = super.func_231044_a_(mouseX, mouseY, mouseButton);
        if (!ret && !buttonRotate.func_231047_b_(mouseX, mouseY) && panel.contains((int) (mouseX - guiLeft), (int) (mouseY - guiTop))) {
            if (mouseButton == 0) {
                doMomentum = false;
            }
            clickButton = mouseButton;
            panelClicked = true;
            clickX = (int) mouseX;
            clickY = (int) mouseY;
            return true;
        }
        return ret;
    }

    @Override
    public boolean func_231045_a_(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (panelClicked) {
            if (clickButton == 0) {
                rotX = MathHelper.clamp(initRotX + mouseY - clickY, 90, 270);
                rotY = initRotY + mouseX - clickX;
            }
            else if (clickButton == 1) {
                zoom = Math.max(1, initZoom + (clickY - mouseY));
            }
        }
        
        return super.func_231045_a_(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public boolean func_231048_c_(double mouseX, double mouseY, int state) {
        if (panelClicked) {
            lastDragTime = System.currentTimeMillis();
        }
        doMomentum = true;
        panelClicked = false;
        initRotX = rotX;
        initRotY = rotY;
        initZoom = zoom;

        return super.func_231048_c_(mouseX, mouseY, state);
    }

//    @Override
//    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
//    }
    
//    @Override
//    protected void actionPerformed(Button button) {
//        super.actionPerformed(button);
//
//        if (button == buttonChisel) {

//        }
//    }
}
