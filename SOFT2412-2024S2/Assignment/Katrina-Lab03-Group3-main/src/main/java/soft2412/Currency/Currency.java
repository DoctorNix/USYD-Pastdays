package soft2412.Currency; // this is use for my own as it conduct in the local root

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Map;

public class Currency {
    private String currencySymbol;
    private Map<String, Rate> conversionRates = new HashMap<>();

    public Currency(String name) {
        this.currencySymbol = name;
    }

    public void addRate(String toCurrency, double value, LocalDate date) {
        Rate existingRate = conversionRates.get(toCurrency);
        if (existingRate == null) {
            conversionRates.put(toCurrency, new Rate(value, date));
        } else {
            if (date.isAfter(existingRate.getDate()) || date.isEqual(existingRate.getDate())) {
                existingRate.addRateToRecord(existingRate.getValue(), existingRate.getDate());
                existingRate.updateRate(value, date);
            } else if (date.isBefore(existingRate.getDate())){
                existingRate.addRateToRecord(value,date);
            }
        }
    }

    public Map<String, Rate> getRates() {
        return new HashMap<>(conversionRates);
    }

    public double getLatestRate(String toCurrency) {
        Rate conversionRate = conversionRates.get(toCurrency);
        if (conversionRate != null) {
            return conversionRate.getValue();
        }
        return -1.0;
    }


    public void writeRateToFile(String toCurrency, double value, LocalDate date) {
        // handling error if no valid rate get write into the file
        if (value == -1.0) {
            return;
        }
        String testPath = "src/TestDatabase/Currencies";
        // in case the main database's data get alter again
        String currencyPath = (testPath != null) ? testPath : "src/Database/Currencies";
        String filename = currencyPath + "/" + this.currencySymbol + ".txt";
        // Limit the rate's precision to 2 decimal places
        String formattedRate = String.format("%.2f", value);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true))){
            String line = toCurrency + "," + formattedRate + "," + date + "\n";
            bufferedWriter.write(line);
        } catch (IOException e) {
            System.out.println("failed to write file");
        }
    }

    public double getPreviousRate(String toCurrency) {
        Rate conversionRate = conversionRates.get(toCurrency);
        if (conversionRate != null) {
            return conversionRate.lastRateToRecord();
        }
        return -1.0;

    }

    public String getCurrencySymbol() {return currencySymbol;}
}
