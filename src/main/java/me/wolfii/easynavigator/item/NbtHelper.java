package me.wolfii.easynavigator.item;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NbtHelper {
    public static void focusCompassOn(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
        System.out.println(pos);
        nbt.put("LodestonePos", net.minecraft.nbt.NbtHelper.fromBlockPos(pos));
        DataResult<NbtElement> var10000 = World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey);
        var10000.result().ifPresent((nbtElement) -> {
            nbt.put("LodestoneDimension", nbtElement);
        });
        nbt.putBoolean("LodestoneTracked", true);
        nbt.putBoolean("RemoveEnchantmentGlint", true);
        nbt.putBoolean("CustomScale", true);
    }
}
