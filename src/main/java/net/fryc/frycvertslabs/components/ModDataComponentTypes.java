package net.fryc.frycvertslabs.components;

import com.mojang.serialization.Codec;
import net.fryc.frycvertslabs.VertSlabs;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModDataComponentTypes {

    public static final DataComponentType<Boolean> SLAB_VERTICAL = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            VertSlabs.id("slab_vertical"),
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );


    public static void registerModDataComponentTypes() {

    }
}
