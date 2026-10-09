package net.fryc.frycvertslabs.datagen;

import com.mojang.math.Quadrant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.fryc.frycvertslabs.VertSlabs;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class VertSlabsModelProvider extends FabricModelProvider {

    private static final String BLOCK_DIR = "block/";
    private static final String TOP_SUFFIX = "_top";
    private static final String SLAB_SUFFIX = "_slab";
    private static final List<String> POSSIBLE_FULL_BLOCK_SUFFIXES = List.of(
            "",
            "s",
            "_planks"
    );

    public VertSlabsModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        List<SlabBlock> list = BuiltInRegistries.BLOCK.stream()
                .filter(block -> block instanceof SlabBlock)
                .map(block -> (SlabBlock) block)
                .toList();

        VertSlabs.LOGGER.info("Detected slabs:");
        list.forEach(el -> VertSlabs.LOGGER.info(el.toString()));

        list.forEach(block -> {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            Identifier bottomId = Identifier.fromNamespaceAndPath(id.getNamespace(), BLOCK_DIR + id.getPath());
            Identifier topId = Identifier.fromNamespaceAndPath(id.getNamespace(), BLOCK_DIR + id.getPath() + TOP_SUFFIX);
            Identifier fullBlockId = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace(SLAB_SUFFIX, ""));
            Optional<Identifier> optional = tryToFindFullBlockById(fullBlockId);

            if(optional.isPresent()) {
                fullBlockId = Identifier.fromNamespaceAndPath(optional.get().getNamespace(), BLOCK_DIR + optional.get().getPath());
                MultiVariant bottomModel = BlockModelGenerators.plainVariant(bottomId);
                MultiVariant topModel = BlockModelGenerators.plainVariant(topId);

                blockModelGenerators.blockStateOutput.accept(
                        MultiPartGenerator.multiPart(block)
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE), BlockModelGenerators.plainVariant(fullBlockId))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.HORIZONTAL)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), bottomModel)
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_NORTH)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), bottomModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Z_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_WEST)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), bottomModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.HORIZONTAL)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel)
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_NORTH)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Z_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_WEST)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                );
            }
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

    private static Optional<Identifier> tryToFindFullBlockById(Identifier slabId) {
        for(String suffix : POSSIBLE_FULL_BLOCK_SUFFIXES) {
            Identifier id = Identifier.fromNamespaceAndPath(slabId.getNamespace(), slabId.getPath() + suffix);
            if(BuiltInRegistries.BLOCK.get(id).isPresent()) {
                return Optional.of(id);
            }
        }

        return Optional.empty();
    }
}
