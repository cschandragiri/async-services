package com.playground.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.playground.entity.Channel;
import com.playground.entity.Permissions;
import com.playground.entity.Result;
import com.playground.entity.User;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class Synchronous extends AbstractTestService {

    /**
     * Show how the user service works
     */
    @Test
    public void userService() {
	assertNull("I don't expect charlie to exist", userService.lookupUser("charlie"));

	assertEquals(new User("Christopher Batey", "chbatey", 1), userService.lookupUser("chbatey"));
    }

    /**
     * Show how the permissionsService service works
     */
    @Test
    public void permissionsService() {
	assertNull("I don't expect charlie to have any permissionsService", permissionsService.getPermissions(3));

	assertEquals(Permissions.permissions("ENTS", "SPORTS"), permissionsService.getPermissions(1));
    }

    /**
     * Show how the ChannelService works
     */
    @Test
    public void channelService() {
	assertNull("No channel named charlie", channelService.lookupChannel("charlie"));

	assertEquals(new Channel("SkySportsOne"), channelService.lookupChannel("SkySportsOne"));
    }

    /**
     * Scenario: A web request comes in asking if chbatey has the SPORTS
     * permission
     * <p>
     * Questions: - Does the user exist? - Is the user allowed to watch the
     * channel?
     */
    @Test
    public void chbatey_has_sports() throws Exception {
	user = userService.lookupUser("chbatey");
	userPermissions = permissionsService.getPermissions(user.getUserId());

	assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    /**
     * Scenario: A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions: - Does this channel exist? - Is chbatey a valid user? - Does
     * chbatey have the permissionsService to watch Sports?
     */
    @Test
    public void chbatey_watch_sky_sports_one() {
	user = userService.lookupUser("chbatey"); // ~500ms
	userPermissions = permissionsService.getPermissions(user.getUserId()); // ~500ms
	channel = channelService.lookupChannel("SkySportsOne"); // ~500ms

	assertNotNull(channel);
	assertTrue(userPermissions.hasPermission("SPORTS"));
	assertNotNull(user);
    }

    /**
     * Scenario: A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions: - Does this channel exist? - Is chbatey a valid user? - Does
     * chbatey have the permissionsService to watch Sports?
     * <p>
     * Take a 2/3 of the response time.
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one_fast() throws Exception {
	ExecutorService es = Executors.newSingleThreadExecutor();
	Future<Channel> fChannel = es.submit(() -> channelService.lookupChannel("SkySportsOne"));
	user = userService.lookupUser("chbatey");
	userPermissions = permissionsService.getPermissions(user.getUserId());
	channel = fChannel.get();

	assertNotNull(channel);
	assertTrue(userPermissions.hasPermission("SPORTS"));
	assertNotNull(user);
    }

    /**
     * Do all of the above but also time out if we don't get all the results
     * back within 1200 milliseconds
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
	ExecutorService es = Executors.newCachedThreadPool();
	Future<Result> wholeOperation = es.submit(() -> {
	    Future<Channel> channelCallable = es.submit(() -> channelService.lookupChannel("SkySportsOne"));
	    User chbatey = userService.lookupUser("chbatey");
	    Permissions p = permissionsService.getPermissions(chbatey.getUserId());
	    try {
		Channel channel = channelCallable.get();
		return new Result(channel, p);
	    } catch (Exception e) {
		throw new RuntimeException("Oh dear", e);
	    }
	});
	result = wholeOperation.get(1200, TimeUnit.MILLISECONDS);

	assertNotNull(result.channel);
	assertTrue(result.permissions.hasPermission("SPORTS"));
    }
}
