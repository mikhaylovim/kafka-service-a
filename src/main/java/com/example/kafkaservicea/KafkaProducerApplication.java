package com.example.kafkaservicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Random;

@SpringBootApplication
public class KafkaProducerApplication implements CommandLineRunner {

	private static final String TOPIC = "card_transactions";
	private static final int CUSTOMER_COUNT = 10000; // 10k customers
	private static final Random random = new Random();

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public static void main(String[] args) {
		SpringApplication.run(KafkaProducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Бесконечный цикл для демонстрации. В реальном приложении стоит ограничить.
		while (true) {
			int customerId = random.nextInt(CUSTOMER_COUNT);
			String transactionEvent = "CustomerID: " + customerId + ", Transaction Amount: " + (random.nextDouble() * 100);
			kafkaTemplate.send(TOPIC, transactionEvent);
			Thread.sleep(1000); // Задержка для имитации частоты событий.
		}
	}
}
