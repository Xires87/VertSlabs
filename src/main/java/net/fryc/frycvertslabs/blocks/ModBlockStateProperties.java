package net.fryc.frycvertslabs.blocks;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModBlockStateProperties {

    public static final EnumProperty<SlabAlignment> SLAB_ALIGNMENT = EnumProperty.create("alignment", SlabAlignment.class);

    public static void registerModBlockProperties() {
    }
}
