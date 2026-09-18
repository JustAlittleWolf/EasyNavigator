package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.text.*;
import org.intellij.lang.annotations.RegExp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatComponent.class)
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
    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V"))
    private GuiMessage checkForCoordinates(GuiMessage message) {
        if (Config.getConfig().matchingDistance != lastMatchingDistance) {
            coordinatePattern = Pattern.compile(pattern.replaceAll("5", String.valueOf(Config.getConfig().matchingDistance - 1)));
            lastMatchingDistance = Config.getConfig().matchingDistance;
        }
        if (!Config.getConfig().highlightChatMessages) return message;
        String text = sanitizeMessage(message.content().getString());
        if (text.startsWith(Component.translatable("easynavigator.prefix").getString())) return message;
        Matcher matcher = coordinatePattern.matcher(text);

        MutableComponent newMessage = message.content().copy();
        while (matcher.find()) {
            String result = matcher.group();

            BlockPos matchPos = blockPosFromMatch(result);
            newMessage.append(TextTool.getMatchMessage(matchPos));
        }
        return new GuiMessage(message.addedTime(), newMessage, message.signature(), message.tag());
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