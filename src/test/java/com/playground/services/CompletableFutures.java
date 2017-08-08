package com.playground.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.playground.entity.Channel;
import com.playground.entity.Permissions;
import com.playground.entity.Result;
import com.playground.entity.User;

@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CompletableFutures extends AbstractTestService {

    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    

    /**
     * Scenario: A web request comes in asking if chbatey has the SPORTS
     * permission
     * <p>
     * Questions: - Does the user exist? - Is the user allowed to watch the
     * channel?
     */
    @Test
    public void chbatey_has_sports_blocking() throws Exception {
	CompletableFuture<User> cUser = userService.lookupUserCompletable("chbatey");

	// Make the blocking explicit
	user = cUser.get();

	CompletableFuture<Permissions> pFuture = permissionsService.getPermissionsCompletable(user.getUserId());

	// Explicit blocking
	userPermissions = pFuture.get();

	assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    /**
     * Scenario: A web request comes in asking if chbatey has the SPORTS
     * permission
     * <p>
     * Questions: - Does the user exist? - Is the user allowed to watch the
     * channel?
     */
    @Test
    public void chbatey_has_sports_compose_and_block() throws Exception {
	CompletableFuture<User> cUser = userService.lookupUserCompletable("chbatey");
	CompletableFuture<Permissions> cPermissions = cUser
		.thenCompose(user -> permissionsService.getPermissionsCompletable(user.getUserId()));

	// blocks but we could have used a call back
	userPermissions = cPermissions.get();

	assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    @Test
    public void chbatey_has_sports_compose_no_blocking() throws Exception {
	CompletableFuture<User> cUser = userService.lookupUserCompletable("chbatey");
	CompletableFuture<Permissions> cPermissions = cUser
		.thenCompose(user -> permissionsService.getPermissionsCompletable(user.getUserId()));

	cPermissions.thenAccept((Permissions p) -> {
	    p.hasPermission("SPORTS");
	});
    }

    /**
     * Scenario: A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions: - Does this channel exist? - Is chbatey a valid user? - Does
     * chbatey have the permissionsService to watch Sports?
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one() throws Exception {
	CompletableFuture<User> cUser = userService.lookupUserCompletable("chbatey");
	CompletableFuture<Permissions> cPermissions = cUser
		.thenCompose(u -> permissionsService.getPermissionsCompletable(u.getUserId()));
	CompletableFuture<Channel> cChannel = channelService.lookupChannelCompletable("SkySportsOne");

	channel = cChannel.get();
	userPermissions = cPermissions.get();
	user = cUser.get(); // will definitely be done as permissionsService is
			    // done

	assertNotNull(channel);
	assertTrue(userPermissions.hasPermission("SPORTS"));
	assertNotNull(user);
    }

    /**
     * Do all of the above but also time out if we don't get all the results
     * back within 500 milliseconds
     *
     * applyToEither
     */
    @Test
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
	CompletableFuture<User> cUser = userService.lookupUserCompletable("chbatey");
	CompletableFuture<Permissions> cPermissions = cUser
		.thenCompose(u -> permissionsService.getPermissionsCompletable(u.getUserId()));
	CompletableFuture<Channel> cChannel = channelService.lookupChannelCompletable("SkySportsOne");

	CompletableFuture<Result> cResult = cPermissions.thenCombine(cChannel, (p, c) -> new Result(c, p));
	CompletableFuture<Result> cTimeout = timeout(3000);
	CompletableFuture<Result> cResultWithTimeout = cResult.applyToEither(cTimeout, Function.identity());

	blockUntilComplete(cResultWithTimeout);

	assertFalse(cResultWithTimeout.isCompletedExceptionally());
	result = cResultWithTimeout.get();
	assertNotNull(result.channel);
	assertTrue(result.permissions.hasPermission("SPORTS"));
    }

    private void blockUntilComplete(CompletableFuture<?> cf) {
	try {
	    cf.get();
	} catch (Exception e) {
	    LOG.warn("Future failed", e);
	}
    }

    private CompletableFuture<Result> timeout(int millis) {
	CompletableFuture<Result> cf = new CompletableFuture<>();
	ses.schedule(() -> cf.completeExceptionally(new TimeoutException("OMG we timed out")), millis,
		TimeUnit.MILLISECONDS);
	return cf;
    }
}
