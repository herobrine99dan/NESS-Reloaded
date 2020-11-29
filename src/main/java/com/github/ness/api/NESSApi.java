package com.github.ness.api;

import java.util.Objects;

import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.Check;

public class NESSApi {

    private NessAnticheat ness;

    public NESSApi(NessAnticheat ness) {
        this.ness = ness;
    }

    /**
     * Flag a Player for a specific Cheat
     * 
     * @param Bukkit    Player object
     * @param Violation object
     * @return 0 if the check name specified in Violation object wasn't found else
     *         the violation Counter
     */
    public int flagPlayer(Player p, Violation violation) {
        NessPlayer nessPlayer = validatePlayer(p);
        for (Check c : nessPlayer.getChecks()) {
            if (c.getCheckName().equals(violation.getCheck())) {
                return c.flag(violation.getDetails(), null);
            }
        }
        return 0;
    }

    /**
     * Add a ViolationAction that will be executed when a player flags a check.
     * 
     * @param action
     */
    public void addViolationAction(ViolationAction action) {
        this.ness.getViolationHandler().addAction(action);
    }

    /**
     * Check if Player isn't online or if the Player object is null
     * 
     * @param Player p
     * @return the NessPlayer if there weren't errors
     */
    private NessPlayer validatePlayer(Player p) {
        Objects.requireNonNull(p);
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(p.getUniqueId());
        Objects.requireNonNull(nessPlayer);
        if (!p.isOnline()) {
            throw new NullPointerException("The Player isn't online and NessPlayer object is null!");
        }
        return ness.getCheckManager().getNessPlayer(p.getUniqueId());
    }
}
