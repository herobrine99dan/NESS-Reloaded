package com.github.ness;

import org.bukkit.event.Event;

import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;

public class TestCheck extends Check {

    protected TestCheck(NessPlayer nessPlayer, CheckManager manager) {
        super(TestCheck.class, nessPlayer, manager);
    }

    @Override
    public void checkEvent(Event e) {
        // TODO Auto-generated method stub

    }

}
