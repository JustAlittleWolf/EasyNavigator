package me.wolfii.easynavigator.chat;

import me.wolfii.easynavigator.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.text.*;

public class TextTool {
    public static Component getMatchMessage(BlockPos blockPos) {
        return Component.literal(" [").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.format("%s, ~, %s", blockPos.getX(), blockPos.getZ())).setStyle(
                        Style.EMPTY.withColor(Config.getConfig().chatHighlightColor.getRGB())
                                .withHoverEvent(
                                    new HoverEvent.ShowText(generateHoverMessage(blockPos))
                                ).withClickEvent(
                                    new ClickEvent.RunCommand(String.format("/easynavigator:navigate %s %s", blockPos.getX(), blockPos.getZ()))
                                )
                ))
                .append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Component generateHoverMessage(BlockPos blockPos) {
        return Component.translatable("easynavigator.chat.hovermessage").setStyle(Style.EMPTY.withItalic(true))
                .append(" ")
                .append(Component.literal(String.format("[%s, ~, %s]", blockPos.getX(), blockPos.getZ())).setStyle(Style.EMPTY.withItalic(true).withColor(Config.getConfig().chatHighlightColor.getRGB())));
    }
}
