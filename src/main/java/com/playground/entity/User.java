package com.playground.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class User {
    private String name;
    private String userName;
    private int userId;

    public User(String name, String userName, int userId) {
	this.name = name;
	this.userName = userName;
	this.userId = userId;
    }

    public String getName() {
	return name;
    }

    public String getUserName() {
	return userName;
    }

    public int getUserId() {
	return userId;
    }

    @Override
    public String toString() {
	return MoreObjects.toStringHelper(this).add("userName", userName).add("name", name).add("userId", userId)
		.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;

	User user = (User) o;
	if (userId != user.userId)
	    return false;
	if (name != null ? !name.equals(user.name) : user.name != null)
	    return false;
	return userName != null ? userName.equals(user.userName) : user.userName == null;
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(this.userName, this.name, this.userId);
    }
}
