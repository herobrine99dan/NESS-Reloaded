package com.github.ness.violation;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.check.InfractionImpl;
import com.github.ness.utility.DiscordWebhook;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface ViolationTriggerSection {

	default boolean enable() {
		return violations() != -1;
	}

	@ConfComments("The amount of violations of a check after which to trigger this action. (-1 to disable)")
	int violations();

	InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness);

	interface NotifyStaff extends ViolationTriggerSection {

		@Override
		@DefaultInteger(6)
		int violations();

		@ConfComments("Notification message. Available variables are %HACK%, %VIOLATIONS% and %DETAILS%")
		@DefaultString("&8[&b&lNESS&8]&r&7> &c%PLAYER% &7failed &c%HACK%&7. Violations: %VIOLATIONS% Details: %DETAILS%")
		String notification();

		@ConfKey("discord.webhook")
		@ConfComments("Discord webhook URL, or \"\" for none")
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
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {
				
				private long lastWebHookTime = System.nanoTime();

				@Override
				public void trigger(Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					// Only we can assume implementation details
					InfractionImpl infractionImpl = (InfractionImpl) infraction;

					String notification = manager.addViolationVariables(notification(), infractionImpl);

					JavaPlugin plugin = ness.getPlugin();

					if (bungeecord()) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("NESS-Reloaded");
						out.writeUTF(notification);
						infractionImpl.getPlayer().getBukkitPlayer().sendPluginMessage(plugin, "BungeeCord",
								out.toByteArray());
					}
					sendWebhook(infractionImpl);

					for (Player staff : plugin.getServer().getOnlinePlayers()) {
						if (staff.hasPermission("ness.notify")) {
							staff.sendMessage(notification);
						}
					}
				}

				private void sendWebhook(InfractionImpl infraction) {
					final long currentTime = System.nanoTime();
					if ((currentTime - lastWebHookTime) / 1e+6 < 1400) {
						return;
					}
					lastWebHookTime = currentTime;
					final String webHookUrl = discordWebHook();
					if (webHookUrl.isEmpty()) {
						return;
					}
					String name = infraction.getPlayer().getBukkitPlayer().getName();
					JavaPlugin plugin = ness.getPlugin();
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
						sendWebHook0(webHookUrl, name, infraction);
					});
				}
				
				private void sendWebHook0(String url, String name, InfractionImpl infraction) {
					DiscordWebhook webhook = new DiscordWebhook(url);

					DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject().setTitle(discordTitle())
							.setDescription(discordDescription().replace("%HACKER%", name))
							.setColor(discordColor()).addField("Cheater", name, true)
							.addField("Cheat", infraction.getCheck().getCheckName(), true)
							.addField("Violations", Integer.toString(infraction.getCount()), false);

					webhook.addEmbed(embed);

					try {
						webhook.execute();
					} catch (IOException ex) {
						NessLogger.getLogger(NotifyStaff.class).log(Level.WARNING, "Unable to send discord webhook",
								ex);
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
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {

				@Override
				public void trigger(Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					// Only we can assume implementation details
					InfractionImpl infractionImpl = (InfractionImpl) infraction;

					String command = manager.addViolationVariables(command(), infractionImpl);

					JavaPlugin plugin = ness.getPlugin();
					if (!callDeprecatedPlayerPunishEvent(plugin, infractionImpl.getPlayer(), infraction, command)) {
						return;
					}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
				}

				@SuppressWarnings("deprecation")
				private boolean callDeprecatedPlayerPunishEvent(JavaPlugin plugin, NessPlayer nessPlayer,
						Infraction infraction, String command) {
					if (com.github.ness.api.impl.PlayerPunishEvent.getHandlerList()
							.getRegisteredListeners().length == 0) {
						return true;
					}
					com.github.ness.api.impl.PlayerPunishEvent event = new com.github.ness.api.impl.PlayerPunishEvent(
							nessPlayer.getBukkitPlayer(), nessPlayer,
							ViolationMigratorUtil.violationFromInfraction(infraction), infraction.getCount(), command);
					plugin.getServer().getPluginManager().callEvent(event);
					return !event.isCancelled();
				}

			};
		}

	}

	interface CancelEvent extends ViolationTriggerSection {

		@Override
		@DefaultInteger(3)
		int violations();

		@Override
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {

				@Override
				public SynchronisationContext context() {
					return SynchronisationContext.ANY;
				}

				@Override
				public void trigger(Infraction infraction) {
				}

			};
		}

	}

}
