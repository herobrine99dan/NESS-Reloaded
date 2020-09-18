package com.github.ness.check.world;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.concurrent.TimeUnit;

public class FastPlace extends AbstractCheck<BlockPlaceEvent> {

    int max;
    
	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo
			.eventOnly(BlockPlaceEvent.class);

	public FastPlace(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	      this.max = this.manager.getNess().getNessConfig().getCheck(this.getClass())
	                .getInt("maxblockplaced", 14);
	}

    @Override
    protected void checkAsyncPeriodic() {
        player().blockPlace = 0;
    }

    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        Check(e);
    }

    /**
     * A Simple FastPlace check
     *
     * @param event
     */
    public void Check(BlockPlaceEvent e) {
        NessPlayer player = player();
        player.blockPlace++;
        if (player.blockPlace > max) {
            player.setViolation(new Violation("FastPlace", "Placing: " + player.blockPlace), e);
        }
    }

}
