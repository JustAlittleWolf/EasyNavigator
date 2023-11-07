package me.wolfii.easynavigator.mixin;

import me.wolfii.easynavigator.Config;
import me.wolfii.easynavigator.chat.RegexMatch;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Unique
    private Pattern coordinatePattern = Pattern.compile("-?\\d+([.]\\d+)?[^\\d>]{1,5}-?\\d+([.]\\d+)?([^\\d>]{1,5}-?\\d+([.]\\d+)?)?".replaceAll("5", String.valueOf(Config.getConfig().matchingDistance)));
    @Unique private int lastMatchingDistance = Config.getConfig().matchingDistance;

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private Text checkForCoordinates(Text message) {
        if(Config.getConfig().matchingDistance != lastMatchingDistance) {
            coordinatePattern = Pattern.compile("-?\\d+([.]\\d+)?[^\\d>]{1,5}-?\\d+([.]\\d+)?([^\\d>]{1,5}-?\\d+([.]\\d+)?)?".replaceAll("5", String.valueOf(Config.getConfig().matchingDistance)));
            lastMatchingDistance = Config.getConfig().matchingDistance;
        }
        if(!Config.getConfig().highlightChatMessages) return message;
        String text = message.getString();
        System.out.println(text);
        text = text.replace(',', '.');
        Matcher matcher = coordinatePattern.matcher(text);

        ArrayList<RegexMatch> matches = new ArrayList<>();
        while(matcher.find()) {
            String result = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            matches.add(RegexMatch.of(result, start, end));
        }
        if (matches.isEmpty()) return message;

        return checkMessageRecursive(message, matches, 0);
    }

    @Unique
    private MutableText checkMessageRecursive(Text message, ArrayList<RegexMatch> matches, int currentIndex) {
        System.out.println(message.getString());
        MutableText modifiedMessage = Text.empty();
        if(!message.copyContentOnly().getString().isEmpty()) {
            if(TextTool.applyCoordinateHighlighting(modifiedMessage, currentIndex, message.copyContentOnly().setStyle(message.getStyle()), matches.get(0))) {
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