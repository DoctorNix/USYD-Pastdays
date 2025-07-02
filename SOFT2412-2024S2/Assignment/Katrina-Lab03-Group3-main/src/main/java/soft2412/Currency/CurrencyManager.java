package soft2412.Currency;

import javax.management.InstanceNotFoundException;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CurrencyManager {
    public HashMap<String, Currency> currencies = new HashMap<>();
    public ArrayList<String> popularCurrencies= new ArrayList<>();

    private int sizeofpopular = 4;
    private String currencyPath= "src/Database/Currencies";
    private String popPath = "Popular.txt";

    public void setpaths(String currencyPath, String popPath) {
        this.currencyPath = currencyPath;
        this.popPath = popPath;
    }

    public void loadCurrencies() {

        File dir = new File(currencyPath);
        File[] files = dir.listFiles();

        if (files == null) {
            System.out.println("No files found in the directory.");
            return;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                if (file.getName().equals(popPath)){
                    continue;
                }

                String currencyName = file.getName().replace(".txt", "");

                // create a currency object for each file
                Currency currency = new Currency(currencyName);
                currencies.put(currencyName, currency);

                String line;
                while ((line = reader.readLine()) != null){
                    String[] parts = line.split(",");
                    String toCurrency = parts[0];
                    double value = Double.parseDouble(parts[1]);
                    LocalDate date = LocalDate.parse(parts[2]);

                    currency.addRate(toCurrency, value, date);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        get_popular();
    }

    private void get_popular(){
        try(BufferedReader reader = new BufferedReader(new FileReader(currencyPath+"/"+popPath))) {

            String line;
            while ((line = reader.readLine()) != null){

                if (currencies.get(line) == null){
                    continue;
                }

                popularCurrencies.add(line);
            }
        } catch (IOException e) {
            System.out.println("No popular file found.");
        }

        while (popularCurrencies.size() < sizeofpopular){
                popularCurrencies.add(null);
        }
    }

    public Currency getCurrency(String currencyName) {
        return currencies.get(currencyName);
    }

    public void addCurrencyRate(String fromCurrency, String toCurrency, double rate, LocalDate date) {
        Currency currency = getCurrency(fromCurrency);
        if (currency == null) {
            currency = new Currency(fromCurrency);
            currencies.put(fromCurrency, currency);
        }
        currency.addRate(toCurrency, rate, date);
        currency.writeRateToFile(toCurrency, rate, date);
    }

    public void processMenuAddRate(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the from currency: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String fromCurrency2 = scanner.nextLine().toUpperCase();

        if (!currencyExist(fromCurrency2)) {
            System.out.println("Currency " + fromCurrency2 + " does not exist.");
            return;
        }

        System.out.print("Enter the to currency: ");
        String toCurrency2 = scanner.nextLine().toUpperCase();
        if (!currencyExist(toCurrency2)) {
            System.out.println("Currency " + toCurrency2 + " does not exist.");
            return;
        }

        System.out.print("Enter the exchange value: ");
        double value;
        
        try {
            value = Double.parseDouble(scanner.nextLine());
            // Check if the exchange rate is negative
            if (value <= 0) {
                System.out.println("Exchange rate cannot be negative or zero.");
                return;  // Exit early if the exchange rate is negative
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid exchange rate input.");
            throw e;
        }

        System.out.print("Enter the date: ");
        LocalDate date;
        date = LocalDate.parse(scanner.nextLine());

        addCurrencyRate(fromCurrency2, toCurrency2, value, date);
        System.out.println("Added rate successfully.");
    }

    public boolean currencyExist(String currencyName) {
        return currencies.containsKey(currencyName);
    }

    public ArrayList<Object> convertCurrency(double amount, String fromCurrency, String toCurrency) {
        Currency currency = currencies.get(fromCurrency);
        Currency toCurrencyObject = currencies.get(toCurrency);
        ArrayList<Object> outList = new ArrayList<>();
        if (currency != null) {
            Double rate = currency.getLatestRate(toCurrency);
            if (rate != -1.0) {
                outList.add(amount * rate);
                outList.add(toCurrencyObject.getCurrencySymbol());
            }
            else{
                outList.add(-1.0);
                outList.add("Not Found");
            }
        }
        else{
            outList.add(-1.0);
            outList.add("Not Found");
        }
        return outList;
    }

    public void addCurrency(String newCurrency, String initialCurrency, double initialRate, LocalDate date) throws Exception{
        if (!currencies.containsKey(newCurrency)) {
            Currency newCurrencyObject = new Currency(newCurrency);
            newCurrencyObject.addRate(initialCurrency, initialRate, date);

            Currency initialCurrencyObject = currencies.get(initialCurrency);
            if(initialCurrencyObject == null){
                throw new InstanceNotFoundException();
            }

            initialCurrencyObject.addRate(newCurrency, 1/initialRate, date);

            currencies.put(newCurrency, newCurrencyObject);

            for(String key : currencies.keySet()) {
                if(key.equals(newCurrency) || key.equals(initialCurrency)) {
                    continue;
                }
                double newRate = (double)convertCurrency(initialRate, initialCurrency, key).get(0);
                newCurrencyObject.addRate(key, newRate, date);

                Currency currentCurrency = currencies.get(key);
                currentCurrency.addRate(newCurrency, 1/newRate, date);
            }

            writeCurrencyToFile(newCurrencyObject);
        }
    }

    private void writeCurrencyToFile(Currency currency) {
        String newfilename = currencyPath + "/" + currency.getCurrencySymbol() + ".txt";
        try {
            FileWriter fileWriter = new FileWriter(newfilename);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            Map<String, Rate> rates = currency.getRates();

            for (String rateCurrency : rates.keySet()) {
                Rate rate = rates.get(rateCurrency);
                //  handling invalid rates get write to the file
                if(rate.getValue() > 0){
                    currency.writeRateToFile(rateCurrency, rate.getValue(), rate.getDate());
                }
            }

            writer.close();

            for (String existingCurrency : currencies.keySet()) {
                if (!existingCurrency.equals(currency.getCurrencySymbol())) {
                    Currency existingCurrencyObject = currencies.get(existingCurrency);

                    Rate reverseRate = existingCurrencyObject.getRates().get(currency.getCurrencySymbol());
                    if(reverseRate != null && reverseRate.getValue() != -1.0){
                        String existingFile = currencyPath + "/" + existingCurrency + ".txt";
                        try (BufferedWriter newwriter = new BufferedWriter(new FileWriter(existingFile, true))){
//                            FileWriter newfileWriter = new FileWriter(existingFile, true);
//                            BufferedWriter newwriter = new BufferedWriter(newfileWriter);
                            newwriter.write(currency.getCurrencySymbol() + "," + String.format("%.2f", reverseRate.getValue()) + "," + reverseRate.getDate().toString() + "\n");
                        } catch (IOException e){
                            System.err.println("cant write to other file"+existingFile);
                        }
                    }

                }
            }

        } catch (IOException e) {
            System.err.println("cant write to file"+ newfilename);
        }
    }

    public void setPopularCurrencies() {
        System.out.println("Which would you like to swap?: ");
        for (int i = 1; i <= popularCurrencies.size(); i++) {
            System.out.println(i+"."+popularCurrencies.get(i-1));
        }
        System.out.println("5.Exit");

        Scanner input = new Scanner(System.in);

        if (!input.hasNextLine()){
            return;
        }

        int choice;

        try {
            choice = Integer.parseInt(input.nextLine());
        }
        catch (Exception e) {
            System.out.println("invalid input");
            return;
        }

        if (choice > popularCurrencies.size() || choice < 1) {
            System.out.println("Invalid number");
            return;
        }

        String chosen_currency = popularCurrencies.get(choice-1);
        System.out.println("What would you like to replace it with?: ");
        for (String key : currencies.keySet()) {
            if (popularCurrencies.contains(key)) {
                continue;
            }
            System.out.println(key);
        }
        String replace_currency = input.nextLine();

        if (currencies.containsKey(replace_currency)){
            popularCurrencies.remove(chosen_currency);
            popularCurrencies.add(replace_currency);
        }
        else{
            System.out.println("Invalid input");
        }

        this.write_popular_currencies();

    }

    private void write_popular_currencies(){
        try {
            PrintWriter out = new PrintWriter(currencyPath+"/"+popPath);
            for(String value : popularCurrencies) {
                out.println(value);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("No popular file found.");
        }

    }
}




