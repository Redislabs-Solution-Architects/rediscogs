package com.redislabs.rediscogs.loader;

public enum EntityType {
	Artists, Labels, Masters, Releases;

	public String id() {
		return name().toLowerCase();
	}
}
