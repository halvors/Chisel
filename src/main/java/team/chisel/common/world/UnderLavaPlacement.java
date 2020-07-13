package team.chisel.common.world;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class UnderLavaPlacement extends Placement<NoPlacementConfig> {

    public UnderLavaPlacement(Codec<NoPlacementConfig> p_i232086_1_) {
        super(p_i232086_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos pos) {
        BlockPos origin = new BlockPos(pos.getX() + 8, 0, pos.getZ() + 8);
        return BlockPos.getAllInBox(origin, origin.add(15, 11, 15))
                .filter(p -> world.getBlockState(p).getMaterial() == Material.LAVA)
                .flatMap(p -> Arrays.stream(Direction.values())
                        .filter(d -> d != Direction.UP)
                        .map(d -> p.offset(d))
                        .filter(p2 -> world.getBlockState(p2).getMaterial() != Material.LAVA));
    }
}
