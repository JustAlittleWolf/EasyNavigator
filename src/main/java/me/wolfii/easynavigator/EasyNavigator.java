package me.wolfii.easynavigator;

import me.wolfii.easynavigator.chat.NavigationMessages;
import me.wolfii.easynavigator.item.NbtHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class EasyNavigator {
    private static final ItemStack COMPASS_ITEM_STACK = new ItemStack(Items.COMPASS);
    private static BlockPos targetBlockPos = new BlockPos(0, 0, 0);
    private static Vector2i renderingPosition = new Vector2i(0, 0);
    private static boolean hasTarget = false;

    public static void updateCompassNbt() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.world != null) {
            RegistryKey<World> worldKey = minecraftClient.world.getRegistryKey();
            System.out.println(worldKey);
            BlockPos targetBlockPos = EasyNavigator.targetBlockPos;
            NbtCompound nbtCompound = EasyNavigator.COMPASS_ITEM_STACK.getOrCreateNbt();
            NbtHelper.focusCompassOn(worldKey, targetBlockPos, nbtCompound);
        }
    }

    public static void updateRenderingPosition() {
        if(!hasTarget) return;
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int padding = (int) (8 * Config.getConfig().scale) + Config.getConfig().padding;
        int doublePadding = (int) (16 * Config.getConfig().scale) + Config.getConfig().padding;
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

    public static void setTargetBlockPos(@NotNull BlockPos blockPos) {
        targetBlockPos = blockPos;
        hasTarget = true;
        updateCompassNbt();
    }

    public static void clearTargetBlockPos() {
        hasTarget = false;
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

    public static boolean hasTarget() {
        return hasTarget;
    }

    public static void checkArrival(MinecraftClient minecraftClient) {
        if (minecraftClient.player == null) return;
        if (!hasTarget) return;
        Vec3d playerPos = minecraftClient.player.getPos().multiply(1, 0,1);
        Vec3d targetPos = targetBlockPos.toCenterPos().multiply(1, 0, 1);
        double squaredDistanceToTarget = playerPos.squaredDistanceTo(targetPos);
        if (squaredDistanceToTarget < Config.getConfig().arrivalDistance * Config.getConfig().arrivalDistance) {
            hasTarget = false;
            NavigationMessages.sendMessage(Text.translatable("easynavigator.command.arrived").formatted(Formatting.WHITE));
        }
    }
}
