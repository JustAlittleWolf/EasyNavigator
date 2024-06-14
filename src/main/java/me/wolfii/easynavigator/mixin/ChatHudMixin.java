package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import org.intellij.lang.annotations.RegExp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Unique
    @RegExp
    String pattern = "(?<=(^|[^.]))(-?\\b\\d+\\.?\\d*\\b[^.()\\d\\n:-][^()\\d\\n]{0,5}-?\\b\\d+\\.?\\d*\\b[^.()\\d\\n:-][^()\\d\\n]{0,5}-?\\b\\d+\\.?\\d*\\b|-?\\b\\d+\\.?\\d*\\b[^.()\\d\\n:-][^()\\d\\n]{0,5}-?\\b\\d+\\.?\\d*\\b)";
    @Unique
    private Pattern coordinatePattern;
    @Unique
    private int lastMatchingDistance = -1;

    /**
     * Checks the incoming message foor coordinates and if there are any, it makes them clickable.
     */
    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"))
    private ChatHudLine checkForCoordinates(ChatHudLine message) {
        if (Config.getConfig().matchingDistance != lastMatchingDistance) {
            coordinatePattern = Pattern.compile(pattern.replaceAll("5", String.valueOf(Config.getConfig().matchingDistance - 1)));
            lastMatchingDistance = Config.getConfig().matchingDistance;
        }
        if (!Config.getConfig().highlightChatMessages) return message;
        String text = sanitizeMessage(message.content().getString());
        if (text.startsWith(Text.translatable("easynavigator.prefix").getString())) return message;
        Matcher matcher = coordinatePattern.matcher(text);

        MutableText newMessage = message.content().copy();
        while (matcher.find()) {
            String result = matcher.group();

            BlockPos matchPos = blockPosFromMatch(result);
            newMessage.append(TextTool.getMatchMessage(matchPos));
        }
        return new ChatHudLine(message.creationTick(), newMessage, message.signature(), message.indicator());
    }

    @Unique
    private BlockPos blockPosFromMatch(String match) {
        Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d)*\\d*");
        Matcher numberMatcher = numberPattern.matcher(match);
        ArrayList<Double> numbers = new ArrayList<>();
        while (numberMatcher.find()) {
            numbers.add(Double.parseDouble(numberMatcher.group()));
        }
        return new BlockPos(numbers.getFirst().intValue(), 0, numbers.getLast().intValue());
    }

    @Unique
    private String sanitizeMessage(String message) {
        message = message.replaceAll("(?<=\\d),(?=\\d)", ".");
        message = message.replaceAll("§.", "");
        return message;
    }
}