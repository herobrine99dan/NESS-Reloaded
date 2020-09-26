package com.github.ness.antibot;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

public interface AntiBotConfig {

	@DefaultBoolean(true)
	boolean enable();
	
	@ConfKey("max-players-per-second")
	@ConfComments("Maximum players able to join in one second")
	@DefaultInteger(15)
	int maxPlayersPerSecond();
	
	@ConfKey("kick-message")
	@ConfComments("The kick message")
	@DefaultString("Bot Attack Detected! By NESS Reloaded")
	String kickMessage();
	
	@ConfKey("time-until-trusted")
	@ConfComments(
			"The play time, in seconds, after which a player will not be denied joining "
			+ "if he or she rejoins during a bot attack")
	@DefaultInteger(10)
	int timeUntilTrusted();
	
}
