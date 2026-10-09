package net.fryc.frycvertslabs.mixin;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
abstract class ItemMixin implements ItemLike, FeatureElement, FabricItem {

    @Inject(at = @At("HEAD"), method = "use(" +
                "Lnet/minecraft/world/level/Level;" +
                "Lnet/minecraft/world/entity/player/Player;" +
                "Lnet/minecraft/world/InteractionHand;" +
            ")Lnet/minecraft/world/InteractionResult;", cancellable = true)
    private void rotateSlabOnRightClick(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ret) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getItem() instanceof BlockItem blockItem) {
            if(blockItem.getBlock() instanceof SlabBlock) {
                boolean vertical = stack.getOrDefault(ModDataComponentTypes.SLAB_VERTICAL, false);

                stack.set(ModDataComponentTypes.SLAB_VERTICAL, !vertical);
                ret.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }
}
