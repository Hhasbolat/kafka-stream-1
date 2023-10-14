package com.poc.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;

import static org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME;

@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
@Configuration
public class KafkaStreamsConfig {

    @Configuration
    @EnableKafkaStreams
    public class KafkaStreamConfig {

        @Bean(name = DEFAULT_STREAMS_CONFIG_BEAN_NAME)
        public KafkaStreamsConfiguration kafkaStreamsConfig() {
            var props = new HashMap<String, Object>();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-item-1");
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                    Serdes.String().getClass().getName());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                    Serdes.String().getClass().getName());
            return new KafkaStreamsConfiguration(props);
        }
    }
}