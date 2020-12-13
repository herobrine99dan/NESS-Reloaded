package com.github.ness.packets;

import java.util.UUID;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

class PacketInterceptorHandler extends ChannelDuplexHandler {

	private final UUID uuid;
	private final PacketInterceptor interceptor;

	PacketInterceptorHandler(UUID uuid, PacketInterceptor actor) {
		this.uuid = uuid;
		this.interceptor = actor;
	}

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
    	if (interceptor.shouldDrop(uuid, packet)) {
    		return;
    	}
    	super.channelRead(channelHandlerContext, packet);
    }

}
