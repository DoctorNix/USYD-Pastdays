package soft2412.Currency;

import java.time.LocalDate;

public class RateRecord {
    private final double value;
    private final LocalDate date;

    public RateRecord(double value, LocalDate date) {
        this.value = value;
        this.date = date;
    }


    public double getValue() {return value;}
    public LocalDate getDate() {return date;}
}
