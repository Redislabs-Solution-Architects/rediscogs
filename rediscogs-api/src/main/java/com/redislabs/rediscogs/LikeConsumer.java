package com.redislabs.rediscogs;

import java.time.Duration;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.redislabs.rediscogs.model.Like;

import lombok.Setter;

@Component
public class LikeConsumer extends Thread {

	@Autowired
	private RediscogsProperties config;
	@Setter
	private boolean stopped;
	@Autowired
	private SimpMessageSendingOperations sendingOperations;
	@Autowired
	private StringRedisTemplate template;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		StreamReadOptions options = StreamReadOptions.empty().block(Duration.ofMillis(100));
		StreamOffset<String> offset = StreamOffset.latest(config.getLikesStream());
		while (!stopped) {
			template.opsForStream().read(Like.class, options, offset).forEach(r -> {
				Like like = r.getValue();
				sendingOperations.convertAndSend(config.getStomp().getLikesTopic(), like);
				template.opsForZSet().incrementScore(config.getAlbumStatsKey(), like.getAlbum().getTitle(), 1);
				template.opsForZSet().incrementScore(config.getArtistStatsKey(), like.getAlbum().getArtist(), 1);
				template.opsForZSet().incrementScore(config.getUserAgentStatsKey(), like.getUserAgent(), 1);
			});
		}
	}

	@PreDestroy
	public void shutdown() {
		setStopped(true);
	}

}