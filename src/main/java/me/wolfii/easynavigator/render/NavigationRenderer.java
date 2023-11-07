package me.wolfii.easynavigator.render;

import me.wolfii.easynavigator.EasyNavigator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

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
