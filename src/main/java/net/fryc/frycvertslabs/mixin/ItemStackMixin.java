package net.fryc.frycvertslabs.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fryc.frycvertslabs.util.SlabHelper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @WrapOperation(
            method = "isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z")
    )
    private static boolean makeVerticalSlabsStackWithHorizontal(Object a, Object b, Operation<Boolean> original) {
        return original.call(a, b) || SlabHelper.shouldIgnoreVerticalSlabComponent((DataComponentMap) a, (DataComponentMap) b);
    }
}
