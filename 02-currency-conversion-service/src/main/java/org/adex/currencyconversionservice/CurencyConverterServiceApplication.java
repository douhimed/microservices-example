package org.adex.currencyconversionservice;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

/**
 * 
 * @author MED DOUHI
 *
 * @version 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CurencyConverterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurencyConverterServiceApplication.class, args);
	}

}

@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
class CurrencyConversionController {

	private final CurrencyExchangeProxy currencyExchangeProxy;

	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public Mono<CurrencyConversionBean> convertWithFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable int quantity) {

		return Mono.just(this.currencyExchangeProxy.getCurrencyExchangeValues(from, to)).map(b -> {
			b.setTotalCalculatedAmount(BigDecimal.valueOf(quantity * b.getConversionMultiple()));
			return b;
		});
	}
}

@FeignClient(name = "GATEWAY-SERVICE")
@RibbonClient(name = "CURRENCY-EXCHANGE-SERVICE")
interface CurrencyExchangeProxy {

	@GetMapping("/CURRENCY-EXCHANGE-SERVICE/currency-exchange/from/{from}/to/{to}")
	CurrencyConversionBean getCurrencyExchangeValues(@PathVariable String from, @PathVariable String to);

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
class CurrencyConversionBean {

	private String id;
	private String fromCurrency;
	private String toCurrency;
	private int quantity;
	private double conversionMultiple;
	private BigDecimal totalCalculatedAmount;
	private int port;

}