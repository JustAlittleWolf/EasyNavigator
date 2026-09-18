package me.wolfii.easynavigator.render;

import me.wolfii.easynavigator.EasyNavigator;
import me.wolfii.easynavigator.config.Config;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class NavigationCompassHudElement implements HudElement {
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker ignored) {
        if (!EasyNavigator.hasTarget()) return;
        if (Config.getConfig().immersiveMode && EasyNavigator.isNavigationPaused()) return;
        ItemStack itemStack = EasyNavigator.getCompassItemStack();
        if (itemStack != null && !itemStack.isEmpty()) {
            graphics.item(
                Objects.requireNonNull(Minecraft.getInstance().player),
                itemStack,
                EasyNavigator.getRenderingPosition().x,
                EasyNavigator.getRenderingPosition().y,
                0
            );
        }
    }
}
