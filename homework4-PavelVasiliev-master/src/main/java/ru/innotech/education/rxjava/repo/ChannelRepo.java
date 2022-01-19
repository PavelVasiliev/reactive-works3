package ru.innotech.education.rxjava.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.innotech.education.rxjava.domain.ChannelEntity;

@Repository
public interface ChannelRepo extends ReactiveCrudRepository<ChannelEntity, Integer> {

    Mono<ChannelEntity> getByLink(String link);
}
