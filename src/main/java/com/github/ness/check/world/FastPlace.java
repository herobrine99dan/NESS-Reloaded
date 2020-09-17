package com.github.ness.check.world;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class FastPlace extends AbstractCheck<BlockPlaceEvent> {

    int max;
    
	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo.eventWithAsyncPeriodic(BlockPlaceEvent.class, 1, TimeUnit.SECONDS);
    private int blockPlace; // For FastPlace Check

	public FastPlace(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	      this.max = this.ness().getNessConfig().getCheck(this.getClass())
	                .getInt("maxblockplaced", 14);
	      blockPlace = 0;
	}

    @Override
    protected void checkAsyncPeriodic() {
        blockPlace = 0;
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
        blockPlace++;
        if (blockPlace > max) {
            player.setViolation(new Violation("FastPlace", "Placing: " + blockPlace), e);
        }
    }

}
