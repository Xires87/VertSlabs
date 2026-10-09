package net.fryc.frycvertslabs;

import net.fabricmc.api.ModInitializer;
import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertSlabs implements ModInitializer {

	public static final String MOD_ID = "frycvertslabs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlockStateProperties.registerModBlockProperties();
		ModDataComponentTypes.registerModDataComponentTypes();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
