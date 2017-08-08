package com.playground.entity;

import java.util.Objects;

public class Result {
	public Channel channel;
    public Permissions permissions;

    public Result(Channel channel, Permissions permissions) {
        this.channel = channel;
        this.permissions = permissions;
    }

    public Channel getChannel() {
        return channel;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(channel, result.channel) &&
                Objects.equals(permissions, result.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, permissions);
    }
}
