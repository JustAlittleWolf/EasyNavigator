package me.wolfii.easynavigator.chat;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public record RegexMatch(BlockPos position, int startIndex, int endIndex) {

    public static RegexMatch of(String position, int startIndex, int endIndex) {
        String cleanedPosition = position.replaceAll("[^0-9.]+", " ").trim();
        ArrayList<Double> numbers = new ArrayList<>();
        for (String number : cleanedPosition.split(" ")) {
            numbers.add(Double.parseDouble(number));
        }
        BlockPos blockPos = new BlockPos(numbers.get(0).intValue(), 0, numbers.get(numbers.size() - 1).intValue());
        return new RegexMatch(blockPos, startIndex, endIndex);
    }
}
