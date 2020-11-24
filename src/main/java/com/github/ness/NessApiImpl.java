package com.github.ness;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;

@SuppressWarnings("deprecation")
final class NessApiImpl {

    private final NessAnticheat ness;

    NessApiImpl(NessAnticheat ness) {
        this.ness = ness;
    }

    public void flagHack(Violation violation, Player player) {
        LoggerHolder.logger.warning(
                "Caller is extremely discouraged from using the deprecated flagHack(Violation, Player) method");
        CheckManager manager = ness.getCheckManager();
        NessPlayer anticheatPlayer = manager.getNessPlayer(player.getUniqueId());
        if (anticheatPlayer != null) {
            for (Check check : anticheatPlayer.getChecks()) {
                if (check.getCheckName().equals(violation.getCheck())) {
                    check.flag("");
                    return;
                }
            }

        }
    }

    private static class LoggerHolder {
        static final Logger logger = NessLogger.getLogger(NessApiImpl.class);
    }

}
