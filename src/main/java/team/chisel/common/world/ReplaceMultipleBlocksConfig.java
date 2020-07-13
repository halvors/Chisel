package team.chisel.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.List;

public class ReplaceMultipleBlocksConfig implements IFeatureConfig {

   public static final Codec<ReplaceMultipleBlocksConfig> codec = RecordCodecBuilder.create((replaceMultipleBlocksConfig) -> {
      return replaceMultipleBlocksConfig.group(BlockState.field_235877_b_.listOf().fieldOf("toReplace").forGetter((replaceMultipleBlocksConfig1) -> {
         return replaceMultipleBlocksConfig1.toReplace;
      }), BlockState.field_235877_b_.fieldOf("result").forGetter((replaceMultipleBlocksConfig1) -> {
         return replaceMultipleBlocksConfig1.result;
      })).apply(replaceMultipleBlocksConfig, ReplaceMultipleBlocksConfig::new);
   });

   public final List<BlockState> toReplace;
   public final BlockState result;

   public ReplaceMultipleBlocksConfig(List<BlockState> toReplace, BlockState result) {
      this.toReplace = toReplace;
      this.result = result;
   }
}