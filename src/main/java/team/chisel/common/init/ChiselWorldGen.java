package team.chisel.common.init;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import team.chisel.Chisel;
import team.chisel.Features;
import team.chisel.client.data.VariantTemplates;
import team.chisel.common.world.ReplaceBlockDownwardsFeature;
import team.chisel.common.world.ReplaceMultipleBlocksConfig;
import team.chisel.common.world.UnderLavaPlacement;

import java.util.Arrays;

public class ChiselWorldGen {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Chisel.MOD_ID);
    public static final DeferredRegister<Placement<?>> PLACEMENTS = DeferredRegister.create(ForgeRegistries.DECORATORS, Chisel.MOD_ID);

    public static final RegistryObject<ReplaceBlockDownwardsFeature> REPLACE_BLOCK_DOWNWARDS = FEATURES.register("replace_block_downwards",
            () -> new ReplaceBlockDownwardsFeature(ReplaceMultipleBlocksConfig.codec));
    
    public static final RegistryObject<UnderLavaPlacement> PLACE_UNDER_LAVA = PLACEMENTS.register(
            "under_lava", () -> new UnderLavaPlacement(NoPlacementConfig.field_236555_a_));
    
    public static void registerWorldGen() {
        for (Biome b : ForgeRegistries.BIOMES.getValues()) {
            if (BiomeDictionary.hasType(b, BiomeDictionary.Type.OVERWORLD)) {
                // Basalt (under and around lava lakes)
                b.addFeature(Decoration.UNDERGROUND_ORES, REPLACE_BLOCK_DOWNWARDS.get()
                        .withConfiguration(new ReplaceMultipleBlocksConfig(Arrays.asList(Blocks.STONE.getDefaultState(), Blocks.ANDESITE.getDefaultState(), Blocks.GRANITE.getDefaultState(), Blocks.DIORITE.getDefaultState()), Features.BASALT.get(VariantTemplates.RAW.getName()).get().getDefaultState()))
                        .withPlacement(PLACE_UNDER_LAVA.get().configure(NoPlacementConfig.NO_PLACEMENT_CONFIG)));
                // Limestone (33x *6 @ y64 +48)
                b.addFeature(Decoration.UNDERGROUND_ORES, Feature.ORE
                        .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.LIMESTONE.get(VariantTemplates.RAW.getName()).get().getDefaultState(), 33))
                        .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(6, 64, 0, 48))));
                // Marble (33x *6 @ y24 +48)
                b.addFeature(Decoration.UNDERGROUND_ORES, Feature.ORE
                        .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, Features.MARBLE.get(VariantTemplates.RAW.getName()).get().getDefaultState(), 33))
                        .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(6, 24, 0, 48))));
            }
        }
    }
}
