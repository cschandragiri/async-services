package com.playground.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.playground.entity.Channel;
import com.playground.entity.Permissions;
import com.playground.entity.User;

@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class VanillaFutures extends AbstractTestService {

    /**
     * Scenario: A web request comes in asking if chbatey has the SPORTS
     * permission
     * <p>
     * Questions: - Does the user exist? - Is the user allowed to watch the
     * channel?
     */
    @Test
    public void chbatey_has_sports_blocking() throws Exception {
	Future<User> fUser = userService.lookupUserAsync("chbatey");

	// Make the blocking explicit
	User chbatey = fUser.get();

	Future<Permissions> fPermission = permissionsService.getPermissionsAsync(chbatey.getUserId());

	// Explicit blocking
	userPermissions = fPermission.get();

	assertTrue(userPermissions.hasPermission("SPORTS"));

    }

    /**
     * Scenario: A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions: - Does this channel exist? - Is chbatey a valid user? - Does
     * chbatey have the permissionsService to watch Sports?
     */
    @Test
    public void chbatey_watch_sky_sports_one_blocking() throws Exception {
	Future<User> fUser = userService.lookupUserAsync("chbatey");

	// Make the blocking explicit
	user = fUser.get();

	Future<Permissions> fPermissions = permissionsService.getPermissionsAsync(user.getUserId());

	// Explicit blocking
	userPermissions = fPermissions.get();

	Future<Channel> fChannel = channelService.lookupChannelAsync("SkySportsOne");

	// Explicit blocking
	channel = fChannel.get();

	assertNotNull(channel);
	assertTrue(userPermissions.hasPermission("SPORTS"));
	assertNotNull(user);

    }

    /**
     * Scenario: A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions: - Does this channel exist? - Is chbatey a valid user? - Does
     * chbatey have the permissionsService to watch Sports?
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one_concurrent() throws Exception {
	Future<Channel> fChannel = channelService.lookupChannelAsync("SkySportsOne");

	Future<User> fUser = userService.lookupUserAsync("chbatey");

	// Make the blocking explicit
	user = fUser.get();

	Future<Permissions> fPermissions = permissionsService.getPermissionsAsync(user.getUserId());

	// Explicit blocking
	userPermissions = fPermissions.get();

	// Explicit blocking
	channel = fChannel.get();

	assertNotNull(channel);
	assertTrue(userPermissions.hasPermission("SPORTS"));
	assertNotNull(user);

    }

    @Test
    public void chbatey_watch_sky_sports_one_concurrent_no_blocking() throws Exception {
	Future<Channel> fChannel = channelService.lookupChannelAsync("SkySportsOne");
	Future<User> fUser = userService.lookupUserAsync("chbatey");
	// ??
	// Future<Permissions> pFuture =
	// permissionsService.permissionsAsync(chbatey.getUserName());
    }
}
