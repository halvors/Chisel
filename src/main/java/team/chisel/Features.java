package team.chisel;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.loot.ConstantRange;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import team.chisel.api.block.ChiselBlockFactory;
import team.chisel.client.data.ModelTemplates;
import team.chisel.client.data.VariantTemplates;
import team.chisel.client.sound.ChiselSoundTypes;
import team.chisel.common.block.BlockCarvable;
import team.chisel.common.block.BlockCarvableBookshelf;
import team.chisel.common.block.BlockCarvableCarpet;
import team.chisel.common.init.ChiselCompatTags;

public class Features {

    private static final ChiselBlockFactory _FACTORY = ChiselBlockFactory.newFactory(Chisel.registrate());
    
    public static final Map<String, BlockEntry<BlockCarvable>> ALUMINUM = _FACTORY.newType(Material.IRON, "metals/aluminum")
            .setGroupName("Aluminum Block")
            .addTag(ChiselCompatTags.STORAGE_BLOCKS_ALUMINUM)
            .variations(VariantTemplates.METAL)
            .build(b -> b.sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    
    public static final Map<String, BlockEntry<BlockCarvable>> ANDESITE = _FACTORY.newType(Material.ROCK, "andesite")
            .addBlock(Blocks.ANDESITE)
            .addBlock(Blocks.POLISHED_ANDESITE)
            .variations(VariantTemplates.ROCK)
            .build(b -> b.hardnessAndResistance(1.5F, 30.0F).sound(SoundType.STONE));
    
    public static final Map<String, BlockEntry<BlockCarvable>> ANTIBLOCK = _FACTORY.newType(Material.ROCK, "antiblock", (p, v) -> new BlockCarvable(p, v))
            .layer(() -> RenderType::getCutout)
            .variations(VariantTemplates.colors(ModelTemplates.twoLayerWithTop("antiblock", false), (prov, block) -> 
                    new ShapedRecipeBuilder(block, 8)
                        .patternLine("SSS").patternLine("SGS").patternLine("SSS")
                        .key('S', Tags.Items.STONE)
                        .key('G', Tags.Items.DUSTS_GLOWSTONE)
                        .addCriterion("has_glowstone", prov.hasItem(Tags.Items.DUSTS_GLOWSTONE))
                        .build(prov)))
            .build();
    
    public static final Map<String, BlockEntry<BlockCarvable>> BASALT = _FACTORY.newType(Material.ROCK, "basalt")
            .variation(VariantTemplates.RAW)
            .variations(VariantTemplates.ROCK)
            .addTag(ChiselCompatTags.STONE_BASALT)
            .build(b -> b.hardnessAndResistance(1.5F, 10.0F).sound(SoundType.STONE));
    
    
    private static final ImmutableList<WoodType> VANILLA_WOODS = ImmutableList.of(WoodType.OAK, WoodType.SPRUCE, WoodType.BIRCH, WoodType.ACACIA, WoodType.JUNGLE, WoodType.DARK_OAK);
    
    // Hardcode to vanilla wood types
    public static final Map<WoodType, Map<String, BlockEntry<BlockCarvableBookshelf>>> BOOKSHELVES = VANILLA_WOODS.stream()
            .collect(Collectors.toMap(Function.identity(), wood -> _FACTORY.newType(Material.WOOD, "bookshelf/" + wood.getName(), BlockCarvableBookshelf::new)
                    .loot((prov, block) -> prov.registerLootTable(block, RegistrateBlockLootTables.droppingWithSilkTouchOrRandomly(block, Items.BOOK, ConstantRange.of(3))))
                    .applyIf(() -> wood == WoodType.OAK, f -> f.addBlock(Blocks.BOOKSHELF))
                    .model((prov, block) -> {
                        prov.simpleBlock(block, prov.models().withExistingParent("block/" + ModelTemplates.name(block), prov.modLoc("cube_2_layer_sides"))
                                .texture("all", "minecraft:block/" + wood.getName() + "_planks")
                                .texture("side", "block/" + ModelTemplates.name(block).replace(wood.getName() + "/", "")));
                    })
                    .layer(() -> RenderType::getCutout)
                    .setGroupName(RegistrateLangProvider.toEnglishName(wood.getName()) + " Bookshelf")
                    .variation("rainbow")
                    .next("novice_necromancer")
                    .next("necromancer")
                    .next("redtomes")
                    .next("abandoned")
                    .next("hoarder")
                    .next("brim")
                    .next("historian")
                    .next("cans")
                    .next("papers")
                    .build(b -> b.sound(SoundType.WOOD).hardnessAndResistance(1.5f))));
            
    public static final Map<String, BlockEntry<BlockCarvable>> BRICKS = _FACTORY.newType(Material.ROCK, "bricks")
            .addBlock(Blocks.BRICKS)
            .variations(VariantTemplates.ROCK)
            .build(p -> p.hardnessAndResistance(2.0F, 6.0F));
    
    public static final Map<String, BlockEntry<BlockCarvable>> BRONZE = _FACTORY.newType(Material.IRON, "metals/bronze")
            .setGroupName("Bronze Block")
            .addTag(ChiselCompatTags.STORAGE_BLOCKS_BRONZE)
            .variations(VariantTemplates.METAL)
            .build(p -> p.sound(SoundType.METAL).hardnessAndResistance(5.0F).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    
    public static final Map<String, BlockEntry<BlockCarvable>> BROWNSTONE = _FACTORY.newType(Material.ROCK, "brownstone")
            .variation("default")
                .recipe((prov, block) -> new ShapedRecipeBuilder(block, 4)
                        .patternLine(" S ").patternLine("SCS").patternLine(" S ")
                        .key('S', Tags.Items.SANDSTONE)
                        .key('C', Items.CLAY_BALL)
                        .addCriterion("has_clay", prov.hasItem(Items.CLAY_BALL))
                        .build(prov))
            .next("block")
            .next("doubleslab")
                .model(ModelTemplates.cubeColumn("doubleslab-side", "block"))
            .next("blocks")
            .next("weathered")
            .next("weathered_block")
            .next("weathered_doubleslab")
                .model(ModelTemplates.cubeColumn("weathered_doubleslab-side", "weathered_block"))
            .next("weathered_blocks")
            .next("weathered_half")
                .model(ModelTemplates.cubeBottomTop("weathered_half-side", "default", "weathered"))
            .next("weathered_block_half")
                .model(ModelTemplates.cubeBottomTop("weathered_block_half-side", "block", "weathered_block"))
            .build(b -> b.sound(SoundType.STONE).hardnessAndResistance(1.0F));
//      BlockSpeedHandler.speedupBlocks.add(b);
//  });
    
    public static final Map<DyeColor, Map<String, BlockEntry<BlockCarvableCarpet>>> CARPET = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(Function.identity(), color -> _FACTORY.newType(Material.WOOL, "carpet/" + (color.getTranslationKey()), BlockCarvableCarpet::new)
                    .addBlock(new ResourceLocation(color.getTranslationKey() + "_carpet"))
                    .setGroupName(RegistrateLangProvider.toEnglishName(color.getTranslationKey()) + " Carpet")
                    .model((prov, block) ->
                        prov.simpleBlock(block, prov.models()
                                .carpet("block/" + ModelTemplates.name(block), prov.modLoc("block/" + ModelTemplates.name(block).replace("carpet", "wool")))
                                .texture("particle", "#wool")))
                    .variation("legacy")
                    .next("llama")
                    .build(b -> b.sound(SoundType.CLOTH).hardnessAndResistance(0.8F))));
    
    public static final Map<String, BlockEntry<BlockCarvable>> CHARCOAL = _FACTORY.newType(Material.ROCK, "charcoal")
            .addTag(ChiselCompatTags.STORAGE_BLOCKS_CHARCOAL)
            .variation(VariantTemplates.withRecipe(VariantTemplates.RAW, (prov, block) -> new ShapelessRecipeBuilder(block, 1)
                    .addIngredient(Items.CHARCOAL)
                    .addCriterion("has_charcoal", prov.hasItem(Items.CHARCOAL))
                    .build(prov)))
            .variations(VariantTemplates.ROCK)
            .build(b -> b.hardnessAndResistance(5.0F, 10.0F).sound(SoundType.STONE));
    
    public static final Map<String, BlockEntry<BlockCarvable>> COAL = _FACTORY.newType(Material.ROCK, "coal")
            .addTag(Tags.Blocks.STORAGE_BLOCKS_COAL)
            .variations(/*VariantTemplates.withUncraft(*/VariantTemplates.ROCK/*, Items.COAL)*/) // TODO
            .build(b -> b.hardnessAndResistance(5.0F, 10.0F).sound(SoundType.STONE));
    
    public static final Map<String, BlockEntry<BlockCarvable>> COBBLESTONE = _FACTORY.newType(Material.ROCK, "cobblestone")
            .addBlock(Blocks.COBBLESTONE)
            .variations(VariantTemplates.ROCK)
            .variation("extra/emboss")
            .next("extra/indent")
            .next("extra/marker")
            .build(b -> b.hardnessAndResistance(2.0F, 10.0F).sound(SoundType.STONE));
    
    public static final Map<DyeColor, Map<String, BlockEntry<BlockCarvable>>> CONCRETE = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(Function.identity(), color -> _FACTORY.newType(Material.ROCK, "concrete/" + color.getTranslationKey())
                    .addBlock(new ResourceLocation(color.getTranslationKey() + "_concrete"))
                    .setGroupName(RegistrateLangProvider.toEnglishName(color.getTranslationKey()) + " Concrete")
                    .variations(VariantTemplates.ROCK)
                    .build(p -> p.sound(SoundType.STONE).hardnessAndResistance(1.8F))));
//  BlockSpeedHandler.speedupBlocks.add(b);
    
