package net.minecraftalus.mcaluscommonutils.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RotationArgumentType implements ArgumentType<Rotation> {
    private static final Collection<String> EXAMPLES = List.of("Y", "Y P");

    private double parseRotation(StringReader stringReader, double playerRotation) throws CommandSyntaxException {
        int cursor = stringReader.getCursor();
        if (stringReader.canRead() && stringReader.peek() == '~') {
            stringReader.skip();
            return playerRotation;
        }
        stringReader.setCursor(cursor);
        return stringReader.readDouble();
    }

    @Override
    public Rotation parse(StringReader stringReader) throws CommandSyntaxException {
        double playerPitch = MinecraftClient.getInstance().player.getPitch();
        double playerYaw = MinecraftClient.getInstance().player.getYaw();

        double pitch = parseRotation(stringReader, playerPitch);
        stringReader.skipWhitespace();
        double yaw = parseRotation(stringReader, playerYaw);
        return new Rotation(pitch, yaw);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();

        double rawPitch = MinecraftClient.getInstance().player.getPitch();
        double rawYaw =  MinecraftClient.getInstance().player.getYaw();
        String formattedPitch = String.format("%.2f", rawPitch);
        String formattedYaw = String.format("%.2f", rawYaw);

        EXAMPLES.stream()
                .filter(s -> s.startsWith(remaining))
                .forEach((i) -> builder.suggest(i.replace("Y", formattedYaw).replace("P", formattedPitch)));

        return builder.buildFuture();
    }

    public static RotationArgumentType rotation() {
        return new RotationArgumentType();
    }

    public static Rotation getRotation(CommandContext<?> context, String name) {
        return context.getArgument(name, Rotation.class);
    }
}