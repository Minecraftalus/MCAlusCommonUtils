package net.minecraftalus.mcaluscommonutils.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PositionArgumentType implements ArgumentType<Position> {
    private static final Collection<String> EXAMPLES = List.of("X", "X Y", "X Y Z", "~", "~ ~", "~ ~ ~");

    private int parseCoordinate(StringReader stringReader, double playerCoord) throws CommandSyntaxException {
        int cursor = stringReader.getCursor();
        if (stringReader.canRead() && stringReader.peek() == '~') {
            stringReader.skip();
            return (int) playerCoord;
        }
        stringReader.setCursor(cursor);
        return stringReader.readInt();
    }

    @Override
    public Position parse(StringReader stringReader) throws CommandSyntaxException {
        Vec3d playerPos = MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getPos() : null;

        if (playerPos!=null){
            int x = parseCoordinate(stringReader, playerPos.x);
            stringReader.skipWhitespace();
            int y = parseCoordinate(stringReader, playerPos.y);
            stringReader.skipWhitespace();
            int z = parseCoordinate(stringReader, playerPos.z);
            return new Position(x, y, z);
        }
        return null;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        String remaining = builder.getRemaining();

        boolean isTargetingBlock = false;
        if (MinecraftClient.getInstance().world != null) {
            isTargetingBlock = Optional.ofNullable(hitResult)
                    .filter(h -> h.getType() == HitResult.Type.BLOCK)
                    .map(h -> (BlockHitResult) h)
                    .map(BlockHitResult::getBlockPos)
                    .map(MinecraftClient.getInstance().world::getBlockState)
                    .map(BlockState::getBlock)
                    .map(b -> b != Blocks.AIR)
                    .orElse(false);
        }

        if (isTargetingBlock) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            Vec3d hitPos = blockHitResult.getPos();
            int x = (int) Math.floor(hitPos.x);
            int y = (int) Math.floor(hitPos.y);
            int z = (int) Math.floor(hitPos.z);

            List<String> suggestions = List.of(
                    String.valueOf(x),
                    String.format("%d %d", x, y),
                    String.format("%d %d %d", x, y, z)
            );

            suggestions.stream()
                    .filter(s -> s.startsWith(remaining))
                    .forEach(builder::suggest);
        } else {
            Stream.of("~", "~ ~", "~ ~ ~")
                    .filter(s -> s.startsWith(remaining))
                    .forEach(builder::suggest);
        }

        return builder.buildFuture();
    }
    public static PositionArgumentType pos() {
        return new PositionArgumentType();
    }

    public static Position getPosition(CommandContext<?> context, String name) {
        return context.getArgument(name, Position.class);
    }
}