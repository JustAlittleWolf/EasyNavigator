package me.wolfii.easynavigator;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.wolfii.easynavigator.render.CompassPosition;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.sql.SQLSyntaxErrorException;

public class Config {
    private static final ConfigClassHandler<Config> configInstance = ConfigClassHandler.createBuilder(Config.class)
            .id(new Identifier("easynavigator"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("easynavigator.json"))
                    .setJson5(true)
                    .build())
            .build();

    public static Config getConfig() {
        return Config.configInstance.instance();
    }

    public static Screen createScreen(Screen parent) {
        return Config.configInstance.generateGui().generateScreen(parent);
    }

    @AutoGen(category = "settings")
    @Boolean(formatter = Boolean.Formatter.ON_OFF)
    @SerialEntry
    public boolean convertNetherCoordinates = true;
    @AutoGen(category = "settings")
    @FloatSlider(min = 0.5f, max = 4.0f, step = 0.1f)
    @SerialEntry
    public float scale = 2.0f;
    @AutoGen(category = "settings")
    @EnumCycler
    @SerialEntry
    public CompassPosition displayPosition = CompassPosition.TOP;
    @AutoGen(category = "settings")
    @IntSlider(min = -4, max = 16, step = 1)
    @SerialEntry
    public int padding = 0;
    @AutoGen(category = "settings")
    @IntSlider(min = 0, max = 100, step = 1)
    @SerialEntry
    public int arrivalDistance = 7;
}
