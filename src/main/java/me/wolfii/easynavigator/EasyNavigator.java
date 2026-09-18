package me.wolfii.easynavigator;

import me.wolfii.easynavigator.chat.NavigationMessages;
import me.wolfii.easynavigator.config.CompassChangeBehaviour;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class EasyNavigator {
    private static ItemStack COMPASS_ITEM_STACK = null;
    private static BlockPos targetBlockPos = new BlockPos(0, 0, 0);
    private static Vector2i renderingPosition = new Vector2i(0, 0);
    private static boolean hasTarget = false;
    private static boolean playerHasCompass = false;
    private static boolean navigationPaused = false;

    public static void updateCompassNbt() {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (minecraftClient.level != null) {
            ResourceKey<Level> worldKey = minecraftClient.level.dimension();

            BlockPos targetBlockPos = EasyNavigator.targetBlockPos;
            if (EasyNavigator.COMPASS_ITEM_STACK == null) {
                EasyNavigator.COMPASS_ITEM_STACK = new ItemStack(Items.COMPASS);
            }
            ComponentHelper.focusCompassOn(worldKey, targetBlockPos, EasyNavigator.COMPASS_ITEM_STACK);
        }
    }

    public static void updateRenderingPosition() {
        if (!hasTarget) return;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int padding = (int) (4 * Config.getConfig().scale + 2 * Config.getConfig().scale * Config.getConfig().padding);
        int doublePadding = (int) (8 * Config.getConfig().scale + 2 * Config.getConfig().scale * Config.getConfig().padding);
        renderingPosition = switch (Config.getConfig().displayPosition) {
            case BOTTOM_LEFT -> new Vector2i(padding, height - doublePadding);
            case LEFT -> new Vector2i(padding, (int) (height / 2f) - padding / 2);
            case TOP_LEFT -> new Vector2i(padding, padding);
            case TOP -> new Vector2i((int) (width / 2f - 8), padding);
            case TOP_RIGHT -> new Vector2i(width - doublePadding, padding);
            case RIGHT -> new Vector2i(width - doublePadding, (int) (height / 2f) - padding / 2);
            case BOTTOM_RIGHT -> new Vector2i(width - doublePadding, height - doublePadding);
        };
    }

    public static void clearTargetBlockPos() {
        navigationPaused = false;
        hasTarget = false;
        EasyNavigator.COMPASS_ITEM_STACK = null;
    }

    public static ItemStack getCompassItemStack() {
        return COMPASS_ITEM_STACK;
    }

    public static Vector2i getRenderingPosition() {
        return renderingPosition;
    }

    public static BlockPos getTargetBlockPos() {
        return targetBlockPos;
    }

    public static void setTargetBlockPos(@NotNull BlockPos blockPos) {
        targetBlockPos = blockPos;
        hasTarget = true;
        updateCompassNbt();
    }

    public static boolean hasTarget() {
        return hasTarget;
    }

    public static void checkArrival(Minecraft minecraftClient) {
        if (minecraftClient.player == null) return;
        if (!hasTarget) return;
        Vec3 playerPos = minecraftClient.player.position().multiply(1, 0, 1);
        Vec3 targetPos = new Vec3(targetBlockPos.getX() + 0.5, 0, targetBlockPos.getZ() + 0.5);
        double squaredDistanceToTarget = playerPos.distanceToSqr(targetPos);
        if (squaredDistanceToTarget < Config.getConfig().arrivalDistance * Config.getConfig().arrivalDistance) {
            hasTarget = false;
            NavigationMessages.sendMessage(Component.translatable("easynavigator.command.arrived").withStyle(ChatFormatting.WHITE));
        }
    }

    public static void setPlayerHasCompass(boolean value) {
        playerHasCompass = value;
    }

    public static boolean playerHasCompass() {
        MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
        if (interactionManager == null) return playerHasCompass;
        return interactionManager.getPlayerMode() == GameType.CREATIVE || interactionManager.getPlayerMode() == GameType.SPECTATOR || playerHasCompass;
    }

    public static void validateImmersiveMove() {
        if (playerHasCompass() != navigationPaused) return;
        navigationPaused = !navigationPaused;
        if (!hasTarget) return;
        if (Config.getConfig().compassChangeBehaviour == CompassChangeBehaviour.STOP) {
            clearTargetBlockPos();
            if (Config.getConfig().alertOnCompassChange)
                NavigationMessages.sendMessage(Component.translatable("easynavigator.immersivemode.abortnavigating").withStyle(ChatFormatting.WHITE));
            return;
        }
        if (Config.getConfig().alertOnCompassChange) {
            NavigationMessages.sendMessage(Component.translatable("easynavigator.immersivemode." + (navigationPaused ? "pausenavigating" : "resumenavigating")).withStyle(ChatFormatting.WHITE));
        }
    }

    public static boolean isNavigationPaused() {
        return navigationPaused;
    }
}
