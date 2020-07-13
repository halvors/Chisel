package team.chisel.common.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import team.chisel.api.block.ICarvable;
import team.chisel.common.block.ItemChiselBlock;

@WailaPlugin
public class ChiselDataHandler implements IWailaPlugin, IComponentProvider {
    
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(this, TooltipPosition.BODY, ICarvable.class);
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() instanceof ICarvable) {
            ItemStack stack = accessor.getStack();
            if (stack.getItem() instanceof ItemChiselBlock) {
                ((ItemChiselBlock)stack.getItem()).addTooltips(stack, tooltip);
            } else {
                ICarvable block = (ICarvable) accessor.getBlock();
                ItemChiselBlock.addTooltips(stack, block, tooltip);
            }
        }
    }
}
