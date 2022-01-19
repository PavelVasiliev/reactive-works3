package ru.innotech.education.rxjava;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class StreamProcessor {

    private static final Pattern WORDS_PATTERN = Pattern.compile("((\\b[^\\s]+\\b)((?<=\\.\\w).)?)");
    private static final String STORE_NAME = KeyDeduplicater.STORE_NAME;
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "distinct-words";

    private final Duration windowSize = Duration.ofMillis(10);
    private final Duration retentionPeriod = windowSize;

    final StoreBuilder<WindowStore<String, Long>> dedupStoreBuilder = Stores.windowStoreBuilder(
            Stores.persistentWindowStore(STORE_NAME,
                    retentionPeriod,
                    windowSize,
                    false
            ),
            Serdes.String(),
            Serdes.Long());

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        streamsBuilder.addStateStore(dedupStoreBuilder);

        streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
                .flatMapValues(
                        this::splitLine)
                .selectKey((key, word) -> word)
                .mapValues(word -> word)
                .transform(
                        () -> new KeyDeduplicater<>((key, value) -> value), STORE_NAME)
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
        return streamsBuilder.build();
    }

    private List<String> splitLine(String line) {
        line = line.toLowerCase(Locale.ROOT);
        Set<String> words = new HashSet<>();
        Matcher m = WORDS_PATTERN.matcher(line);
        while (m.find()) {
            words.add(m.group());
        }
        return new ArrayList<>(words);
    }

    @Bean
    public NewTopic inputTopic() {
        return TopicBuilder.name(INPUT_TOPIC).build();
    }

    @Bean
    public NewTopic outputTopic() {
        return TopicBuilder.name(OUTPUT_TOPIC).build();
    }
}