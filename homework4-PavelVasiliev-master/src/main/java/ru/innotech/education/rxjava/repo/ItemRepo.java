package ru.innotech.education.rxjava.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.innotech.education.rxjava.domain.ItemEntity;

@Repository
public interface ItemRepo extends ReactiveCrudRepository<ItemEntity, Integer> {

    Flux<ItemEntity> findAllByLink(String link);
}
