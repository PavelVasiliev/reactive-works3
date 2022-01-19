package ru.innotech.education.rxjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.innotech.education.rxjava.domain.SubscriptionEntity;
import ru.innotech.education.rxjava.repo.SubsRepo;

@Service
public class SubsService {

    @Autowired
    private SubsRepo subsRepo;

    public Mono<SubscriptionEntity> save(SubscriptionEntity entity) {
        return subsRepo.save(entity);
    }

    public Mono<Void> delete(String name) {
        return subsRepo.deleteByName(name);
    }

    public Mono<SubscriptionEntity> getByName(String name) {
        return subsRepo.getByName(name);
    }
}
