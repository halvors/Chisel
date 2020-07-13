package team.chisel.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.Random;

public class ReplaceBlockDownwardsFeature extends Feature<ReplaceMultipleBlocksConfig> {

    public ReplaceBlockDownwardsFeature(Codec<ReplaceMultipleBlocksConfig> p_i231953_1_) {
        super(p_i231953_1_);
    }

    @Override
    public boolean func_230362_a_(ISeedReader seedReader, StructureManager structureManager, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, ReplaceMultipleBlocksConfig config) {
        boolean ret = false;
        int max = 2;
        if (rand.nextFloat() < 0.7f) {
            max++;
            if (rand.nextFloat() < 0.2f) {
                max++;
            }
        }
        for (int i = 0; i < max; i++) {
            if (config.toReplace.contains(seedReader.getBlockState(pos))) {
                seedReader.setBlockState(pos, config.result, 2);
                ret = true;
            }
            pos = pos.down();
        }
        return ret;
    }
}
