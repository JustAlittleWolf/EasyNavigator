package me.wolfii.easynavigator.mixin;

import com.mojang.datafixers.TypeRewriteRule;
import me.wolfii.easynavigator.Config;
import me.wolfii.easynavigator.chat.RegexMatch;
import me.wolfii.easynavigator.chat.TextTool;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Unique
    private final Pattern regexMatcher = Pattern.compile("\\d+([.]\\d+)?[^\\d>]{1,3}\\d+([.]\\d+)?([^\\d>]{1,3}\\d+([.]\\d+)?)?");

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private Text checkForCoordinates(Text message) {
        if(!Config.getConfig().highlightChatMessages) return message;
        String text = message.getString();
        text = text.replace(',', '.');
        Matcher matcher = regexMatcher.matcher(text);

        ArrayList<RegexMatch> matches = new ArrayList<>();
        while(matcher.find()) {
            String result = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            matches.add(RegexMatch.of(result, start, end));
        }
        if (matches.isEmpty()) return message;
        MutableText modifiedMessage = Text.empty();

        if(message.getSiblings().isEmpty()) {
            TextTool.applyCoordinateHighlighting(modifiedMessage, 0, message, matches.get(0));
            return modifiedMessage;
        }

        int currentIndex = 0;
        for (Text subtext : message.getSiblings()) {

            RegexMatch regexMatch = matches.isEmpty() ? null : matches.get(0);
            if(TextTool.applyCoordinateHighlighting(modifiedMessage, currentIndex, subtext, regexMatch)) {
                matches.remove(0);
            }

            currentIndex += subtext.getString().length();
        }

        return modifiedMessage;
    }
}