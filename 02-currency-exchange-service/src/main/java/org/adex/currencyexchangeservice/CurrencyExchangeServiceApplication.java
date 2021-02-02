package org.adex.currencyexchangeservice;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * 
 * @author MED DOUHI
 *
 * @version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
public class CurrencyExchangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeServiceApplication.class, args);
	}

}

@RestController
@RequestMapping("/currency-exchange")
@RequiredArgsConstructor
@CrossOrigin
class CurrencyExchangeController {

	private final Environment env;
	private final ExchangeRepository exchangeRepository;

	@GetMapping("/all")
	public Flux<ExchangeValue> getAll() {
		return this.exchangeRepository.findAll().map(value -> {
			value.setPort(Integer.parseInt(env.getProperty("local.server.port")));
			return value;
		});
	}

	@GetMapping("/from/{from}/to/{to}")
	public Mono<ExchangeValue> fromCurrencyTo(@PathVariable String from, @PathVariable String to) {
		return this.exchangeRepository.findByFromCurrencyAndToCurrency(from, to).map(value -> {
			value.setPort(Integer.parseInt(env.getProperty("local.server.port")));
			return value;
		});
	}

}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
class ExchangeValue {

	@org.springframework.data.annotation.Id
	private String id;
	private String fromCurrency;
	private String toCurrency;
	private BigDecimal conversionMultiple;
	@Transient
	private int port;

}

interface ExchangeRepository extends ReactiveMongoRepository<ExchangeValue, String> {

	Mono<ExchangeValue> findByFromCurrencyAndToCurrency(String from, String to);

}

@Component
@RequiredArgsConstructor
@Log4j2
class Initializer {

	private final ExchangeRepository exchangeRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {

		Flux<ExchangeValue> values = Flux
				.just(new ExchangeValue(null, "usa", "dh", BigDecimal.valueOf(9), 0),
						new ExchangeValue(null, "eur", "dh", BigDecimal.valueOf(11), 0),
						new ExchangeValue(null, "dh", "usa", BigDecimal.valueOf(0.11), 0),
						new ExchangeValue(null, "dh", "eur", BigDecimal.valueOf(0.08), 0))
				.map(value -> value).flatMap(this.exchangeRepository::save);

		this.exchangeRepository.deleteAll().thenMany(values).thenMany(this.exchangeRepository.findAll())
				.subscribe(log::info);

	}

}
