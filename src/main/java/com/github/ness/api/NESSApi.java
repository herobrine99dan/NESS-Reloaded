package com.github.ness.api;

import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.Check;

public class NESSApi {

    NessAnticheat ness;

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
        violation.validateValues();
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
     * Flag a Player for a specific Cheat
     * 
     * @param Bukkit Player object
     * @param Check  check object
     * @param String details object
     * @return 0 if the check wasn't founded specified in Violation object wasn't
     *         found else the violation Counter
     */
    public int flagPlayer(Player p, Check check, String details) {
        NessPlayer nessPlayer = validateValues(p, check);
        for (Check c : nessPlayer.getChecks()) {
            if (c.equals(check)) {
                return c.flag(details, null);
            }
        }
        return 0;
    }

    /**
     * Check if Player isn't online or if the Player object is null
     * 
     * @param Player p
     * @return the NessPlayer if there weren't errors
     */
    private NessPlayer validatePlayer(Player p) {
        if (p == null) {
            throw new NullPointerException("Player Object can't be null!");
        }
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(p.getUniqueId());
        if (!p.isOnline() && nessPlayer == null) {
            throw new NullPointerException("The Player isn't online and NessPlayer object is null!");
        }
        return ness.getCheckManager().getNessPlayer(p.getUniqueId());
    }

    /**
     * Check if the Check object is null, then call validatePlayer
     * 
     * @param Player p
     * @param Check  check
     * @return the NessPlayer obtained from validatePlayer(player) method
     */
    private NessPlayer validateValues(Player p, Check check) {
        if (check == null) {
            throw new NullPointerException("Check object can't be null!");
        }
        return validatePlayer(p);
    }

}
