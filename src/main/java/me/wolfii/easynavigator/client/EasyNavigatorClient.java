package me.wolfii.easynavigator.client;

import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.EasyNavigator;
import me.wolfii.easynavigator.chat.NavigationMessages;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import me.wolfii.easynavigator.mixin.PlayerInventoryAccessor;
import me.wolfii.easynavigator.render.NavigationRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class EasyNavigatorClient implements ClientModInitializer {
    private double lastCoordinateScale = 1.0;
    private int time = 0;
    private int dimensionSwitchTimeout = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(NavigationRenderer::render);

        ClientPlayConnectionEvents.JOIN.register(this::onWorldJoin);

        ClientPlayConnectionEvents.DISCONNECT.register(this::onWorldLeave);

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        ClientCommandRegistrationCallback.EVENT.register(CommandManager::registerCommand);

        EasyNavigatorComponentData.register();
    }

    private void onClientTick(MinecraftClient minecraftClient) {
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

    private void checkForCompass(MinecraftClient minecraftClient) {
        if (minecraftClient.player == null) return;
        for (DefaultedList<ItemStack> defaultedList : ((PlayerInventoryAccessor) minecraftClient.player.getInventory()).getCombinedInventory()) {
            for (ItemStack itemStack : defaultedList) {
                Item item = itemStack.getItem();
                if (!(item instanceof CompassItem)) continue;
                EasyNavigator.setPlayerHasCompass(true);
                return;
            }
        }
        EasyNavigator.setPlayerHasCompass(false);
    }

    private boolean checkDimension(MinecraftClient minecraftClient) {
        if (minecraftClient.world == null) return false;
        double coordinateScale = minecraftClient.world.getDimension().coordinateScale();
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
                Text.translatable("easynavigator.command.converted").formatted(Formatting.WHITE)
                        .append(Text.literal(" "))
                        .append(Text.literal(String.format("[%s, ~, %s]", newBlockPos.getX(), newBlockPos.getZ())).formatted(Formatting.GREEN))
        );
    }

    private void onWorldLeave(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        EasyNavigator.clearTargetBlockPos();
    }

    private void onWorldJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        EasyNavigator.updateCompassNbt();
        if (minecraftClient.world == null) return;
        lastCoordinateScale = minecraftClient.world.getDimension().coordinateScale();
    }
}
