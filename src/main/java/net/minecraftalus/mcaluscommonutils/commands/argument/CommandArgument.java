package net.minecraftalus.mcaluscommonutils.commands.argument;

import com.mojang.brigadier.arguments.ArgumentType;

public class CommandArgument {
    private final ArgumentType<?> argumentType;
    private final String name;

    public CommandArgument(ArgumentType<?> argumentType, String name) {
        this.argumentType=argumentType;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public ArgumentType<?> getType() {
        return argumentType;
    }
}