    public static final Map<String, BlockEntry<BlockCarvable>> FACTORY = _FACTORY.newType(Material.IRON, "factory")
            .variation("dots")
                .recipe((prov, block) -> new ShapedRecipeBuilder(block, 32)
                        .patternLine("SXS").patternLine("X X").patternLine("SXS")
                        .key('S', Tags.Items.STONE)
                        .key('X', Tags.Items.INGOTS_IRON)
                        .addCriterion("has_iron", prov.hasItem(Tags.Items.INGOTS_IRON))
                        .build(prov))
            .next("rust2")
            .next("rust")
            .next("platex")
            .next("wireframewhite")
            .next("wireframe")
            .next("hazard")
            .next("hazardorange")
            .next("circuit")
            .next("metalbox")
                .model(ModelTemplates.cubeColumn())
            .next("goldplate")
            .next("goldplating")
            .next("grinder")
            .next("plating")
            .next("rustplates")
            .next("column")
                .model(ModelTemplates.cubeColumn())
            .next("frameblue")
            .next("iceiceice")
            .next("tilemosaic")
            .next("vent")
                .model(ModelTemplates.cubeColumn())
            .next("wireframeblue")
            .build(b -> b.sound(ChiselSoundTypes.METAL));

    public static final Map<String, BlockEntry<BlockCarvable>> LIMESTONE = _FACTORY.newType(Material.ROCK, "limestone")
            .variation(VariantTemplates.RAW)
            .variations(VariantTemplates.ROCK)
            .addTag(ChiselCompatTags.STONE_LIMESTONE)
            .build(b -> b.hardnessAndResistance(1.5F, 10.0F).sound(SoundType.STONE));

