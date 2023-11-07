package me.wolfii.easynavigator.chat;

import me.wolfii.easynavigator.Config;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;

public class TextTool {
    public static boolean applyCoordinateHighlighting(MutableText parentMessage, int currentIndex, Text text, RegexMatch regexMatch) {
        String message = text.getString();
        int messageLength = message.length();
        if (regexMatch == null) {
            parentMessage.append(text.copy());
            return false;
        }
        if (currentIndex + messageLength < regexMatch.startIndex()) {
            parentMessage.append(text.copy());
            return false;
        }

        int splitStart = Math.max(0, regexMatch.startIndex() - currentIndex);
        int splitEnd = Math.min(regexMatch.endIndex() - currentIndex, messageLength);

        if (splitStart > 0) {
            String prefix = message.substring(0, splitStart);
            parentMessage.append(Text.literal(prefix).setStyle(text.getStyle()));
        }

        Style highlightingStyle =  Style.EMPTY
                .withColor(Config.getConfig().chatHighlightColor.getRGB())
                .withBold(text.getStyle().isBold())
                .withItalic(text.getStyle().isItalic())
                .withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, generateHoverMessage(regexMatch.position()))
        ).withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/easynavigator:navigate %s %s", regexMatch.position().getX(), regexMatch.position().getZ()))
        );
        String highlighted = message.substring(splitStart, splitEnd);
        parentMessage.append(Text.literal(highlighted).setStyle(highlightingStyle));

        if (splitEnd < messageLength) {
            String suffix = message.substring(splitEnd, messageLength);
            parentMessage.append(Text.literal(suffix).setStyle(text.getStyle()));
            return true;
        }
        return false;
    }

    private static Text generateHoverMessage(BlockPos blockPos) {
        return Text.translatable("easynavigator.chat.hovermessage").setStyle(Style.EMPTY.withItalic(true))
                .append(" ")
                .append(Text.literal(String.format("[%s, ~, %s]", blockPos.getX(), blockPos.getZ())).setStyle(Style.EMPTY.withItalic(true).withColor(Config.getConfig().chatHighlightColor.getRGB())));
    }
}
