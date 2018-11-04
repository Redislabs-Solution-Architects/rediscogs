package com.redislabs.rediscogs;

public enum EntityType {
	Artists, Labels, Masters, Releases;

	public String id() {
		return name().toLowerCase();
	}
}
