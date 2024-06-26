package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", shift = At.Shift.AFTER))
    private void scaleItemRendering(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int z, CallbackInfo ci) {
        if (!stack.getComponents().getOrDefault(EasyNavigatorComponentData.CUSTOM_SCALE, false)) return;
        float customScale = Config.getConfig().scale;
        ((DrawContextAccessor) this).getMatrices().scale(customScale, customScale, customScale);
    }
}
