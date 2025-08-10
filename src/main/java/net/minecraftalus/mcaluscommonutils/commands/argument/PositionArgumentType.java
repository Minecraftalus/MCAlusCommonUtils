package net.minecraftalus.mcaluscommonutils.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
        int x = parseCoordinate(stringReader, playerPos.x);
        stringReader.skipWhitespace();
        int y = parseCoordinate(stringReader, playerPos.y);
        stringReader.skipWhitespace();
        int z = parseCoordinate(stringReader, playerPos.z);
        return new Position(x, y, z);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        Vec3d hitPos = null;
        boolean isTargetingBlock = false;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);

            if (blockState.getBlock() != Blocks.AIR) {
                hitPos = blockHitResult.getPos();
                isTargetingBlock = true;
            }
        }

        String remaining = builder.getRemaining();

        if (isTargetingBlock) {
            int x = (int) Math.floor(hitPos.x);
            int y = (int) Math.floor(hitPos.y);
            int z = (int) Math.floor(hitPos.z);

            List<String> suggestions = List.of(String.valueOf(x), String.format("%d %d", x, y), String.format("%d %d %d", x, y, z));
            for (String suggestion : suggestions) {
                if (suggestion.startsWith(remaining)) {
                    builder.suggest(suggestion);
                }
            }
        } else {
            List<String> suggestions = List.of("~", "~ ~", "~ ~ ~");
            for (String suggestion : suggestions) {
                if (suggestion.startsWith(remaining)) {
                    builder.suggest(suggestion);
                }
            }
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