    public static final Map<String, BlockEntry<BlockCarvable>> MARBLE = _FACTORY.newType(Material.ROCK, "marble")
            .variation(VariantTemplates.RAW)
            .variations(VariantTemplates.ROCK)
            .addTag(ChiselCompatTags.STONE_MARBLE)
            .build(b -> b.hardnessAndResistance(1.5F, 10.0F).sound(SoundType.STONE));

    public static final Map<WoodType, Map<String, BlockEntry<BlockCarvable>>> PLANKS = VANILLA_WOODS.stream()
            .map(t -> Pair.of(t, new ResourceLocation(t.getName() + "_planks")))
            .collect(Collectors.toMap(Pair::getFirst, pair -> _FACTORY.newType(Material.WOOD, "planks/" + pair.getFirst().getName())
                    .addBlock(pair.getSecond())
                    .setGroupName(RegistrateLangProvider.toEnglishName(pair.getFirst().getName()) + " Planks")
                    .variations(VariantTemplates.PLANKS)
                    .build($ -> Block.Properties.from(ForgeRegistries.BLOCKS.getValue(pair.getSecond())))));

    // TODO Temp Merge with above once 1.16 hits
    public static final Map<String, Map<String, BlockEntry<BlockCarvable>>> PLANKS_116 = ImmutableList.of("crimson").stream()
            .map(t -> Pair.of(t, new ResourceLocation(t + "_planks")))
            .collect(Collectors.toMap(Pair::getFirst, pair -> _FACTORY.newType(Material.WOOD, "planks/" + pair.getFirst())
                    .addBlock(pair.getSecond())
                    .setGroupName(RegistrateLangProvider.toEnglishName(pair.getFirst()) + " Planks")
                    .variations(VariantTemplates.PLANKS)
                    .build($ -> Block.Properties.from(ForgeRegistries.BLOCKS.getValue(pair.getSecond())))));

