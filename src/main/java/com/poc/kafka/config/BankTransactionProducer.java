package com.poc.kafka.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kafka.model.BankResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class BankTransactionProducer {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static void main(String[] args) {
        KafkaProducer<String, String> bankTransactionProducer =
                new KafkaProducer<>(Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
                ));


        List<BankResponse> data1 = List.of(
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build(),
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build(),
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build(),
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build(),
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build(),
            BankResponse.builder()
                .bankCode(16L)
                .id("100L")
                .status("SUCCESS")
                .build()
        );

       /* data1.stream()
                .map(bankTransaction -> new ProducerRecord<>("a", bankTransaction.getBankCode().toString(), toJson(bankTransaction)))
                .forEach(record -> send(bankTransactionProducer, record));*/

        BankResponse bankTransaction = BankResponse.builder()
            .bankCode(16L)
            .id("100")
            .status("SUCCESS")
            .build();

        //send(bankTransactionProducer, new ProducerRecord<>("a", bankTransaction.getBankCode().toString(), toJson(bankTransaction)));

        while (true) {
            Thread.sleep(5000L);
            Stream.of(BankResponse.builder()
                          .bankCode(4L)
                          .id(UUID.randomUUID().toString())
                          .status("SUCCESS")
                          .build())
                .peek(t -> log.info("Sending new Transaction: {}", t))
                .map(t -> new ProducerRecord<>("a", t.getBankCode().toString(), toJson(t)))
                .forEach(record -> send(bankTransactionProducer, record));
        }

    }

    @SneakyThrows
    private static void send(KafkaProducer<String, String> bankTransactionProducer, ProducerRecord<String, String> record) {
        bankTransactionProducer.send(record).get();
    }

    @SneakyThrows
    private static String toJson(BankResponse bankTransaction) {
        return OBJECT_MAPPER.writeValueAsString(bankTransaction);
    }
}