package com.github.ness;

import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.ReceivedPacketEvent;

public class TestCheck extends Check {

    protected TestCheck(NessPlayer nessPlayer, CheckManager manager) {
        super(TestCheck.class, nessPlayer, manager);
    }

    @Override
    public void checkEvent(ReceivedPacketEvent e) {
        // TODO Auto-generated method stub

    }

}
