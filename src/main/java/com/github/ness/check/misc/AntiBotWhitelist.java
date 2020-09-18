package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiBotWhitelist extends AbstractCheck<PlayerJoinEvent> {
    int neededSeconds = 10;
	public static final CheckInfo<PlayerJoinEvent> checkInfo = CheckInfo
			.eventOnly(PlayerJoinEvent.class);

	public AntiBotWhitelist(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.neededSeconds = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getInt("minimumseconds", 10);
	}

    @Override
    protected void checkEvent(PlayerJoinEvent e) {
        Check(e);
    }

    void Check(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer().isOnline()) {
                    if (!AntiBot.whitelistbypass.contains(e.getPlayer().getName())) {
                        AntiBot.whitelistbypass.add(e.getPlayer().getName());
                    }
                }
            }
        }.runTaskLater(manager.getNess(), neededSeconds * 20L);
    }
}
