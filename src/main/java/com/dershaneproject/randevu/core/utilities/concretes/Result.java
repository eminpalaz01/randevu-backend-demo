package com.dershaneproject.randevu.core.utilities.concretes;

import lombok.Getter;

@Getter
public class Result {

	private final String message;

	public Result(String message) {
		if (message == null || message.isEmpty())
			this.message = "Genel Hata";
		else
			this.message = message;
	}
}
