package net.fryc.frycvertslabs.mixin;

import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlabBlock.class)
abstract class SlabBlockMixin extends Block implements SimpleWaterloggedBlock {

	@Shadow
	private static @Final VoxelShape SHAPE_BOTTOM;

	@Shadow
	private static @Final VoxelShape SHAPE_TOP;

	private static final VoxelShape SHAPE_LEFT_NORTH = Block.box(0, 0, 0, 8, 16, 16);
	private static final VoxelShape SHAPE_RIGHT_NORTH = Block.box(8, 0, 0, 16, 16, 16);
	private static final VoxelShape SHAPE_LEFT_WEST = Block.box(0, 0, 8, 16, 16, 16);
	private static final VoxelShape SHAPE_RIGHT_WEST = Block.box(0, 0, 0, 16, 16, 8);


	public SlabBlockMixin(Properties properties) {
		super(properties);
	}


	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V")
	private void defaultAlignmentProperty(BlockBehaviour.Properties properties, CallbackInfo info) {
		this.registerDefaultState(this.defaultBlockState().setValue(ModBlockStateProperties.SLAB_ALIGNMENT, SlabAlignment.HORIZONTAL));
	}


	@Inject(at = @At("HEAD"), method = "getShape(" +
				"Lnet/minecraft/world/level/block/state/BlockState;" +
				"Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;" +
				"Lnet/minecraft/world/phys/shapes/CollisionContext;" +
			")Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void getVerticalSlabShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ret) {
		switch (state.getValue(ModBlockStateProperties.SLAB_ALIGNMENT)) {
			case HORIZONTAL -> {}
			case VERTICAL_NORTH -> {
				switch (state.getValue(SlabBlock.TYPE)) {
					case TOP -> ret.setReturnValue(SHAPE_RIGHT_NORTH);
					case BOTTOM -> ret.setReturnValue(SHAPE_LEFT_NORTH);
					case DOUBLE -> ret.setReturnValue(Shapes.block());
				}
			}
			case VERTICAL_WEST -> {
				switch (state.getValue(SlabBlock.TYPE)) {
					case TOP -> ret.setReturnValue(SHAPE_RIGHT_WEST);
					case BOTTOM -> ret.setReturnValue(SHAPE_LEFT_WEST);
					case DOUBLE -> ret.setReturnValue(Shapes.block());
				}
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V")
	private void addNewBlockStateProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo info) {
		builder.add(ModBlockStateProperties.SLAB_ALIGNMENT);
	}

}