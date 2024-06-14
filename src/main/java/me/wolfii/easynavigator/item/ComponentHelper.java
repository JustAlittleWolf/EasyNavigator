package me.wolfii.easynavigator.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ComponentHelper {
    public static void focusCompassOn(RegistryKey<World> worldKey, BlockPos pos, ItemStack compass) {
        compass.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(new GlobalPos(worldKey, pos)), true));
        compass.set(EasyNavigatorComponentData.REMOVE_ENCHANTMENT_GLINT, true);
        compass.set(EasyNavigatorComponentData.CUSTOM_SCALE, true);

    }
}
