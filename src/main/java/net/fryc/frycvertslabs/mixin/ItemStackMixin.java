package net.fryc.frycvertslabs.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fryc.frycvertslabs.components.ModDataComponentTypes;
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
        return original.call(a, b) || shouldIgnoreVerticalSlabComponent((DataComponentMap) a, (DataComponentMap) b);
    }

    private static boolean shouldIgnoreVerticalSlabComponent(DataComponentMap a, DataComponentMap b) {
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
