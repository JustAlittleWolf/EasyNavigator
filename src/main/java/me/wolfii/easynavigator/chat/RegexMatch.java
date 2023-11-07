package me.wolfii.easynavigator.chat;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RegexMatch(BlockPos position, int startIndex, int endIndex) {

    public static RegexMatch of(String text, int startIndex, int endIndex) {
        Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d)*\\d*");
        Matcher numberMatcher = numberPattern.matcher(text);
        ArrayList<Double> numbers = new ArrayList<>();
        while(numberMatcher.find()) {
            numbers.add(Double.parseDouble(numberMatcher.group()));
        }
        BlockPos blockPos = new BlockPos(numbers.get(0).intValue(), 0, numbers.get(numbers.size() - 1).intValue());
        return new RegexMatch(blockPos, startIndex, endIndex);
    }
}
