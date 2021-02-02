package org.adex.bourseservice;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.adex.bourseservice.entities.Enterprise;
import org.adex.bourseservice.entities.EnterpriseBean;
import org.adex.bourseservice.entities.Transaction;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;

import lombok.RequiredArgsConstructor;
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
@EnableEurekaClient
@EnableCircuitBreaker
public class BourseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BourseServiceApplication.class, args);
	}
}

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/bourses")
@RequiredArgsConstructor
class BourseController {

	@HystrixCommand(fallbackMethod = "getEnterprisesInAnyDelay")
	@GetMapping
	public Flux<EnterpriseBean> getEnterprises() {

		sleepToTestHystryx();

		return WebClient.create("http://localhost:3000").get().uri("/enterprises")
				.accept(MediaType.APPLICATION_STREAM_JSON).retrieve().bodyToFlux(EnterpriseBean.class);
	}

	@HystrixCommand(fallbackMethod = "getTransactionsInAnyDelay")
	@GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Transaction> getTransactionsByEnterprise(@PathVariable String id) {

		this.sleepToTestHystryx();

		Mono<EnterpriseBean> bean = WebClient.create("http://localhost:8080").get()
				.uri("/ENTERPRISE-SERVICE/enterprises/" + id).accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.bodyToMono(EnterpriseBean.class);

		return this.mapEnterpriseBean(bean);
	}

	private Flux<Transaction> getTransactionsInAnyDelay(@PathVariable String id) {
		return this.mapEnterpriseBean(this.getEnterpriseMonoIfErrorOrDelay(id));
	}

	private Flux<EnterpriseBean> getEnterprisesInAnyDelay() {
		return Flux.just(new EnterpriseBean("SG", "SG", 100 + Math.random() * 900, "Unknown", 0),
				new EnterpriseBean("Atos", "Atos", 100 + Math.random() * 900, "Unknown", 0));
	}

	private Mono<EnterpriseBean> getEnterpriseMonoIfErrorOrDelay(String id) {
		return this.getEnterprisesInAnyDelay().filter(enterprise -> enterprise.getId().equals(id)).single();
	}

	private Flux<Transaction> mapEnterpriseBean(Mono<EnterpriseBean> enterpriseMono) {
		return enterpriseMono.flatMapMany(enterprise -> Flux
				.fromStream(Stream.generate(() -> Transaction.builder().enterprise(enterprise)
						.price((new Random().nextInt((8000 - 6000) + 1) + 6000)).instant(Instant.now()).build()))
				.delayElements(Duration.ofSeconds(1))).share();
	}

	private void sleepToTestHystryx() {

		try {
			if (Math.random() > .5 && !Thread.interrupted())
				Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

/*
 * interface EnterpriseRepository extends
 * ReactiveMongoRepository<EnterpriseBean, String> {
 * 
 * }
 * 
 * @Component
 * 
 * @RequiredArgsConstructor
 * 
 * @Log4j2 class Initializer {
 * 
 * private final EnterpriseRepository enterpriseRepository;
 * 
 * @EventListener(ApplicationReadyEvent.class) public void initialize() {
 * 
 * Flux<EnterpriseBean> enterprises = Flux .just(new EnterpriseBean("SG", "SG",
 * 100 + Math.random() * 900, "Unknown", 0), new EnterpriseBean("Atos", "Atos",
 * 100 + Math.random() * 900, "Unknown", 0))
 * .flatMap(this.enterpriseRepository::save);
 * 
 * this.enterpriseRepository.deleteAll().thenMany(enterprises).thenMany(this.
 * enterpriseRepository.findAll()) .subscribe(log::info);
 * 
 * }
 * 
 * }
 */
