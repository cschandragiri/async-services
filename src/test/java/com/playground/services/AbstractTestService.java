package com.playground.services;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.playground.entity.Channel;
import com.playground.entity.Permissions;
import com.playground.entity.Result;
import com.playground.entity.User;

public abstract class AbstractTestService {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected UserService userService;

    @Autowired
    protected PermissionsService permissionsService;

    @Autowired
    protected ChannelService channelService;

    protected Channel channel;
    protected User user;
    protected Permissions userPermissions;
    protected Result result;

    @Before
    public void setup() {
	channel = null;
	user = null;
	userPermissions = null;
	result = null;
    }
}
