package com.github.ness.violation;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.ViolationTrigger;
import com.github.ness.utility.DiscordWebhook;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import space.arim.dazzleconf.annote.ConfComment;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

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
		
		@ConfKey("discord.webhook")
		@DefaultString("")
		String discordWebHook();
		
		@ConfKey("discord.title")
		@DefaultString("Anti-Cheat")
		String discordTitle();
		
		@ConfKey("discord.description")
		@DefaultString("%HACKER% maybe is cheating!")
		String discordDescription();
		
		@ConfKey("discord.color")
		@DefaultString("#FF0000")
		Color discordColor();
		
		@DefaultBoolean(false)
		boolean bungeecord();
		
		@Override
		default ViolationTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new ViolationTrigger() {

				@Override
				public void trigger(AnticheatPlayer player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String notif = manager.addViolationVariables(notification(), player, infraction);

					// Only we can assume implementation details
					NessPlayer nessPlayer = (NessPlayer) player;
					sendWebhook(nessPlayer, infraction);

					if (bungeecord()) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("NESS-Reloaded");
						out.writeUTF(notif);
						player.getPlayer().sendPluginMessage(ness, "BungeeCord", out.toByteArray());
					}
					for (Player staff : ness.getServer().getOnlinePlayers()) {
						if (staff.hasPermission("ness.notify")) {
							staff.sendMessage(notif);
						}
					}
				}
				
				private void sendWebhook(NessPlayer nessPlayer, Infraction infraction) {

					final String webhookurl = discordWebHook();
					if (webhookurl.isEmpty()) {
						return;
					}
					String hackerName = nessPlayer.getPlayer().getName();
					ness.getServer().getScheduler().runTaskAsynchronously(ness, () -> {

						DiscordWebhook webhook = new DiscordWebhook(webhookurl);

						DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
								.setTitle(discordTitle())
								.setDescription(discordDescription().replace("%HACKER%", hackerName))
								.setColor(discordColor())
								.addField("Cheater", hackerName, true)
								.addField("Cheat", infraction.getCheck().getCheckName(), true)
								.addField("Violations", Integer.toString(infraction.getCount()), false);

						webhook.addEmbed(embed);

						try {
							webhook.execute();
						} catch (IOException ex) {
							NessLogger.getLogger(NotifyStaff.class).log(Level.WARNING, "Unable to send discord webhook", ex);
						}
					});
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
				public void trigger(AnticheatPlayer player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String cmd = manager.addViolationVariables(command(), player, infraction);

					// Only we can assume implementation details
					NessPlayer nessPlayer = (NessPlayer) player;

					@SuppressWarnings("deprecation")
					com.github.ness.api.impl.PlayerPunishEvent event = new com.github.ness.api.impl.PlayerPunishEvent(
							player.getPlayer(), nessPlayer,
							ViolationMigratorUtil.violationFromInfraction(infraction),
							infraction.getCount(), cmd);
					ness.getServer().getPluginManager().callEvent(event);

					if (((Cancellable) event).isCancelled()) {
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
				public void trigger(AnticheatPlayer player, Infraction infraction) {}
				
			};
		}
		
	}
	
}
