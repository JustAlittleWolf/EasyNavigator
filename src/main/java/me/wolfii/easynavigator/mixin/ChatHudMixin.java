package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.Config;
import me.wolfii.easynavigator.chat.RegexMatch;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Unique
    @RegExp
    String pattern = "-?\\d+[.]?\\d*[^.<>()][^\\d<>()]{1,5}-?\\d+[.]?\\d*([^.<>()][^\\d<>()]{1,5}-?\\d+[.]?\\d*)?";
    @Unique
    private Pattern coordinatePattern;
    @Unique
    private int lastMatchingDistance = -1;

    /**
     * Checks the incoming message foor coordinates and if there are any, it makes them clickable.
     * The Redirect is at such a weird position to avoid conflicts with chatpatches
     */
    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private List<OrderedText> checkForCoordinates(StringVisitable stringVisitable, int width, TextRenderer textRenderer) {
        Text message = (Text) stringVisitable;
        if (Config.getConfig().matchingDistance != lastMatchingDistance) {
            coordinatePattern = Pattern.compile(pattern.replaceAll("5", String.valueOf(Config.getConfig().matchingDistance - 1)));
            lastMatchingDistance = Config.getConfig().matchingDistance;
        }
        if (!Config.getConfig().highlightChatMessages)
            return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
        String text = message.getString();
        text = text.replace(',', '.');
        text = text.replaceAll("§.", "");
        Matcher matcher = coordinatePattern.matcher(text);

        ArrayList<RegexMatch> matches = new ArrayList<>();
        while (matcher.find()) {
            String result = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            matches.add(RegexMatch.of(result, start, end));
        }
        if (matches.isEmpty()) return ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
        return ChatMessages.breakRenderedChatMessageLines(checkMessageRecursive(message, matches, 0), width, textRenderer);
    }

    @Unique
    private MutableText checkMessageRecursive(Text message, ArrayList<RegexMatch> matches, int currentIndex) {
        MutableText modifiedMessage = Text.empty();
        RegexMatch regexMatch = matches.isEmpty() ? null : matches.get(0);
        if (!message.copyContentOnly().getString().isEmpty()) {
            if (TextTool.applyCoordinateHighlighting(modifiedMessage, currentIndex, message.copyContentOnly().setStyle(message.getStyle()), regexMatch)) {
                matches.remove(0);
            }
            currentIndex += message.copyContentOnly().getString().length();
        }

        for (Text subtext : message.getSiblings()) {
            modifiedMessage.append(checkMessageRecursive(subtext, matches, currentIndex));
            currentIndex += subtext.getString().length();
        }

        return modifiedMessage;
    }
}