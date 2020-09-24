package com.github.ness.violation;

import java.awt.Color;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.ViolationTrigger;
import com.github.ness.api.impl.PlayerPunishEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfComment;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;

import org.bukkit.entity.Player;

public interface ViolationTriggerSection {

	default boolean enable() {
		return violations() != -1;
	}
	
	@ConfComment("The amount of violations of a check after which to trigger this action. (-1 to disable)")
	int violations();
	
	ViolationTrigger toTrigger(ViolationManager manager, NESSAnticheat ness);
	
	interface NotifyStaff extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(6)
		int violations();
		
		@DefaultString("&8[&b&lNESS&8]&r&7> &c%PLAYER% &7failed &c%HACK%&7. Violations: %VIOLATIONS%")
		String notification();
		
		@ConfKey("discord-webhook")
		@DefaultString("")
		String discordWebHook();
		
		@ConfKey("discord-title")
		@DefaultString("Anti-Cheat")
		String discordTitle();
		
		@ConfKey("discord-description")
		@DefaultString("%HACKER% maybe is cheating!") // <hacker> is legacy variable
		String discordDescription();
		
		@ConfKey("discord-color")
		@DefaultInteger(0) // TODO: Determine java.awt.Color.RED.getRGB()
		Color discordColor();
		
		@DefaultBoolean(false)
		boolean bungeecord();
		
		@Override
		default ViolationTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new ViolationTrigger() {

				@Override
				public void trigger(Player player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String notif = manager.addViolationVariables(notification(), player, infraction);

					NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
					if (nessPlayer != null) {
						// TODO: Change this to Infraction
						//nessPlayer.sendWebhook(violation, violationCount);
					}
					if (bungeecord()) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("NESS-Reloaded");
						out.writeUTF(notif);
						player.sendPluginMessage(ness, "BungeeCord", out.toByteArray());
					}
					for (Player staff : ness.getServer().getOnlinePlayers()) {
						if (staff.hasPermission("ness.notify")) {
							staff.sendMessage(notif);
						}
					}
				}
			};
		}
		
	}
	
	interface ExecuteCommand extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(20)
		int violations();
		
		@DefaultString("kick %PLAYER% Please do not cheat. Detected for: %HACK% %VIOLATIONS% times. "
				+ "If you think this is an error, contact a staff member.")
		String command();
		
		@Override
		default ViolationTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new ViolationTrigger() {

				@Override
				public void trigger(Player player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String cmd = manager.addViolationVariables(command(), player, infraction);
					NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(player);
					if (nessPlayer == null) {
						return;
					}
					// TODO: Deprecate PlayerPunishEvent
					@SuppressWarnings("deprecation")
					PlayerPunishEvent event = new PlayerPunishEvent(player, nessPlayer,
							new com.github.ness.api.Violation(infraction.getCheck(), ""), infraction.getCount(), cmd);
					ness.getServer().getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						return;
					}
					ness.getServer().dispatchCommand(ness.getServer().getConsoleSender(), cmd);
				}
				
			};
		}
		
	}
	
	interface CancelEvent extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(3)
		int violations();
		
		@Override
		default ViolationTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new ViolationTrigger() {
				
				@Override
				public SynchronisationContext context() {
					return SynchronisationContext.EITHER;
				}

				@Override
				public void trigger(Player player, Infraction infraction) {
					
				}
				
			};
		}
		
	}
	
}
