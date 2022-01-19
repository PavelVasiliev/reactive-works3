package ru.innotech.education.rxjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.innotech.education.rxjava.domain.ItemEntity;
import ru.innotech.education.rxjava.repo.ItemRepo;

@Service
public class ItemService {

    @Autowired
    private ItemRepo itemRepo;

    public Flux<ItemEntity> getAll(){
        return itemRepo.findAll();
    }

    public Flux<ItemEntity> getAllByLink(String link) {
        return itemRepo.findAllByLink(link);
    }
}
