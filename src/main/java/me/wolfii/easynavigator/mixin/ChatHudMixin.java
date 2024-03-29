package me.wolfii.easynavigator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.wolfii.easynavigator.chat.NavigationMessages;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.intellij.lang.annotations.RegExp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Unique
    @RegExp
    String pattern = "(?<=(^|[^.]))(-?\\b\\d[\\d]*\\.?[\\d]*\\b[^.()\\d\\n:][^()\\d\\n]{0,5}-?\\b\\d[\\d]*\\.?[\\d]*\\b[^.()\\d\\n:][^()\\d\\n]{0,5}-?\\b\\d[\\d]*\\.?[\\d]*\\b|-?\\b\\d[\\d]*\\.?[\\d]*\\b[^.()\\d\\n:][^()\\d\\n]{0,5}-?\\b\\d[\\d]*\\.?[\\d]*\\b)";
    @Unique
    private Pattern coordinatePattern;
    @Unique
    private int lastMatchingDistance = -1;

    /**
     * Checks the incoming message foor coordinates and if there are any, it makes them clickable.
     * The Redirect is at such a weird position to avoid conflicts with chatpatches
     */
    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private List<OrderedText> checkForCoordinates(StringVisitable stringVisitable, int width, TextRenderer textRenderer, @Local(argsOnly = true) boolean refresh) {
        if (refresh) return ChatMessages.breakRenderedChatMessageLines(stringVisitable, width, textRenderer);
        MutableText message = (MutableText) stringVisitable;
        if (Config.getConfig().matchingDistance != lastMatchingDistance) {
            coordinatePattern = Pattern.compile(pattern.replaceAll("5", String.valueOf(Config.getConfig().matchingDistance - 1)));
            lastMatchingDistance = Config.getConfig().matchingDistance;
        }
        if (!Config.getConfig().highlightChatMessages)
            return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
        String text = sanitizeMessage(message.getString());
        if (text.startsWith(Text.translatable("easynavigator.prefix").getString()))
            return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
        Matcher matcher = coordinatePattern.matcher(text);

        while (matcher.find()) {
            String result = matcher.group();

            BlockPos matchPos = blockPosFromMatch(result);
            message.append(TextTool.getMatchMessage(matchPos));
        }
        return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
    }

    @Unique
    private BlockPos blockPosFromMatch(String match) {
        Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d)*\\d*");
        Matcher numberMatcher = numberPattern.matcher(match);
        ArrayList<Double> numbers = new ArrayList<>();
        while (numberMatcher.find()) {
            numbers.add(Double.parseDouble(numberMatcher.group()));
        }
        return new BlockPos(numbers.get(0).intValue(), 0, numbers.get(numbers.size() - 1).intValue());
    }

    @Unique
    private String sanitizeMessage(String message) {
        message = message.replaceAll("(?<=\\d),(?=\\d)", ".");
        message = message.replaceAll("§.", "");
        return message;
    }
}