package ru.innotech.education.rxjava.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.innotech.education.rxjava.domain.SubscriptionEntity;

@Repository
public interface SubsRepo extends ReactiveCrudRepository<SubscriptionEntity, Integer> {

    Mono<Void> deleteByName(String name);

    Mono<SubscriptionEntity> getByName(String name);
}
