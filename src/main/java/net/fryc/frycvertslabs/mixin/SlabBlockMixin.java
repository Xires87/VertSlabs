package net.fryc.frycvertslabs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fryc.frycvertslabs.blocks.ModBlockStateProperties;
import net.fryc.frycvertslabs.blocks.SlabAlignment;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.fryc.frycvertslabs.util.SlabHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
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


	public SlabBlockMixin(Properties properties) {
		super(properties);
	}


	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V")
	private void defaultAlignmentProperty(Properties properties, CallbackInfo info) {
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
					case TOP -> ret.setReturnValue(SlabHelper.SHAPE_RIGHT_NORTH);
					case BOTTOM -> ret.setReturnValue(SlabHelper.SHAPE_LEFT_NORTH);
					case DOUBLE -> ret.setReturnValue(Shapes.block());
				}
			}
			case VERTICAL_WEST -> {
				switch (state.getValue(SlabBlock.TYPE)) {
					case TOP -> ret.setReturnValue(SlabHelper.SHAPE_RIGHT_WEST);
					case BOTTOM -> ret.setReturnValue(SlabHelper.SHAPE_LEFT_WEST);
					case DOUBLE -> ret.setReturnValue(Shapes.block());
				}
			}
		}
	}


	@Inject(at = @At("HEAD"), method = "canBeReplaced(" +
				"Lnet/minecraft/world/level/block/state/BlockState;" +
				"Lnet/minecraft/world/item/context/BlockPlaceContext;" +
			")Z", cancellable = true)
	private void modifyReplacementRules(BlockState state, BlockPlaceContext context, CallbackInfoReturnable<Boolean> ret) {
		ItemStack itemStack = context.getItemInHand();
		if(itemStack.getOrDefault(ModDataComponentTypes.SLAB_VERTICAL, false)) {
			SlabType type = state.getValue(SlabBlock.TYPE);
			SlabAlignment alignment = state.getValue(ModBlockStateProperties.SLAB_ALIGNMENT);
			if (type != SlabType.DOUBLE && alignment != SlabAlignment.HORIZONTAL && itemStack.is(this.asItem())) {
				if (context.replacingClickedOnBlock()) {
					switch (context.getClickedFace()) {
						case NORTH -> ret.setReturnValue(type == SlabType.BOTTOM && alignment == SlabAlignment.VERTICAL_NORTH);
						case EAST -> ret.setReturnValue(type == SlabType.BOTTOM && alignment == SlabAlignment.VERTICAL_WEST);
						case WEST -> ret.setReturnValue(type == SlabType.TOP && alignment == SlabAlignment.VERTICAL_WEST);
						case SOUTH -> ret.setReturnValue(type == SlabType.TOP && alignment == SlabAlignment.VERTICAL_NORTH);
						case UP -> {
							ret.setReturnValue(context.getClickLocation().y - context.getClickedPos().getY() < 0.5);
						}
						case DOWN -> {
							ret.setReturnValue(context.getClickLocation().y - context.getClickedPos().getY() > 0.5);
						}
					}
				}
				else {
					ret.setReturnValue(true);
				}
			}
			else {
				ret.setReturnValue(false);
			}
		}
	}


	@ModifyExpressionValue(
			method = "canBeReplaced(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/context/BlockPlaceContext;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
	)
	private boolean disallowCombiningHorizontalSlabsWithVertical(boolean original, BlockState state, BlockPlaceContext context) {
		return original && state.getValue(ModBlockStateProperties.SLAB_ALIGNMENT) == SlabAlignment.HORIZONTAL;
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

				Pair<SlabType, SlabAlignment> pair = SlabHelper.getStatesForVerticalPlacement(context);

				ret.setReturnValue(result.setValue(SlabBlock.TYPE, pair.getA()).setValue(ModBlockStateProperties.SLAB_ALIGNMENT, pair.getB()));
			}
		}
	}
}