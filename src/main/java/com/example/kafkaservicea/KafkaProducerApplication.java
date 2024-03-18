package com.example.kafkaservicea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Random;

@SpringBootApplication
public class KafkaProducerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducerApplication.class);

	@Value("${app.event-generation-delay}")
	private long eventGenerationDelay;

	@Value("${app.customer-count}")
	private int customerCount;

	private static final String TOPIC = "card_transactions";
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
			int customerId = random.nextInt(customerCount);
			String transactionEvent = "CustomerID: " + customerId + ", Transaction Amount: " + (random.nextDouble() * 100);
			kafkaTemplate.send(TOPIC, transactionEvent);
			logger.info(transactionEvent + " sended");
			Thread.sleep(eventGenerationDelay); // Задержка для имитации частоты событий.
		}
	}
}
