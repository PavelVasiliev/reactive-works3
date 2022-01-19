package ru.innotech.education.rxjava.service;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import ru.innotech.education.rxjava.domain.ItemEntity;
import ru.innotech.education.rxjava.domain.SubscriptionEntity;
import ru.innotech.education.rxjava.models.Channel;
import ru.innotech.education.rxjava.models.Item;

public final class ModelMapper {

    @NotNull
    public static ItemEntity toEntity(@NotNull Integer subscriptionId, @NotNull Item item) {
        return new ItemEntity()
                .setTitle(parseHtml(item.getTitle()))
                .setLink(item.getLink())
                .setDescription(parseHtml(item.getDescription()))
                .setPublication(item.getPublication())
                .setSubscriptionId(subscriptionId);
    }

    @NotNull
    public static SubscriptionEntity toEntity(@NotNull String name, @NotNull String link, @NotNull Channel channel) {
        return new SubscriptionEntity()
                .setName(name)
                .setLink(link)
                .setTitle(channel.getTitle());
    }

    @NotNull
    public static Item toModel(@NotNull ItemEntity item) {
        return new Item()
                .setTitle(item.getTitle())
                .setLink(item.getLink())
                .setDescription(item.getDescription())
                .setPublication(item.getPublication());
    }

    @NotNull
    private static String parseHtml(@NotNull String text) {
        return Jsoup.parse(text).text();
    }
}