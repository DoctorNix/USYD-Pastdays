package soft2412.Currency;
// package soft2412.Currency; // for later use
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class Rate {
    private double currentValue;
    private LocalDate currentDate;
    private ArrayList<RateRecord> previousRates = new ArrayList<RateRecord>();


    public Rate(double value, LocalDate date) {
        this.currentValue = value;
        this.currentDate = date;
    }
    public double getValue() {
        return currentValue;
    }
    public LocalDate getDate() {
        return currentDate;
    }

    public void addRateToRecord(double newValue, LocalDate newDate) {
        RateRecord oldRate = new RateRecord(newValue, newDate);
        previousRates.add(oldRate);
    }


    public void updateRate(double newValue, LocalDate newDate) {
        this.currentValue = newValue;
        this.currentDate = newDate;
    }

    public String getHistory(LocalDate startDate, LocalDate endDate, String fromCurrency, String toCurrency) {
        StringBuilder history = new StringBuilder();
        ArrayList<Double> allRates = new ArrayList<Double>();
        for (RateRecord record : previousRates) {
            if((record.getDate().isBefore(endDate) && record.getDate().isAfter(startDate)) || startDate.isEqual(record.getDate()) || endDate.isEqual(record.getDate())){
                history.append(String.format("%.4f (Date: %s);", record.getValue(), record.getDate()));
                allRates.add(record.getValue());
            }
        }
        if((currentDate.isBefore(endDate) && currentDate.isAfter(startDate)) || startDate.isEqual(currentDate) || endDate.isEqual(currentDate)){
            history.append(String.format("%.4f (Date: %s);", currentValue, currentDate));
            allRates.add(currentValue);
        }

        if(allRates.size() == 0){
            return "";
        }

        double mean = mean(allRates);
        double median = median(allRates);
        double mode = mode(allRates);

        double sd = standardDeviation(allRates, mean);

        double max = Collections.max(allRates);
        double min = Collections.min(allRates);

        // Appending statistics to the history
        history.append("\n");
        history.append("Mean: " + mean + "\n");
        history.append("Median: " + median + "\n");
        history.append("Mode: " + mode + "\n");
        history.append("Standard Deviation: " + sd + "\n");
        history.append("Maximum Rate: " + max + "\n");
        history.append("Minimum Rate: " + min + "\n");

        // Returning the history as a string
        return history.toString();
    }

    double standardDeviation(ArrayList<Double> allRates, double mean) {
        double total = 0.0;
        for(double rate : allRates){
            total += Math.pow(rate - mean, 2);
        }
        double newMean = total / allRates.size();
        return Math.sqrt(newMean);
    }

    double mean(ArrayList<Double> allRates) {
        double sum = 0;
        for (double rate : allRates) {
            sum += rate;
        }
        return sum / allRates.size();
    }

    double median(ArrayList<Double> allRates) {
        if(allRates.size() == 1){return allRates.get(0);}

        int middle = allRates.size() / 2;
        if (allRates.size() % 2 == 1) {
            return allRates.get(middle);
        }
        else{
            return (allRates.get(middle - 1) + allRates.get(middle)) / 2.0;
        }
    }

    double mode(ArrayList<Double> allRates) {
        double maxRate = -1.0;
        int maxCount = 0;
        for (double currentRate : allRates) {
            int currentCount = 0;
            for(double rate : allRates){
                if(currentRate == rate){
                    currentCount++;
                }
            }
            if(currentCount > maxCount){
                maxRate = currentRate;
                maxCount = currentCount;
            }
        }
        return maxRate;
    }

    public double lastRateToRecord() {
        RateRecord most_recent = null;
        for (RateRecord record : previousRates) {
            if (most_recent == null){
                most_recent = record;
                continue;
            }

            if (record.getDate().isAfter(most_recent.getDate())){
                most_recent = record;
            }
        }
        if (most_recent == null) {
            return -1.0;
        }
        return most_recent.getValue();

    }
}
