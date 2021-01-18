package com.ceti.demojfx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class DemoTableData {

	private String name;

	private Integer age;

	private Float amount;

	private String status;
}
