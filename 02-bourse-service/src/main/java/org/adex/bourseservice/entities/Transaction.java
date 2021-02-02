package org.adex.bourseservice.entities;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Transaction {

	private String id;
	private Instant instant;
	private double price;
	@JsonProperty(access = Access.WRITE_ONLY)
	private EnterpriseBean enterprise;

}
