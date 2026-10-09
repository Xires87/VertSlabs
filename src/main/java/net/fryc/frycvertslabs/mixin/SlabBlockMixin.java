package net.fryc.frycvertslabs.mixin;

import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
import oshi.util.tuples.Pair;

@Mixin(SlabBlock.class)
abstract class SlabBlockMixin extends Block implements SimpleWaterloggedBlock {

	@Shadow
	private static @Final VoxelShape SHAPE_BOTTOM;

	@Shadow
	private static @Final VoxelShape SHAPE_TOP;

	private static final VoxelShape SHAPE_LEFT_WEST = Block.box(0, 0, 0, 8, 16, 16);
	private static final VoxelShape SHAPE_RIGHT_WEST = Block.box(8, 0, 0, 16, 16, 16);
	private static final VoxelShape SHAPE_LEFT_NORTH = Block.box(0, 0, 8, 16, 16, 16);
	private static final VoxelShape SHAPE_RIGHT_NORTH = Block.box(0, 0, 0, 16, 16, 8);


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


	@Inject(at = @At("HEAD"), method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;", cancellable = true)
	private void modifyStateForPlacementWhenVertical(BlockPlaceContext context, CallbackInfoReturnable<BlockState> ret) {
		if(context.getItemInHand().getOrDefault(ModDataComponentTypes.SLAB_VERTICAL, false)){
			BlockPos pos = context.getClickedPos();
			BlockState replacedBlockState = context.getLevel().getBlockState(pos);
			if (replacedBlockState.is(this)) {
				ret.setReturnValue(replacedBlockState.setValue(SlabBlock.TYPE, SlabType.DOUBLE).setValue(SlabBlock.WATERLOGGED, false));
			}
			else {
				FluidState replacedFluidState = context.getLevel().getFluidState(pos);
				BlockState result = this.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(SlabBlock.WATERLOGGED, replacedFluidState.is(Fluids.WATER));

				Direction clickedFace = context.getClickedFace();
				Pair<SlabType, SlabAlignment> pair = getStatesForVerticalPlacement(clickedFace);

				ret.setReturnValue(result.setValue(SlabBlock.TYPE, pair.getA()).setValue(ModBlockStateProperties.SLAB_ALIGNMENT, pair.getB()));


				//ret.setReturnValue(clickedFace != Direction.DOWN && (clickedFace == Direction.UP || !(context.getClickLocation().y - (double)pos.getY() > (double)0.5F)) ? result : (BlockState)result.setValue(SlabBlock.TYPE, SlabType.TOP));
			}
		}
	}

	private static Pair<SlabType, SlabAlignment> getStatesForVerticalPlacement(Direction clickedFace) {
		if(clickedFace.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
			return switch (clickedFace.getAxis()) {
				case X -> new Pair<>(SlabType.TOP, SlabAlignment.VERTICAL_WEST);
				case Y -> new Pair<>(SlabType.BOTTOM, SlabAlignment.VERTICAL_NORTH);
				case Z -> new Pair<>(SlabType.BOTTOM, SlabAlignment.VERTICAL_NORTH);
			};
		}

		return switch (clickedFace.getAxis()) {
			case X -> new Pair<>(SlabType.BOTTOM, SlabAlignment.VERTICAL_WEST);
			case Y -> new Pair<>(SlabType.TOP, SlabAlignment.VERTICAL_NORTH);
			case Z -> new Pair<>(SlabType.TOP, SlabAlignment.VERTICAL_NORTH);
		};
	}

}