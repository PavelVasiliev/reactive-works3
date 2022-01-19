package ru.innotech.education.rxjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.innotech.education.rxjava.domain.ChannelEntity;
import ru.innotech.education.rxjava.repo.ChannelRepo;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepo channelRepo;

    public Mono<ChannelEntity> getByLink(String  link){
        return channelRepo.getByLink(link);
    }
}
