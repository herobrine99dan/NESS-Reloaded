package com.github.ness.check;

import java.util.concurrent.ScheduledFuture;

import org.bukkit.event.Event;
import org.bukkit.plugin.RegisteredListener;

import com.github.ness.utility.HandlerListUtils;

public class CheckFactory {
	
	ScheduledFuture<?> scheduler;
	RegisteredListener listener;
	CheckManager manager;
	CheckInfo<?> info;
	
	public CheckFactory(CheckInfo<?> info, CheckManager manager) {
		this.manager = manager;
		this.info = info;
	}
	
	public void start() {
        if (info.asyncInterval != -1L) {
        	scheduler = manager.getNess().getExecutor().scheduleWithFixedDelay(() -> {
            }, 1L, info.asyncInterval, info.units);
        }
        if (info.event != null) {
            HandlerListUtils.getEventListeners(info.event).register(listener);
        }
	}
	
	public AbstractCheck<?> generateCheck() {
		return new AbstractCheck<Event>(manager, (CheckInfo<Event>) info);
	}

}
