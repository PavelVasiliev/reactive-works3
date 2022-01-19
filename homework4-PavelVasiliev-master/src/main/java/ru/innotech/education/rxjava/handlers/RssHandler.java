package ru.innotech.education.rxjava.handlers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.innotech.education.rxjava.domain.ChannelEntity;
import ru.innotech.education.rxjava.domain.SubscriptionEntity;
import ru.innotech.education.rxjava.models.ErrorResponse;
import ru.innotech.education.rxjava.models.Item;
import ru.innotech.education.rxjava.models.SubscriptionRequest;
import ru.innotech.education.rxjava.service.ChannelService;
import ru.innotech.education.rxjava.service.ItemService;
import ru.innotech.education.rxjava.service.SubsService;

@Tag(name = "RSS operations")
@Service
@Slf4j
public class RssHandler {
    private WebClient webClient = WebClient.create("http://localhost:8080/api/v1/rss/");

    @Autowired
    private SubsService subsService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ChannelService channelService;

    public RssHandler(SubsService subsService, ItemService itemService, ChannelService channelService) {
        this.subsService = subsService;
        this.itemService = itemService;
        this.channelService = channelService;
    }

    @NotNull
    @Operation(
            summary = "Subscribe to RSS feed",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = SubscriptionRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parsed data from RSS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
                    @ApiResponse(responseCode = "409", description = "RSS already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public Mono<ServerResponse> subscribe(@NotNull ServerRequest request) {
        String name = request.pathVariable("name");

        Mono<SubscriptionEntity> saveSub = subsService.save(
                new SubscriptionEntity(
                        name, request.path()))
                .log("saving new sub to channel => " + name);
        return subsService
                .getByName(name)
                .flatMap(sr -> onError(409, "RSS already exists"))
                .switchIfEmpty(getNewsForSub(saveSub));
    }

    private Mono<ServerResponse> getNewsForSub(Mono<SubscriptionEntity> monoSub) {
        return monoSub.flatMap(
                entity -> {
                    log.info("getting news for sub => " + entity.getLink());
                    Mono<ChannelEntity> ch = channelService.getByLink(entity.getLink()); //ToDo dome stuff..
                    return ch.flatMap(
                            channelEntity -> ServerResponse.ok().bodyValue(channelEntity.getItems()));
                }
        ).switchIfEmpty(onError(404, "RSS not found"));
    }

    @NotNull
    @Operation(
            summary = "Get updates for RSS feed",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parsed data from RSS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Item.class)))),
                    @ApiResponse(responseCode = "404", description = "RSS not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public Mono<ServerResponse> updates(@NotNull ServerRequest request) {
        return getNewsForSub(
                subsService.getByName(
                        request.pathVariable("name")));
    }

    @NotNull
    @Operation(
            summary = "Unsubscribe from RSS feed",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully unsubscribed", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "RSS not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public Mono<ServerResponse> unsubscribe(@NotNull ServerRequest request) {
        String name = request.pathVariable("name");
        return subsService.getByName(name)
                .flatMap(
                        sr -> ServerResponse
                                .noContent()
                                .build(
                                        subsService.delete(name))
                                .log("unsubscribed => " + sr.getLink()))
                .switchIfEmpty(
                        onError(404, "RSS not found"));
    }

    private Mono<ServerResponse> onError(int status, String message) {
        return ServerResponse
                .status(status)
                .bodyValue(BodyInserters.fromValue(
                        new ErrorResponse(message).getMessage()))
                .log("error occurred => " + status + " " + message);
    }
}
