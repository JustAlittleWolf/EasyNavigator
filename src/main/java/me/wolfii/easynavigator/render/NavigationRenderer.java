package me.wolfii.easynavigator.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.wolfii.easynavigator.EasyNavigator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class NavigationRenderer {
    public static void render(DrawContext drawContext, float tickDelta) {
        if(!EasyNavigator.hasTarget()) return;
        NavigationRenderer.renderItem(drawContext, EasyNavigator.getRenderingPosition().x, EasyNavigator.getRenderingPosition().y, tickDelta, MinecraftClient.getInstance().player, EasyNavigator.getCompassItemStack());
    }

    private static void renderItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            context.drawItem(player, stack, x, y, 0);
        }
    }
}
