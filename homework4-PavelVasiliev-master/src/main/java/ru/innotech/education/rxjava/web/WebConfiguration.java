package ru.innotech.education.rxjava.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.innotech.education.rxjava.handlers.RssHandler;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.reactive.function.client.ExchangeStrategies.builder;

@Configuration
public class WebConfiguration {
    private static final int MAX_SIZE = 32 * 1024 * 1024;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    method = POST,
                    path = "/api/v1/rss/{name}/subscribe",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = RssHandler.class,
                    beanMethod = "subscribe"
            ),
            @RouterOperation(
                    method = GET,
                    path = "/api/v1/rss/{name}/updates",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = RssHandler.class,
                    beanMethod = "updates"
            ),
            @RouterOperation(
                    method = DELETE,
                    path = "/api/v1/rss/{name}/unsubscribe",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = RssHandler.class,
                    beanMethod = "unsubscribe"
            )
    })
    public RouterFunction<ServerResponse> rss(RssHandler rssHandler) {
        return RouterFunctions.route()
                .path("/api/v1/rss", builder -> {
                    builder.POST("/{name}/subscribe", rssHandler::subscribe);
                    builder.GET("/{name}/updates", rssHandler::updates);
                    builder.DELETE("/{name}/unsubscribe", rssHandler::unsubscribe);
                })
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(builder()
                        .codecs(configurer -> {
                            configurer.defaultCodecs().maxInMemorySize(MAX_SIZE);
                            configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                            configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                        })
                        .build())
                .build();
    }
}
