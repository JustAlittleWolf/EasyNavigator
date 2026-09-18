package me.wolfii.easynavigator.render;

import me.wolfii.easynavigator.config.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import me.wolfii.easynavigator.EasyNavigator;

public class NavigationRenderer {
    public static void render(GuiGraphics drawContext, DeltaTracker ignored) {
        if (!EasyNavigator.hasTarget()) return;
        if (Config.getConfig().immersiveMode && EasyNavigator.isNavigationPaused()) return;
        NavigationRenderer.renderItem(drawContext, EasyNavigator.getRenderingPosition().x, EasyNavigator.getRenderingPosition().y, Minecraft.getInstance().player, EasyNavigator.getCompassItemStack());
    }

    private static void renderItem(GuiGraphics context, int x, int y, Player player, ItemStack stack) {
        if (!stack.isEmpty()) {
            context.renderItem(player, stack, x, y, 0);
        }
    }
}
