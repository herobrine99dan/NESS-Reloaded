package com.github.ness.check.impl.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AntiBot extends AbstractCheck<AsyncPlayerPreLoginEvent> {
    public static ArrayList<String> whitelistbypass = new ArrayList<String>();
    int maxPlayers;
    int playerCounter = 0;
    String message;

    public AntiBot(CheckManager manager) {
        super(manager, CheckInfo.eventWithAsyncPeriodic(AsyncPlayerPreLoginEvent.class, 1, TimeUnit.SECONDS));
        message = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getString("message", "BotAttack Detected! By NESS Reloaded");
        maxPlayers = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getInt("maxplayers", 15);
    }

    @Override
    protected void checkAsyncPeriodic(NESSPlayer player) {
        this.playerCounter = 0;
    }

    @Override
    protected void checkEvent(AsyncPlayerPreLoginEvent e) {
        Check(e);
    }

    public void Check(AsyncPlayerPreLoginEvent e) {
        playerCounter++;
        if (playerCounter > maxPlayers && !whitelistbypass.contains(e.getName())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
        } else {
            e.allow();
        }
    }

}
