package com.redislabs.rediscogs;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.RediSearchCommands;

import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.XReadArgs.StreamOffset;
import lombok.Setter;

@Component
public class LikeConsumer extends Thread {

	@Autowired
	private RediscogsConfiguration config;
	@Setter
	private boolean stopped;
	@Autowired
	private SimpMessageSendingOperations sendingOperations;
	@Autowired
	private AlbumMarshaller marshaller;
	@Autowired
	private RediSearchClient client;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		RediSearchCommands<String, String> commands = client.connect().sync();
		XReadArgs xargs = XReadArgs.Builder.block(Duration.ofSeconds(1));
		StreamOffset<String> stream = StreamOffset.latest(config.getLikesStream());
		while (!stopped) {
			while (true) {
				for (StreamMessage<String, String> message : commands.xread(xargs, stream)) {
					sendingOperations.convertAndSend(config.getLikesTopic(), marshaller.albumLike(message));
				}
			}
		}
	}

}
