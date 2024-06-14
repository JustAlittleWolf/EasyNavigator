package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItem.class)
public class CompassItemMixin {
    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    public void removeGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getComponents() != null && stack.getComponents().getOrDefault(EasyNavigatorComponentData.REMOVE_ENCHANTMENT_GLINT, false)) {
            cir.setReturnValue(false);
        }
    }
}
