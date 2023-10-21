package com.fedorov.volatiles;

public abstract class IVolatile {

    public static final int MAX_DAYS_IN_MONTH = 30;
    public static final int MAX_DAYS_IN_YEAR = 365;

    public abstract  int totalDays();
    public abstract void update(int years, int months, int days);

    public static int getTotal(int days, int months, int years){
        int total = MAX_DAYS_IN_YEAR*years + MAX_DAYS_IN_MONTH*months + days;
        return total;
    }
}
