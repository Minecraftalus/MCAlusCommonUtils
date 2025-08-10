package net.minecraftalus.mcaluscommonutils.keybinds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface KeyBindingBuilder {
    String getTranslationName();
    InputUtil.Type getType();
    int getKeyCode();
    String getTranslationCategory();
    Consumer<KeyBinding> runEveryTick();
}
