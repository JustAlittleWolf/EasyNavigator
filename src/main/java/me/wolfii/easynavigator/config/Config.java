package me.wolfii.easynavigator.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.wolfii.easynavigator.render.CompassPosition;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.awt.*;

public class Config {
    private static final ConfigClassHandler<Config> configInstance = ConfigClassHandler.createBuilder(Config.class)
            .id(new Identifier("easynavigator"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("easynavigator.json"))
                    .build())
            .build();

    static {
        configInstance.load();
    }

    public static Config getConfig() {
        return Config.configInstance.instance();
    }

    public static Screen createScreen(Screen parent) {
        return Config.configInstance.generateGui().generateScreen(parent);
    }

    @AutoGen(category = "navigator")
    @Boolean(formatter = Boolean.Formatter.ON_OFF)
    @SerialEntry
    public boolean convertNetherCoordinates = true;
    @AutoGen(category = "navigator")
    @FloatSlider(min = 0.5f, max = 4.0f, step = 0.1f)
    @SerialEntry
    public float scale = 2.0f;
    @AutoGen(category = "navigator")
    @EnumCycler
    @SerialEntry
    public CompassPosition displayPosition = CompassPosition.TOP;
    @AutoGen(category = "navigator")
    @IntSlider(min = -4, max = 16, step = 1)
    @SerialEntry
    public int padding = 0;
    @AutoGen(category = "navigator")
    @IntSlider(min = 0, max = 100, step = 1)
    @SerialEntry
    public int arrivalDistance = 7;
    @AutoGen(category = "chat")
    @Boolean(formatter = Boolean.Formatter.ON_OFF)
    @SerialEntry
    public boolean navigatorMessages = true;
    @AutoGen(category = "chat")
    @Boolean(formatter = Boolean.Formatter.ON_OFF)
    @SerialEntry
    public boolean highlightChatMessages = true;
    @AutoGen(category = "chat")
    @ColorField
    @SerialEntry
    public Color chatHighlightColor = new Color(85, 255, 85);
    @AutoGen(category = "chat")
    @IntSlider(min = 1, max = 9, step = 1)
    @SerialEntry
    public int matchingDistance = 5;
    @AutoGen(category = "survival")
    @MasterTickBox({"compassChangeBehaviour", "alertOnCompassChange"})
    @SerialEntry
    public boolean immersiveMode = false;
    @AutoGen(category = "survival")
    @EnumCycler
    @SerialEntry
    public CompassChangeBehaviour compassChangeBehaviour = CompassChangeBehaviour.STOP;
    @AutoGen(category = "survival")
    @Boolean(formatter = Boolean.Formatter.ON_OFF)
    @SerialEntry
    public boolean alertOnCompassChange = false;

    public static int compassCheckInterval = 5;
}
