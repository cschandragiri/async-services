package com.playground.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.*;
import com.playground.entity.Channel;
import com.playground.entity.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ChannelService {
    private static Logger LOG = LoggerFactory.getLogger(ChannelService.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
	    new ThreadFactoryBuilder().setNameFormat("channel-service-%d").build());
    private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);

    private static final Map<String, Channel> channels = ImmutableMap.of("SkyOne", new Channel("SkyOne"),
	    "SkySportsOne", new Channel("SkySportsOne"));

    public Channel lookupChannel(String name) {
	Uninterruptibles.sleepUninterruptibly(Config.CHANNEL_DELAY, TimeUnit.MILLISECONDS);
	LOG.info("Channel lookup complete");
	return channels.get(name);
    }

    public Future<Channel> lookupChannelAsync(String name) {
	return executor.schedule(() -> {
	    LOG.info("Channel lookup complete");
	    return channels.get(name);
	}, Config.CHANNEL_DELAY, TimeUnit.MILLISECONDS);
    }

    public ListenableFuture<Channel> lookupChannelListenable(String name) {
	return ls.schedule(() -> {
	    LOG.info("Channel lookup complete");
	    return channels.get(name);
	}, Config.CHANNEL_DELAY, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Channel> lookupChannelCompletable(String name) {
	CompletableFuture<Channel> result = new CompletableFuture<>();
	executor.schedule(() -> {
	    LOG.info("Channel lookup complete");
	    result.complete(channels.get(name));
	}, Config.CHANNEL_DELAY, TimeUnit.MILLISECONDS);
	return result;
    }
}
