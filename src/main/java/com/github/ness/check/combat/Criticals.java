package com.github.ness.check.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class Criticals extends ListeningCheck<EntityDamageByEntityEvent> {

    public static final ListeningCheckInfo<EntityDamageByEntityEvent> checkInfo = CheckInfos
            .forEvent(EntityDamageByEntityEvent.class);

    public Criticals(ListeningCheckFactory<?, EntityDamageByEntityEvent> factory, NessPlayer player) {
        super(factory, player);
    }

    @Override
    protected void checkEvent(EntityDamageByEntityEvent e) {
        if (player().isNot(e.getDamager())) {
            return;
        }
        check(e);
    }

    private void check(EntityDamageByEntityEvent event) {
        NessPlayer nessPlayer = player();
        Player player = (Player) event.getDamager();
        MovementValues values = player().getMovementValues();
        if (values.isNearMaterials("WEB")) {
            return;
        }
        if (values.isNearLiquid() || values.getHelper().hasflybypass(nessPlayer)) {
            return;
        }
        boolean onGroundReally = this.player().getMovementValues().getTo().getY() % 1.0 == 0.0;
        boolean onGround = player.isOnGround();
        if (!onGround && onGroundReally) {
            flagEvent(event);
        }
    }
}
