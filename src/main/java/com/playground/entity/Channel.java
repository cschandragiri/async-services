package com.playground.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Channel {
    private String name;

    public Channel(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    @Override
    public String toString() {
	return MoreObjects.toStringHelper(this).add("name", name).toString();
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object object) {
	if (object instanceof Channel) {
	    Channel that = (Channel) object;
	    return Objects.equal(this.name, that.name);
	}
	return false;
    }

}
