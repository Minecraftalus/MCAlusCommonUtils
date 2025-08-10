package net.minecraftalus.mcaluscommonutils;

import net.fabricmc.api.ModInitializer;
import net.minecraftalus.mcaluscommonutils.commands.simpleclientcommandbuilder.SimpleClientCommandAutoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCAlusCommonUtils implements ModInitializer {

	public static final String MOD_ID = "MCAlusCU";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SimpleClientCommandAutoBuilder.initialize();

		LOGGER.info("Minecraftalus' Common Utilities started");
	}
}