package me.wolfii.easynavigator.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wolfii.easynavigator.EasyNavigator;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CommandManager {
    public static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("navigate")
                .then(ClientCommandManager.argument("blockpos", new NavigationArgumentType())
                .executes((context) -> {
                    PosArgument posArgument = context.getArgument("blockpos", PosArgument.class);
                    FabricClientCommandSource commandSource = context.getSource();
                    ServerCommandSource serverCommandSource = new ServerCommandSource(null, commandSource.getPosition(), commandSource.getRotation(), null, 0, null, null, null, commandSource.getEntity());

                    BlockPos blockPos = posArgument.toAbsoluteBlockPos(serverCommandSource);
                    EasyNavigator.setTargetBlockPos(blockPos);
                    NavigationMessages.sendMessage(
                            Text.translatable("easynavigator.command.navigating").formatted(Formatting.WHITE)
                            .append(Text.literal(" "))
                            .append(Text.literal(String.format("[%s, ~, %s]", blockPos.getX(), blockPos.getZ())).formatted(Formatting.GREEN))
                    );
                    return 1;
                })));
    }

    private static class NavigationArgumentType implements ArgumentType<PosArgument> {
        private final Vec2ArgumentType vec2ArgumentType = Vec2ArgumentType.vec2();
        private final Vec3ArgumentType vec3ArgumentType = Vec3ArgumentType.vec3();
        @Override
        public PosArgument parse(StringReader reader) throws CommandSyntaxException {
            try {
                return vec3ArgumentType.parse(reader);
            } catch (Exception ignored) {}
            try {
                return vec2ArgumentType.parse(reader);
            } catch (Exception ignored) {}
            throw new SimpleCommandExceptionType(Text.translatable("navigationargumenttype.error")).createWithContext(reader);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            String remaining = builder.getRemaining();
            if(remaining.indexOf(' ') == -1 || remaining.lastIndexOf(' ') == remaining.indexOf(' ')) {
                return vec2ArgumentType.listSuggestions(context, builder);
            }
            return vec3ArgumentType.listSuggestions(context, builder);
        }

        @Override
        public Collection<String> getExamples() {
            Collection<String> examples = vec2ArgumentType.getExamples();
            examples.addAll(vec3ArgumentType.getExamples());
            return examples;
        }
    }
}
