package com.poc.kafka.config;

import com.poc.kafka.model.BankResponse;
import com.poc.kafka.model.BankTransaction;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Slf4j
@Configuration
public class KafkaItemStreamOne {

    @Bean
    public KStream<String, BankTransaction> kStreamItem(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var jsonSerde = new JsonSerde<>(BankResponse.class);
        var bankTransactionJsonSerde = new JsonSerde<>(BankTransaction.class);

        KStream<Windowed<String>, BankTransaction> stream = builder
            .stream("a", Consumed.with(stringSerde, jsonSerde))
            .map((key, value) -> KeyValue.pair(value.getBankCode().toString(), value))
            .groupByKey(Grouped.with(stringSerde, jsonSerde))
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(20L)))
            .aggregate(BankTransaction::new,
                       (key, value, agg) -> BankTransaction.builder()
                           .bankCode(Long.valueOf(key))
                           .totalItems(agg.getTotalItems() + 1)
                           .successItems(value.getStatus().equals("SUCCESS") ? agg.getSuccessItems() + 1 : agg.getSuccessItems())
                           .build(), Materialized.<String, BankTransaction, WindowStore<Bytes, byte[]>>as("counts")
                           .withKeySerde(Serdes.String())
                           .withValueSerde(bankTransactionJsonSerde))

            .toStream()
            .peek((key, value) -> log.info("Banka şuanda bu idli kayıtları okuyor.. bank code: {}: ", value.getBankCode()));

        KStream<String, BankTransaction> map = stream.map((key, value) -> KeyValue.pair(key.key(), value));

        map.to("b", Produced.with(Serdes.String(), bankTransactionJsonSerde));

        KStream<String, String> stringDoubleKStream = map.mapValues(
            (readOnlyKey, value) -> String.valueOf( (double) value.getSuccessItems() * 100 / value.getTotalItems()));

        stringDoubleKStream.to("percentage",  Produced.with(Serdes.String(), Serdes.String()));

        return map;
    }
}
