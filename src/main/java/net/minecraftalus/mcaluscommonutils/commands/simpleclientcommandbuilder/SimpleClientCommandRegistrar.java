package net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraftalus.mcaluscommonutils.MCAlusCommonUtils;
import net.minecraftalus.mcaluscommonutils.commands.argument.CommandArgument;

import java.util.HashSet;
import java.util.Set;

public class SimpleClientCommandRegistrar {

    private static final Set<String> REGISTERED_COMMANDS = new HashSet<>();

    public static void initialize() {
        ClassLoader classLoader = SimpleClientCommandBuilder.class.getClassLoader();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .overrideClassLoaders(classLoader)
                .scan()) {

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoBuildSimpleClientCommand.class.getName())
                    .filter(classInfo -> classInfo.implementsInterface(SimpleClientCommandBuilder.class.getName()))) {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    SimpleClientCommandBuilder commandBuilder = (SimpleClientCommandBuilder) clazz.getDeclaredConstructor().newInstance();
                    registerCommand(commandBuilder);
                } catch (Exception e) {
                    MCAlusCommonUtils.LOGGER.error("An error occurred while auto registering simple client commands", e);
                }
            }
        }
    }

    public static void registerCommand(SimpleClientCommandBuilder commandBuilder) {
        String commandName = commandBuilder.getName();
        if (REGISTERED_COMMANDS.contains(commandName)) {
            MCAlusCommonUtils.LOGGER.warn("Command '{}' is already registered. Skipping.", commandName);
            return;
        }

        REGISTERED_COMMANDS.add(commandName);
        commandBuilder.registerArguments();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> literal = ClientCommandManager.literal(commandName);

            if (commandBuilder.getArguments().isEmpty()) {
                literal.executes(commandBuilder::onExecute);
            } else {
                RequiredArgumentBuilder<FabricClientCommandSource, ?> argumentBuilder = null;
                for (int i = commandBuilder.getArguments().size() - 1; i >= 0; i--) {
                    CommandArgument argument = commandBuilder.getArguments().get(i);
                    RequiredArgumentBuilder<FabricClientCommandSource, ?> newArgumentBuilder =
                            ClientCommandManager.argument(argument.getName(), (ArgumentType<?>) argument.getType());

                    if (argumentBuilder == null) {
                        newArgumentBuilder.executes(commandBuilder::onExecute);
                    } else {
                        newArgumentBuilder.then(argumentBuilder);
                    }
                    argumentBuilder = newArgumentBuilder;
                }
                literal.then(argumentBuilder);
            }
            dispatcher.register(literal);
        });
    }
}