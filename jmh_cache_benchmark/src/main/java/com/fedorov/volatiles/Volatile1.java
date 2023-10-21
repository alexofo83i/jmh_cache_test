package com.fedorov.volatiles;

public class Volatile1 extends IVolatile {
    private int years;
    private int months;
    private /*volatile*/ int days;

    public int  totalDays() {
        int total = this.days;
        total += months * 30;
        total += years * 365;
        return total;
    }

    public void update(int years, int months, int days){
		this.months = months;
		this.years  = years;
        this.days   = days;
    }
}