package com.github.ness;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

class NessCommands implements CommandExecutor {

    private static final Logger logger = NessLogger.getLogger(NessCommands.class);

    private final NessAnticheat ness;

    NessCommands(NessAnticheat ness) {
        this.ness = ness;
    }

    private String getPermission(String arg) {
        if (arg == null) {
            return "ness.command.version";
        }
        switch (arg) {
        case "reload":
            return "ness.command.reload";
        case "violations":
            return "ness.command.violations";
        case "clear":
            return "ness.command.clear";
        case "debug":
            return "ness.command.debug";
        case "debugvelocity":
            return "ness.command.debugvelocity";
        default:
            return "ness.command.version";
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onCommand0(sender, args);
        return true;
    }

    private void onCommand0(final CommandSender sender, final String[] args) {
        String firstArg = (args.length >= 1) ? args[0] : null;
        if (!sender.hasPermission(getPermission(firstArg))) {
            return;
        }
        if (firstArg == null) {
            usage(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
        case "reload":
            try {
                sendMessage(sender, "&aReloaded NESS!");
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Error while reloading", ex);
                sendMessage(sender, "&cError reloading NESS, check your server console for details.");
                return;
            }
            break;
        case "violations":
            showViolations(sender, (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null);
            break;
        case "clear":
            clearViolations(sender, (args.length >= 2) ? Bukkit.getPlayer(args[1]) : null);
            break;
        case "version":
            sendMessage(sender, "&7NESS Version: " + ness.getPlugin().getDescription().getVersion());
            break;
        case "debug":
            if (sender instanceof Player) {
                NessPlayer np = ness.getCheckManager().getNessPlayer((Player) sender);
                if (np == null) {
                    // This shouldn't happen, but just in case
                    sendMessage(sender, "Your NESSPlayer isn't loaded");
                    return;
                }
                if (np.isDebugMode()) {
                    this.sendMessage(sender, "&7Debug Mode &cDisabled&7!");
                    np.setDebugMode(false);
                } else {
                    this.sendMessage(sender, "&7Debug Mode &aEnabled&7!");
                    np.setDebugMode(true);
                }
            } else {
                mustBePlayer(sender);
            }
            break;
        case "debugvelocity":
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length > 1) {
                    p.setVelocity(p.getLocation().getDirection().multiply(Double.valueOf(args[1])));
                    p.sendMessage("Done!");
                }
            } else {
                mustBePlayer(sender);
            }
            break;
        default:
            usage(sender);
            break;
        }
        return;
    }

    private void showViolations(CommandSender sender, Player target) {
        if (target == null) {
            sendUnknownTarget(sender);
            return;
        }
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(target);
        if (nessPlayer == null) {
            sendUnknownTarget(sender);
            return;
        }
        String name = target.getName();
        String header = "Header:";
        String body = "Body";

        sendMessage(sender, header.replace("%TARGET%", name));
        ness.getCheckManager().getNessPlayer(target.getUniqueId()).getChecks().forEach((check) -> {
            sendMessage(sender, body.replace("%HACK%", check.getCheckName()).replace("%VIOLATIONS%",
                    Integer.toString(check.currentViolationCount())));
        });
    }

    private void clearViolations(CommandSender sender, Player target) {
        if (target == null) {
            sendUnknownTarget(sender);
            return;
        }
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(target);
        if (nessPlayer == null) {
            sendUnknownTarget(sender);
            return;
        }
        ness.getCheckManager().getNessPlayer(target.getUniqueId()).getChecks()
                .forEach((check) -> check.clearViolationCount());
        sendMessage(sender, "&7Cleared violations for &e%TARGET%".replace("%TARGET%", target.getName()));
    }

    private void sendUnknownTarget(CommandSender sender) {
    }

    private void mustBePlayer(CommandSender sender) {
        sendMessage(sender, "&7Sorry but to use this command you have to be a Player");
    }

    private void usage(CommandSender sender) {
        PluginDescriptionFile description = ness.getPlugin().getDescription();
        sendMessage(sender, "&aNESS Anticheat");
        sendMessage(sender, "&7Version " + description.getVersion());
        sendMessage(sender, "/ness reload - Reload configuration");
        sendMessage(sender, "/ness violations <player> - Show violations for a player");
        sendMessage(sender, "/ness clear <player> - Clear player violations");
        sendMessage(sender, "/ness version - View the NESS Reloaded Version");
        sendMessage(sender, "/ness gui - Open a gui where you can see violations of players");
        sendMessage(sender, "/ness debug - Enable / Disable Debug Mode");
        sendMessage(sender, "&7Authors: " + String.join(", ", description.getAuthors()));
    }

    private void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

}
