package net.minecraftalus.skintostatue.commands;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraftalus.skintostatue.commands.argument.CommandArgument;
import net.minecraftalus.skintostatue.commands.simpleclientcommandbuilder.AutoBuildSimpleClientCommand;
import net.minecraftalus.skintostatue.commands.simpleclientcommandbuilder.SimpleClientCommandBuilder;

import java.util.ArrayList;
import java.util.List;

@AutoBuildSimpleClientCommand
public class GetTargetedBlockCommand implements SimpleClientCommandBuilder {
    private final ArrayList<CommandArgument> arguments = new ArrayList<>();

    @Override
    public String getName() {
        return "gettargetedblock";
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public void registerArguments() {
        // No arguments needed for this command
    }

    @Override
    public Command<FabricClientCommandSource> onExecute() {
        return (context) -> {
            HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;

            if (hitResult instanceof BlockHitResult hit) {

                BlockPos hitPos = hit.getBlockPos();
                World world = context.getSource().getPlayer().getWorld();
                BlockState targetedBlockState = world.getBlockState(hitPos);

                Identifier blockIdentifier = Registries.BLOCK.getId(targetedBlockState.getBlock());
                String blockName = blockIdentifier.toString();
                boolean isTargetedBlockSolid = targetedBlockState.isSolidBlock(world, hitPos);

                System.out.println(blockName);

                context.getSource().sendFeedback(Text.of("Current block is: " + blockName + ", is solid: " + isTargetedBlockSolid));
                return 1;
            }
            context.getSource().sendFeedback(Text.of("Target is not a block"));
            return 1;
        };
    }
}