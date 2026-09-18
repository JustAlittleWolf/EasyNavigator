package me.wolfii.easynavigator.chat;

import me.wolfii.easynavigator.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NavigationMessages {
    private static final MutableComponent prefix = Component.translatable("easynavigator.prefix").withStyle(ChatFormatting.YELLOW).append(Component.literal(": ").withStyle(ChatFormatting.YELLOW));
    public static void sendMessage(MutableComponent message) {
        if (!Config.getConfig().navigatorMessages) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;

        message = prefix.copy().append(message);
        player.displayClientMessage(message, false);
    }
}
