package ru.innotech.education.rxjava;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;

public class KeyDeduplicater<K, V, E> implements Transformer<K, V, KeyValue<K, V>> {
    public static final String STORE_NAME = "key-store";
    private ProcessorContext context;
    private final KeyValueMapper<K, V, E> idExtractor;
    private WindowStore<E, Long> store;

    public KeyDeduplicater(KeyValueMapper<K, V, E> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        store = context.getStateStore(STORE_NAME);
    }

    @Override
    public KeyValue<K, V> transform(K key, V value) {
        final E eventId = idExtractor.apply(key, value);
        if (eventId == null) {
            return KeyValue.pair(key, value);
        } else {
            final KeyValue<K, V> output;
            if (isDuplicate(eventId)) {
                output = null;
                updateKeyTime(eventId, context.timestamp());
            } else {
                output = KeyValue.pair(key, value);
                putNewKey(eventId, context.timestamp());
            }
            return output;
        }
    }

    private boolean isDuplicate(final E eventId) {
        final long eventTime = context.timestamp();
        final WindowStoreIterator<Long> timeIterator = store.fetch(
                eventId,
                eventTime,
                eventTime);
        final boolean isDuplicate = timeIterator.hasNext();
        timeIterator.close();
        return isDuplicate;
    }

    private void updateKeyTime(E eventId, long newTimestamp) {
        store.put(eventId, newTimestamp, newTimestamp);
    }

    private void putNewKey(E key, long timestamp) {
        store.put(key, timestamp, timestamp);
    }

    @Override
    public void close() {

    }
}
