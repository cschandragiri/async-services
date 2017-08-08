package com.playground.entity;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class Permissions {

    public static Permissions permissions(String... permissions) {
	return new Permissions(Sets.newHashSet(permissions));
    }

    private Set<String> permissions;

    Permissions(Set<String> permissions) {
	this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
	return permissions.contains(permission);
    }

    @Override
    public String toString() {
	return MoreObjects.toStringHelper(this).add("permissions", permissions).toString();
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(permissions);
    }

    @Override
    public boolean equals(Object object) {
	if (object instanceof Permissions) {
	    Permissions that = (Permissions) object;
	    return Objects.equal(this.permissions, that.permissions);
	}
	return false;
    }

}
