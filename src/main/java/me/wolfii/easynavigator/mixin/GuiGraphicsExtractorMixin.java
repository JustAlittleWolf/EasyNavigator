package me.wolfii.easynavigator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {
    @Shadow
    @Final
    public GuiGraphicsExtractor.ScissorStack scissorStack;
    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @WrapOperation(
        method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;addItem(Lnet/minecraft/client/renderer/state/gui/GuiItemRenderState;)V"
        )
    )
    private void scaleItemRendering(
        GuiRenderState instance,
        GuiItemRenderState itemState,
        Operation<Void> original,
        @Local(argsOnly = true, name = "itemStack") ItemStack itemStack,
        @Local(argsOnly = true, name = "x") int x,
        @Local(argsOnly = true, name = "y") int y,
        @Local(name = "itemStackRenderState") TrackingItemStackRenderState itemStackRenderState
    ) {
        if (!itemStack.getComponents().getOrDefault(EasyNavigatorComponentData.CUSTOM_SCALE, false)) {
            original.call(instance, itemState);
            return;
        }
        float customScale = Config.getConfig().scale;
        original.call(instance, new GuiItemRenderState(
            this.pose.scale(customScale, new Matrix3x2f()),
            itemStackRenderState,
            (int) (x / customScale),
            (int) (y / customScale),
            this.scissorStack.peek()
        ));
    }
}
