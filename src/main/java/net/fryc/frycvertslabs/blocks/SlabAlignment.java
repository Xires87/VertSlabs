package net.fryc.frycvertslabs.blocks;

import net.minecraft.util.StringRepresentable;

public enum SlabAlignment implements StringRepresentable {

    HORIZONTAL("horizontal"),
    VERTICAL_NORTH("vertical_north"),
    VERTICAL_WEST("vertical_west");

    private final String name;

    SlabAlignment(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
