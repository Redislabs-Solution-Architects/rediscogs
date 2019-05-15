package com.redislabs.rediscogs;

import java.time.Instant;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.redislabs.rediscogs.model.Album;
import com.redislabs.rediscogs.model.Like;
import com.redislabs.rediscogs.model.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LikeService {

	@Autowired
	private RediscogsProperties config;
	@Autowired
	private StringRedisTemplate template;

	public Stream<Like> likes() {
		return template.opsForStream().range(Like.class, config.getLikesStream(), Range.unbounded(),
				Limit.limit().count(config.getMaxLikes())).stream().map(m -> m.getValue());
	}

	public void like(Album album, User user, String userAgent) {
		Like like = Like.builder().album(album).user(user).userAgent(userAgent).time(Instant.now()).build();
		RecordId recordId = template.opsForStream().add(ObjectRecord.create(config.getLikesStream(), like));
		log.info("Liked album {} - RecordId: {}", like.getAlbum().getId(), recordId.getValue());
	}

}
