package ru.innotech.education.rxjava;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.innotech.education.rxjava.models.Channel;
import ru.innotech.education.rxjava.models.Item;
import ru.innotech.education.rxjava.models.RSS;

import java.time.ZonedDateTime;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@SpringBootApplication
public class RssServerApplication {
/*
     static String link = "http://localhost:8080/rss";
     static List<Item> items = new ArrayList<>(of(buildItem(), buildItem(), buildItem()));
     static RSS rss = buildRss(link, items);
*/
    public static void main(String[] args) {
/*
        List<Item> items = rss.getChannel().getItem();
        System.out.println(rss.getChannel());
        for(Item i : items){
            System.out.println(i);
        }
*/
        SpringApplication.run(RssServerApplication.class, args);
    }
/*
    private static RSS buildRss(@NotNull String link, @NotNull List<Item> items) {
        return new RSS()
                .setChannel(new Channel()
                        .setLink(link)
                         .setTitle(randomAlphabetic(8))
                        .setDescription(randomAlphabetic(8))
                        .setItem(items));
    }

    @NotNull
    private static Item buildItem() {
        return new Item()
                .setTitle(randomAlphabetic(8))
                .setLink("https://" + randomAlphabetic(8) + "/news")
                .setDescription(randomAlphabetic(14))
                .setPublication(ZonedDateTime.now());
    }
*/
}
