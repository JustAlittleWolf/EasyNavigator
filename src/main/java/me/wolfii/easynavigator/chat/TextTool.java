package me.wolfii.easynavigator.chat;

import me.wolfii.easynavigator.config.Config;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class TextTool {
    public static Text getMatchMessage(BlockPos blockPos) {
        return Text.literal(" [").formatted(Formatting.DARK_GRAY)
                .append(Text.literal(String.format("%s, ~, %s", blockPos.getX(), blockPos.getZ())).setStyle(
                        Style.EMPTY.withColor(Config.getConfig().chatHighlightColor.getRGB())
                                .withHoverEvent(
                                    new HoverEvent.ShowText(generateHoverMessage(blockPos))
                                ).withClickEvent(
                                    new ClickEvent.RunCommand(String.format("/easynavigator:navigate %s %s", blockPos.getX(), blockPos.getZ()))
                                )
                ))
                .append(Text.literal("]").formatted(Formatting.DARK_GRAY));
    }

    public static Text generateHoverMessage(BlockPos blockPos) {
        return Text.translatable("easynavigator.chat.hovermessage").setStyle(Style.EMPTY.withItalic(true))
                .append(" ")
                .append(Text.literal(String.format("[%s, ~, %s]", blockPos.getX(), blockPos.getZ())).setStyle(Style.EMPTY.withItalic(true).withColor(Config.getConfig().chatHighlightColor.getRGB())));
    }
}
