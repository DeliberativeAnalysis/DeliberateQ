package com.github.deliberateq.qsort.gui;

public class NamedNumber {

	private String name;
	private Double value;

	public NamedNumber(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name;
	}
}
