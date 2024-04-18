package com.dershaneproject.randevu.core.utilities.concretes;

import lombok.Getter;

@Getter
public class DataResult<T> extends Result {

	private final T data;

	public DataResult(T data) {
        super(null);
        this.data = data;
	}

	public DataResult(String message) {
		super(message);
		this.data = null;
	}
	
	public DataResult(T data, String message) {
		super(message);
		this.data = data;
	}

}
