package net.minecraftalus.skintostatue.commands.simpleclientcommandbuilder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraftalus.skintostatue.commands.argument.CommandArgument;

public class SimpleClientCommandAutoBuilder {

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
                    e.printStackTrace();
                }
            }
        }
    }

    private static void registerCommand(SimpleClientCommandBuilder commandBuilder) {
        // populate once
        commandBuilder.registerArguments();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> literal = ClientCommandManager.literal(commandBuilder.getName());

            if (commandBuilder.getArguments().isEmpty()) {
                literal.executes(commandBuilder.onExecute());
            } else {
                RequiredArgumentBuilder<FabricClientCommandSource, ?> argumentBuilder = null;
                for (int i = commandBuilder.getArguments().size() - 1; i >= 0; i--) {
                    CommandArgument argument = commandBuilder.getArguments().get(i);
                    RequiredArgumentBuilder<FabricClientCommandSource, ?> newArgumentBuilder =
                            ClientCommandManager.argument(argument.getName(), (ArgumentType<?>) argument.getType());

                    if (argumentBuilder == null) {
                        newArgumentBuilder.executes(commandBuilder.onExecute());
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