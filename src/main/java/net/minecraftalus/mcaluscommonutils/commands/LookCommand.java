package net.minecraftalus.mcaluscommonutils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraftalus.mcaluscommonutils.commands.argument.CommandArgument;
import net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder.AutoBuildSimpleClientCommand;
import net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder.SimpleClientCommandBuilder;

import java.util.ArrayList;
import java.util.List;

@AutoBuildSimpleClientCommand
public class LookCommand implements SimpleClientCommandBuilder {
    private final ArrayList<CommandArgument> arguments = new ArrayList<>();

    @Override
    public String getName() {
        return "look";
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public void registerArguments() {
        arguments.add(new CommandArgument(IntegerArgumentType.integer(), "pitch"));
        arguments.add(new CommandArgument(IntegerArgumentType.integer(), "yaw"));
    }

    @Override
    public Command<FabricClientCommandSource> onExecute() {
        return (context) -> {
                int pitch = IntegerArgumentType.getInteger(context, "pitch");
                int yaw = IntegerArgumentType.getInteger(context, "yaw");
                PlayerEntity player = context.getSource().getPlayer();

                player.setPitch(pitch);
                player.setYaw(yaw);

                context.getSource().sendFeedback(Text.of("Set look direction to Yaw: " + yaw + ", Pitch: " + pitch));
            return 1;
        };
    }

}
