package net.fryc.frycvertslabs.util;

import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.VoxelShape;
import oshi.util.tuples.Pair;

public class SlabHelper {

    public static final VoxelShape SHAPE_LEFT_WEST = Block.box(0, 0, 0, 8, 16, 16);
    public static final VoxelShape SHAPE_RIGHT_WEST = Block.box(8, 0, 0, 16, 16, 16);
    public static final VoxelShape SHAPE_LEFT_NORTH = Block.box(0, 0, 8, 16, 16, 16);
    public static final VoxelShape SHAPE_RIGHT_NORTH = Block.box(0, 0, 0, 16, 16, 8);

    public static Pair<SlabType, SlabAlignment> getStatesForVerticalPlacement(BlockPlaceContext context) {
        return switch (context.getHorizontalDirection()) {
            case NORTH -> new Pair<>(context.getClickedPos().getZ() - context.getClickLocation().z() < -0.5 ? SlabType.BOTTOM : SlabType.TOP, SlabAlignment.VERTICAL_NORTH);
            case SOUTH -> new Pair<>(context.getClickedPos().getZ() - context.getClickLocation().z() > -0.5 ? SlabType.TOP : SlabType.BOTTOM, SlabAlignment.VERTICAL_NORTH);
            case WEST -> new Pair<>(context.getClickedPos().getX() - context.getClickLocation().x() > -0.5 ? SlabType.BOTTOM : SlabType.TOP, SlabAlignment.VERTICAL_WEST);
            case EAST -> new Pair<>(context.getClickedPos().getX() - context.getClickLocation().x() < -0.5 ? SlabType.TOP : SlabType.BOTTOM, SlabAlignment.VERTICAL_WEST);
            default -> new Pair<>(SlabType.BOTTOM, SlabAlignment.VERTICAL_NORTH);
        };
    }

    public static boolean shouldIgnoreVerticalSlabComponent(DataComponentMap a, DataComponentMap b) {
        if(a.has(ModDataComponentTypes.SLAB_VERTICAL) || b.has(ModDataComponentTypes.SLAB_VERTICAL)) {
            int acceptedDiff = a.has(ModDataComponentTypes.SLAB_VERTICAL) && b.has(ModDataComponentTypes.SLAB_VERTICAL) ? 0 : 1;

            return Math.abs(a.size() - b.size()) == acceptedDiff && a.stream().allMatch(component -> {
                return component.type().equals(ModDataComponentTypes.SLAB_VERTICAL) ||
                        (b.has(component.type()) && b.get(component.type()) == component.value());
            });
        }

        return false;
    }
}
