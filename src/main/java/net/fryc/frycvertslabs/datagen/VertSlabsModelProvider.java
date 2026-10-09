package net.fryc.frycvertslabs.datagen;

import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fryc.frycvertslabs.VertSlabs;
import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class VertSlabsModelProvider extends FabricModelProvider {

    private static final String BLOCK_DIR = "block/";
    private static final String TOP_SUFFIX = "_top";
    private static final String SLAB_SUFFIX = "_slab";
    private static final List<String> POSSIBLE_FULL_BLOCK_SUFFIXES = List.of(
            "",
            "s",
            "_planks",
            "_block"
    );

    private static final List<String> IGNORED_PREFIXES = List.of(
            "waxed_"
    );

    private static final Map<String, String> HANDMADE_CORRECTIONS = Map.of(
            "bamboo", "bamboo_planks"
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
            Identifier id = cutPrefix(BuiltInRegistries.BLOCK.getKey(block));
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
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), bottomModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_WEST)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), bottomModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Z_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.HORIZONTAL)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel)
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_NORTH)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                                .with(BlockModelGenerators
                                        .condition()
                                        .term(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.VERTICAL_WEST)
                                        .term(BlockStateProperties.SLAB_TYPE, SlabType.TOP), topModel.with(VariantMutator.UV_LOCK.withValue(true)).with(VariantMutator.Z_ROT.withValue(Quadrant.R90)))
                );
            }
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        List<Item> list = BuiltInRegistries.ITEM
                .stream()
                .filter(item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock)
                .toList();

        Transformation verticalTransform = new Transformation(
                new Vector3f(0.707F, 0.0F, 0.0F),
                new Quaternionf(0.0, 0.0, 0.0, 1.0),
                new Vector3f(1.0F, 1.0F, 1.0F),
                new Quaternionf(0.0, 0.0, 0.707, 0.707)
        );

        list.forEach(item -> {
            Identifier id = cutPrefix(BuiltInRegistries.ITEM.getKey(item));
            Identifier modelId = Identifier.fromNamespaceAndPath(id.getNamespace(), BLOCK_DIR + id.getPath());

            ItemModel.Unbaked vertModel = ItemModelUtils.plainModel(modelId, verticalTransform);
            ItemModel.Unbaked model = ItemModelUtils.plainModel(modelId);

            itemModelGenerators.itemModelOutput.accept(
                    item,
                    ItemModelUtils.select(
                            new ComponentContents<>(ModDataComponentTypes.SLAB_VERTICAL),
                            model,
                            ItemModelUtils.when(true, vertModel),
                            ItemModelUtils.when(false, model)
                    )
            );
        });
    }

    private static Optional<Identifier> tryToFindFullBlockById(Identifier fullBlockId) {
        if(shouldPerformHandmadeCorrection(fullBlockId)) {
            return Optional.of(performHandmadeCorrection(fullBlockId));
        }

        for(String suffix : POSSIBLE_FULL_BLOCK_SUFFIXES) {
            Identifier id = Identifier.fromNamespaceAndPath(fullBlockId.getNamespace(), fullBlockId.getPath() + suffix);
            if(BuiltInRegistries.BLOCK.get(id).isPresent()) {
                return Optional.of(id);
            }
        }

        return Optional.empty();
    }

    private static Identifier cutPrefix(Identifier id) {
        return Identifier.fromNamespaceAndPath(
                id.getNamespace(),
                id.getPath().replaceFirst(IGNORED_PREFIXES.stream().filter(id.getPath()::startsWith).findFirst().orElse(""), "")
        );
    }

    private static boolean shouldPerformHandmadeCorrection(Identifier id) {
        return HANDMADE_CORRECTIONS.containsKey(id.getPath());
    }

    private static Identifier performHandmadeCorrection(Identifier id) {
        return Identifier.fromNamespaceAndPath(id.getNamespace(), HANDMADE_CORRECTIONS.get(id.getPath()));
    }
}
