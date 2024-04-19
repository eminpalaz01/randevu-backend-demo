package com.dershaneproject.randevu.entities.concretes;

public enum Authority {
    ADMINISTRATOR(1), STAFF(2), TEACHER(3), STUDENT(4);
    private Integer value;

    Authority(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
