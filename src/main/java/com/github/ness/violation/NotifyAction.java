package com.github.ness.violation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;

import net.md_5.bungee.api.ChatColor;

public class NotifyAction extends ViolationAction {

    long lastDelay = System.nanoTime();

    public NotifyAction() {
        super();
    }

    @Override
    public void actOn(Player player, Violation violation) {
        final long current = System.nanoTime();
        final long result = (long) ((current - lastDelay) / 1e+6);
        if (result > 100) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("ness.notify")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&b&lNESS&8]&r&7> &c" + player.getName() + "&7failed &c" + violation.getCheck()
                                    + "&7. Violations: " + violation.getViolationCount() + "Details: "
                                    + violation.getDetails()));
                }
            }
        }

        lastDelay = current;
    }

}