    public static final Map<String, BlockEntry<BlockCarvable>> STONE_BRICKS = _FACTORY.newType(Material.ROCK, "stone_bricks")
            .addBlock(Blocks.STONE)
            .addBlock(Blocks.STONE_BRICKS)
            .addBlock(Blocks.CHISELED_STONE_BRICKS)
            .addBlock(Blocks.CRACKED_STONE_BRICKS)
            .variations(VariantTemplates.ROCK)
            .variation("extra/largeornate")
            .next("extra/poison")
            .next("extra/sunken")
            .build(p -> p.hardnessAndResistance(1.5F, 10.0F));
    
    public static final Map<String, BlockEntry<BlockCarvable>> TYRIAN = _FACTORY.newType(Material.IRON, "tyrian")
            .variation("shining")
                .recipe((prov, block) -> new ShapedRecipeBuilder(block, 32)
                        .patternLine("SSS").patternLine("SXS").patternLine("SSS")
                        .key('S', Tags.Items.STONE)
                        .key('X', Tags.Items.INGOTS_IRON)
                        .addCriterion("has_iron", prov.hasItem(Tags.Items.INGOTS_IRON))
                        .build(prov))
            .next("tyrian")
            .next("chaotic")
            .next("softplate")
            .next("rust")
            .next("elaborate")
            .next("routes")
            .next("platform")
            .next("platetiles")
            .next("diagonal")
            .next("dent")
            .next("blueplating")
            .next("black")
            .next("black2")
            .next("opening")
            .next("plate") // FIXME temporary texture
            .build(b -> b.sound(SoundType.METAL));
    
    public static final Map<DyeColor, Map<String, BlockEntry<BlockCarvable>>> WOOL = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(Function.identity(), color -> _FACTORY.newType(Material.WOOL, "wool/" + (color.getTranslationKey()))
                    .addBlock(new ResourceLocation(color.getTranslationKey() + "_wool"))
                    .setGroupName(RegistrateLangProvider.toEnglishName(color.getTranslationKey()) + " Wool")
                    .variation("legacy")
                    .next("llama")
                    .build(b -> b.sound(SoundType.CLOTH).hardnessAndResistance(0.8F))));
    
    public static void init() {}
}
