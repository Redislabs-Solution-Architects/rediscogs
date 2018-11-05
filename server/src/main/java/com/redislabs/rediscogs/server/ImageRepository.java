package com.redislabs.rediscogs.server;

public interface ImageRepository {

	byte[] getImage(String masterId);
}
