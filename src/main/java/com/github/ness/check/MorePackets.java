package com.github.ness.check;

import com.github.ness.CheckManager;
import com.github.ness.packets.events.ReceivedPacketEvent;

public class MorePackets extends AbstractCheck<ReceivedPacketEvent> {

	public MorePackets(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(ReceivedPacketEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(ReceivedPacketEvent e) {
		System.out.println("Packet!");
	}
}
