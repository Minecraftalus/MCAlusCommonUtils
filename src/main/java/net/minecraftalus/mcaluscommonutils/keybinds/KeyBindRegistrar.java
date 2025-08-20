package net.minecraftalus.mcaluscommonutils.keybinds;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraftalus.mcaluscommonutils.MCAlusCommonUtils;

import java.util.HashSet;
import java.util.Set;

public class KeyBindRegistrar {

    private static final Set<String> REGISTERED_KEYBINDS = new HashSet<>();

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
                    KeyBindingBuilder keyBindingBuilder = (KeyBindingBuilder) clazz.getDeclaredConstructor().newInstance();
                    registerKeybind(keyBindingBuilder);
                } catch (Exception e) {
                    MCAlusCommonUtils.LOGGER.error("An error occurred while auto registering key binds", e);
                }
            }
        }
    }

    public static void registerKeybind(KeyBindingBuilder keyBindingBuilder) {
        String keybindTranslationName = keyBindingBuilder.getTranslationName();
        if (REGISTERED_KEYBINDS.contains(keybindTranslationName)) {
            MCAlusCommonUtils.LOGGER.warn("Keybind '{}' is already registered. Skipping.", keybindTranslationName);
            return;
        }

        REGISTERED_KEYBINDS.add(keybindTranslationName);
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