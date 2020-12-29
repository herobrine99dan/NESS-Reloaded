package com.github.ness.check.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.excel.ExcelData;

public class TestCheck extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	ArrayList<String> yDiffs;
	ArrayList<String> lastYDiffs;
	double lastYDiff;

	public TestCheck(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		yDiffs = new ArrayList<String>();
		lastYDiffs = new ArrayList<String>();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = player().getMovementValues();
	}

}
