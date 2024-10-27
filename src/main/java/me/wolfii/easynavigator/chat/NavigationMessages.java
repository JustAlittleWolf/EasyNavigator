package me.wolfii.easynavigator.chat;

import me.wolfii.easynavigator.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NavigationMessages {
    private static final MutableText prefix = Text.translatable("easynavigator.prefix").formatted(Formatting.YELLOW).append(Text.literal(": ").formatted(Formatting.YELLOW));
    public static void sendMessage(MutableText message) {
        if (!Config.getConfig().navigatorMessages) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        message = prefix.copy().append(message);
        player.sendMessage(message, false);
    }
}
