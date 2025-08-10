package net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraftalus.mcaluscommonutils.commands.argument.CommandArgument;

import java.util.List;

public interface SimpleClientCommandBuilder {
    String getName();
    void registerArguments();         // populate a per-instance list
    Command<FabricClientCommandSource> onExecute();
    List<CommandArgument> getArguments(); // implementations provide their own list
}

