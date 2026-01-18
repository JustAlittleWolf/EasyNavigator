package me.wolfii.easynavigator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @WrapOperation(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", at = @At(value = "NEW", target = "(Ljava/lang/String;Lorg/joml/Matrix3x2f;Lnet/minecraft/client/render/item/KeyedItemRenderState;IILnet/minecraft/client/gui/ScreenRect;)Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;"))
    private ItemGuiElementRenderState scaleItemRendering(String name, Matrix3x2f pose, KeyedItemRenderState state, int x, int y, ScreenRect scissor, Operation<ItemGuiElementRenderState> original, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.getComponents().getOrDefault(EasyNavigatorComponentData.CUSTOM_SCALE, false)) return original.call(name, pose, state, x, y, scissor);
        float customScale = Config.getConfig().scale;
        return new ItemGuiElementRenderState(name, pose.scale(customScale, new Matrix3x2f()), state, (int) (x / customScale), (int) (y / customScale), scissor);
    }
}
