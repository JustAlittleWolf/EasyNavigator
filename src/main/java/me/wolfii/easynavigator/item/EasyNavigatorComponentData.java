package me.wolfii.easynavigator.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EasyNavigatorComponentData {
    public static final ComponentType<Boolean> CUSTOM_SCALE = ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN).build();
    public static final ComponentType<Boolean> REMOVE_ENCHANTMENT_GLINT = ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN).build();

    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("easynavigator", "custom_scale"), CUSTOM_SCALE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("easynavigator", "remove_enchantment_glint"), REMOVE_ENCHANTMENT_GLINT);
    }
}
