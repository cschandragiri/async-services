package com.playground.services;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;
import com.playground.entity.Config;
import com.playground.entity.Permissions;

@Service
public class PermissionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionsService.class);

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
	    new ThreadFactoryBuilder().setNameFormat("permissions-service-%d").build());
    private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);

    private final Map<Integer, Permissions> permissions = ImmutableMap.of(1, Permissions.permissions("SPORTS", "ENTS"),
	    2, Permissions.permissions());

    public Permissions getPermissions(int userId) {
	Uninterruptibles.sleepUninterruptibly(Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
	LOG.info("Permission lookup complete");
	return permissions.get(userId);
    }

    public Future<Permissions> getPermissionsAsync(int userId) {
	return executor.schedule(() -> {
	    LOG.info("Permission lookup complete");
	    return permissions.get(userId);
	}, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public ListenableFuture<Permissions> getPermissionsListenable(int userId) {
	return ls.schedule(() -> {
	    LOG.info("Permission lookup complete");
	    return permissions.get(userId);
	}, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Permissions> getPermissionsCompletable(int userId) {
	CompletableFuture<Permissions> result = new CompletableFuture<>();
	executor.schedule(() -> {
	    LOG.info("Permissions look up complete");
	    result.complete(permissions.get(userId));
	}, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
	return result;
    }
}
