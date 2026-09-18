package me.wolfii.easynavigator.client;

import me.wolfii.easynavigator.EasyNavigator;
import me.wolfii.easynavigator.chat.NavigationMessages;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import me.wolfii.easynavigator.render.NavigationCompassHudElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EasyNavigatorClient implements ClientModInitializer {
    private double lastCoordinateScale = 1.0;
    private int time = 0;
    private int dimensionSwitchTimeout = 0;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath("easynavigator", "navigation_compass"), new NavigationCompassHudElement());

        ClientPlayConnectionEvents.JOIN.register(this::onWorldJoin);

        ClientPlayConnectionEvents.DISCONNECT.register(this::onWorldLeave);

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        ClientCommandRegistrationCallback.EVENT.register(CommandManager::registerCommand);

        EasyNavigatorComponentData.register();
    }

    private void onClientTick(Minecraft minecraftClient) {
        time++;

        if (Config.getConfig().immersiveMode && time % Config.compassCheckInterval == 0) {
            checkForCompass(minecraftClient);
            EasyNavigator.validateImmersiveMove();
        }
        if (Config.getConfig().immersiveMode && EasyNavigator.isNavigationPaused()) return;

        if (checkDimension(minecraftClient)) dimensionSwitchTimeout = time + 100;

        EasyNavigator.updateRenderingPosition();
        if (time >= dimensionSwitchTimeout) EasyNavigator.checkArrival(minecraftClient);
    }

    private void checkForCompass(Minecraft minecraftClient) {
        if (minecraftClient.player == null) return;
        for (ItemStack itemStack : minecraftClient.player.getInventory().getNonEquipmentItems()) {
            Item item = itemStack.getItem();
            if (!(item instanceof CompassItem)) continue;
            EasyNavigator.setPlayerHasCompass(true);
            return;
        }
        EasyNavigator.setPlayerHasCompass(false);
    }

    private boolean checkDimension(Minecraft minecraftClient) {
        if (minecraftClient.level == null) return false;
        double coordinateScale = minecraftClient.level.dimensionType().coordinateScale();
        if (coordinateScale == lastCoordinateScale) return false;
        if (!EasyNavigator.hasTarget()) {
            lastCoordinateScale = coordinateScale;
            return false;
        }
        if (!Config.getConfig().convertNetherCoordinates) return false;
        this.onDimensionScaleChange(coordinateScale);
        return true;
    }


    private void onDimensionScaleChange(double coordinateScale) {
        BlockPos oldBlockPos = EasyNavigator.getTargetBlockPos();
        double scaleChange = (lastCoordinateScale / coordinateScale);
        BlockPos newBlockPos = new BlockPos((int) (oldBlockPos.getX() * scaleChange), (int) (oldBlockPos.getY() * scaleChange), (int) (oldBlockPos.getZ() * scaleChange));
        EasyNavigator.setTargetBlockPos(newBlockPos);
        lastCoordinateScale = coordinateScale;

        NavigationMessages.sendMessage(
            Component.translatable("easynavigator.command.converted").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(" "))
                .append(Component.literal(String.format("[%s, ~, %s]", newBlockPos.getX(), newBlockPos.getZ())).withStyle(ChatFormatting.GREEN))
        );
    }

    private void onWorldLeave(ClientPacketListener clientPlayNetworkHandler, Minecraft minecraftClient) {
        EasyNavigator.clearTargetBlockPos();
    }

    private void onWorldJoin(ClientPacketListener clientPlayNetworkHandler, PacketSender packetSender, Minecraft minecraftClient) {
        EasyNavigator.updateCompassNbt();
        if (minecraftClient.level == null) return;
        lastCoordinateScale = minecraftClient.level.dimensionType().coordinateScale();
    }
}
