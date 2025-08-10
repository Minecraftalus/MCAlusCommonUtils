package net.minecraftalus.mcaluscommonutils.commands;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.text.Text;
import net.minecraftalus.mcaluscommonutils.commands.argument.CommandArgument;
import net.minecraftalus.mcaluscommonutils.commands.argument.Position;
import net.minecraftalus.mcaluscommonutils.commands.argument.PositionArgumentType;
import net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder.AutoBuildSimpleClientCommand;
import net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder.SimpleClientCommandBuilder;

import java.util.ArrayList;
import java.util.List;

@AutoBuildSimpleClientCommand
public class TestPosArg implements SimpleClientCommandBuilder {

    private final ArrayList<CommandArgument> arguments = new ArrayList<>();

    @Override
    public String getName() {
        return "testposarg";
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;

    }

    @Override
    public void registerArguments() {
        arguments.add(new CommandArgument(PositionArgumentType.pos(), "pitch"));
    }

    @Override
    public Command<FabricClientCommandSource> onExecute() {
        return (context) -> {
            Position pos = PositionArgumentType.getPosition(context, "pitch");

            context.getSource().getPlayer().sendMessage(Text.of(pos.toString()), true);

            context.getSource().sendFeedback(Text.of(pos.toString()));
            return 1;
        };
    }


}
