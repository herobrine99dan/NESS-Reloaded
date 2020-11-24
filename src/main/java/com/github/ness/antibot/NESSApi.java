package com.github.ness.antibot;

import org.bukkit.entity.Player;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.api.ViolationAction;
import com.github.ness.check.Check;

public class NESSApi {

    NessAnticheat ness;

    public NESSApi(NessAnticheat ness) {
        this.ness = ness;
    }
    
    public int flagPlayer(Player p, Violation violation) {
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(p.getUniqueId());
        for(Check c : nessPlayer.getChecks()) {
            if(c.getCheckName().equals(violation.getCheck())) {
                return c.flag(violation.getDetails());
            }
        }
        return 0;
    }
    
    public void addViolationAction(ViolationAction action) {
        this.ness.getViolationHandler().addAction(action);
    }
    
    public int flagPlayer(Player p, Check check, String details) {
        NessPlayer nessPlayer = ness.getCheckManager().getNessPlayer(p.getUniqueId());
        for(Check c : nessPlayer.getChecks()) {
            if(c.equals(check)) {
                return c.flag(details);
            }
        }
        return 0;
    }

}
