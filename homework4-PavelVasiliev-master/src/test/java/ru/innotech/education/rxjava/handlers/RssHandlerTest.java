package ru.innotech.education.rxjava.handlers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.hamcrest.core.IsEqual;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.innotech.education.rxjava.config.DatabaseTestConfiguration;
import ru.innotech.education.rxjava.config.WireMockInitializer;
import ru.innotech.education.rxjava.models.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.util.List.of;
import static org.slf4j.LoggerFactory.getLogger;
import static org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(initializers = { WireMockInitializer.class })
@AutoConfigureWebTestClient
@Import(DatabaseTestConfiguration.class)
class RssHandlerTest {
    private static final Logger logger = getLogger(RssHandlerTest.class);
    private static final String NAME = "habr";

    @Autowired
    private WebTestClient webTestClient = WebTestClient.bindToServer().build();

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    void scenario() {
        final String link = "http://localhost:" + wireMockServer.port() + "/rss";
        final List<Item> items = new ArrayList<>(of(buildItem(), buildItem(), buildItem()));
        final RSS rss = buildRss(link, items);
        wireMockServer.stubFor(
                get("/rss")
                        .inScenario("Full Scenario")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, "application/atom+xml")
                                .withBody(convertToXml(rss, RSS.class)))
                        .willSetStateTo("Updates"));

        final List<Item> newItems = of(buildItem(), buildItem());
        items.addAll(newItems);
        rss.getChannel().setItem(items);
        wireMockServer.stubFor(
                get("/rss")
                        .inScenario("Full Scenario")
                        .whenScenarioStateIs("Updates")
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, "application/atom+xml")
                                .withBody(convertToXml(rss, RSS.class))));


        final SubscriptionRequest request =
                new SubscriptionRequest()
                        .setLink(link);

        logger.info("### Step 1: Subscribe to {} RSS: {}", NAME, link);
        webTestClient.post()
                .uri("/api/v1/rss/{name}/subscribe", NAME)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Item.class)
                .hasSize(3);

        logger.info("### Step 2: Try to subscribe to {} again, get 409 Conflict", NAME);
        webTestClient.post()
                .uri("/api/v1/rss/{name}/subscribe", NAME)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class)
                .value(ErrorResponse::getMessage, IsEqual.equalTo("Subscription '" + NAME + "' already exists"));

        logger.info("### Step 3: Get updates on {} RSS", NAME);
        webTestClient.get()
                .uri("/api/v1/rss/{name}/updates", NAME)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Item.class)
                .hasSize(2);

        logger.info("### Step 4: Unsubscribe from {}", NAME);
        webTestClient.delete()
                .uri("/api/v1/rss/{name}/unsubscribe", NAME)
                .exchange()
                .expectStatus()
                .isNoContent();

        logger.info("### Step 5: Get updates after unsubscription, get 404 Not Found");
        webTestClient.get()
                .uri("/api/v1/rss/{name}/updates", NAME)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponse.class)
                .value(ErrorResponse::getMessage, IsEqual.equalTo("Subscription '" + NAME + "' not found"));
    }

    @NotNull
    private RSS buildRss(@NotNull String link, @NotNull List<Item> items) {
        return new RSS()
                .setChannel(new Channel()
                        .setLink(link)
                        .setTitle(randomAlphabetic(8))
                        .setDescription(randomAlphabetic(8))
                        .setItem(items));
    }

    @NotNull
    private Item buildItem() {
        return new Item()
                .setTitle(randomAlphabetic(8))
                .setLink("https://" + randomAlphabetic(8) + "/news")
                .setDescription(randomAlphabetic(14))
                .setPublication(ZonedDateTime.now());
    }

    @NotNull
    private <T> String convertToXml(@NotNull T data, @NotNull Class<T> cls) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(cls);
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            final StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(data, writer);
            return writer.toString();
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
}