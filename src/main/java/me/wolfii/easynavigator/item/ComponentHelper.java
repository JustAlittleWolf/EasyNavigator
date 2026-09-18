package me.wolfii.easynavigator.item;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;

public class ComponentHelper {
    public static void focusCompassOn(ResourceKey<Level> worldKey, BlockPos pos, ItemStack compass) {
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(new GlobalPos(worldKey, pos)), true));
        compass.set(EasyNavigatorComponentData.REMOVE_ENCHANTMENT_GLINT, true);
        compass.set(EasyNavigatorComponentData.CUSTOM_SCALE, true);
    }
}
