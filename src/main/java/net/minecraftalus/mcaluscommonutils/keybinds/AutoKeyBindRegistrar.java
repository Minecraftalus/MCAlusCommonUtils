package net.minecraftalus.mcaluscommonutils.keybinds;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class AutoKeyBindRegistrar {
    public static void initialize() {
        ClassLoader classLoader = KeyBindingBuilder.class.getClassLoader();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .overrideClassLoaders(classLoader)
                .scan()) {

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(AutoRegisterKeybind.class.getName())
                    .filter(classInfo -> classInfo.implementsInterface(KeyBindingBuilder.class.getName()))) {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    KeyBindingBuilder commandBuilder = (KeyBindingBuilder) clazz.getDeclaredConstructor().newInstance();
                    registerKeybind(commandBuilder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void registerKeybind(KeyBindingBuilder keyBindingBuilder) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        keyBindingBuilder.getTranslationName(),
                        keyBindingBuilder.getType(),
                        keyBindingBuilder.getKeyCode(),
                        keyBindingBuilder.getTranslationCategory()
                )
        );
        ClientTickEvents.END_CLIENT_TICK.register(client -> keyBindingBuilder.runEveryTick().accept(keyBinding));
    }
}
