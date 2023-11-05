package me.wolfii.easynavigator.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NavigationMessages {
    private static final MutableText prefix = Text.translatable("easynavigator.prefix").formatted(Formatting.YELLOW).append(Text.literal(": ").formatted(Formatting.YELLOW));
    public static void sendMessage(MutableText message) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        message = prefix.copy().append(message);
        player.sendMessage(message);
    }
}
