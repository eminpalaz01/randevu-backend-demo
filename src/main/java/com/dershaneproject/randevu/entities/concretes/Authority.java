package com.dershaneproject.randevu.entities.concretes;

public enum Authority {
    ADMINISTRATOR(1), STAFF(2);
    private int value;

    Authority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
