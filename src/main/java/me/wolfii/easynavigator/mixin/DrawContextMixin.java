package me.wolfii.easynavigator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public class DrawContextMixin {
    @WrapOperation(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At(value = "NEW", target = "(Ljava/lang/String;Lorg/joml/Matrix3x2f;Lnet/minecraft/client/renderer/item/TrackingItemStackRenderState;IILnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/gui/render/state/GuiItemRenderState;"))
    private GuiItemRenderState scaleItemRendering(String name, Matrix3x2f pose, TrackingItemStackRenderState state, int x, int y, ScreenRectangle scissor, Operation<GuiItemRenderState> original, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.getComponents().getOrDefault(EasyNavigatorComponentData.CUSTOM_SCALE, false)) return original.call(name, pose, state, x, y, scissor);
        float customScale = Config.getConfig().scale;
        return new GuiItemRenderState(name, pose.scale(customScale, new Matrix3x2f()), state, (int) (x / customScale), (int) (y / customScale), scissor);
    }
}
