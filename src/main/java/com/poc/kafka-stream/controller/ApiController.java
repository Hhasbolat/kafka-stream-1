package com.moneypay.KStream.controller;

import com.moneypay.KStream.model.BankTransaction;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/count/{bankCode}")
    public Double getWordCount(@PathVariable Long bankCode) {

        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<Long, BankTransaction> counts = kafkaStreams.store(
            StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore())
        );

        BankTransaction order = counts.get(bankCode);
        double percentage = (double) (order.getSuccessItems() * 100) / order.getTotalItems();
        return percentage;
    }
}
