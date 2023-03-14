package com.dershaneproject.randevu.core.utilities.concretes;

//Bunları projeye ekle. Düzgün ve anlaşılır bir veri aktarımı için.
public class Result {
	private boolean success;
	private String message;

	public Result(boolean success) {
		this.success = success;
	}

	public Result(boolean success, String message) {
		this(success);
		this.message = message;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
