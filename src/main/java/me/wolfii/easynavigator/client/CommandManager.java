package me.wolfii.easynavigator.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.wolfii.easynavigator.chat.TextTool;
import me.wolfii.easynavigator.config.Config;
import me.wolfii.easynavigator.EasyNavigator;
import me.wolfii.easynavigator.chat.NavigationMessages;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.phys.Vec3;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CommandManager {
    public static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext ignoredRegistryAccess) {
        final LiteralCommandNode<FabricClientCommandSource> navigateNode = dispatcher.register(ClientCommandManager.literal("navigate")
                .then(ClientCommandManager.argument("blockpos", new NavigationArgumentType())
                        .executes((context) -> {
                            if (Config.getConfig().immersiveMode && !EasyNavigator.playerHasCompass()) {
                                NavigationMessages.sendMessage(Component.translatable("easynavigator.immersivemode.cannotstart").withStyle(ChatFormatting.WHITE));
                                return 1;
                            }

                            Coordinates posArgument = context.getArgument("blockpos", Coordinates.class);
                            FabricClientCommandSource commandSource = context.getSource();
                            CommandSourceStack serverCommandSource = new CommandSourceStack(null, commandSource.getPosition(), commandSource.getRotation(), null, PermissionSet.ALL_PERMISSIONS, null, null, null, commandSource.getEntity());

                            BlockPos blockPos = posArgument.getBlockPos(serverCommandSource);
                            if (Minecraft.getInstance().player != null) {
                                Vec3 playerPos = Minecraft.getInstance().player.position().multiply(1, 0, 1);
                                Vec3 targetPos = blockPos.getCenter().multiply(1, 0, 1);
                                double squaredDistanceToTarget = playerPos.distanceToSqr(targetPos);
                                if (squaredDistanceToTarget < Config.getConfig().arrivalDistance * Config.getConfig().arrivalDistance) {
                                    NavigationMessages.sendMessage(Component.translatable("easynavigator.command.alreadyattarget").withStyle(ChatFormatting.WHITE));
                                    return 1;
                                }
                            }

                            EasyNavigator.setTargetBlockPos(blockPos);
                            NavigationMessages.sendMessage(
                                    Component.translatable("easynavigator.command.navigating").withStyle(ChatFormatting.WHITE)
                                            .append(Component.literal(" "))
                                            .append(Component.literal(String.format("[%s, ~, %s]", blockPos.getX(), blockPos.getZ())).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                                                    .withHoverEvent(
                                                        new HoverEvent.ShowText(TextTool.generateHoverMessage(blockPos))
                                                    ).withClickEvent(
                                                        new ClickEvent.RunCommand(String.format("/easynavigator:navigate %s %s", blockPos.getX(), blockPos.getZ()))
                                                    )))
                            );
                            return 1;
                        })));
        dispatcher.register(ClientCommandManager.literal("easynavigator:navigate").redirect(navigateNode));
        final LiteralCommandNode<FabricClientCommandSource> navigationNode = dispatcher.register(ClientCommandManager.literal("navigation")
                .then(ClientCommandManager.literal("stop")
                        .executes((context) -> {
                            EasyNavigator.clearTargetBlockPos();
                            NavigationMessages.sendMessage(
                                    Component.translatable("easynavigator.command.stopnavigating").withStyle(ChatFormatting.WHITE)
                            );
                            return 1;
                        })));
        dispatcher.register(ClientCommandManager.literal("easynavigator:navigation").redirect(navigationNode));
    }

    private static class NavigationArgumentType implements ArgumentType<Coordinates> {
        private final Vec2Argument vec2ArgumentType = Vec2Argument.vec2();
        private final Vec3Argument vec3ArgumentType = Vec3Argument.vec3();

        @Override
        public Coordinates parse(StringReader reader) throws CommandSyntaxException {
            try {
                return vec3ArgumentType.parse(reader);
            } catch (Exception ignored) {
            }
            try {
                return vec2ArgumentType.parse(reader);
            } catch (Exception ignored) {
            }
            throw new SimpleCommandExceptionType(Component.translatable("easynavigator.navigationargumenttype.error")).createWithContext(reader);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            String remaining = builder.getRemaining();
            if (remaining.indexOf(' ') == -1 || remaining.lastIndexOf(' ') == remaining.indexOf(' ')) {
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
