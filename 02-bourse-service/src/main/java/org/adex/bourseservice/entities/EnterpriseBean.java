package org.adex.bourseservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EnterpriseBean {

	private String id;
	private String name;
	private double price;
	private String seat;
	private int  collaboraters;
}