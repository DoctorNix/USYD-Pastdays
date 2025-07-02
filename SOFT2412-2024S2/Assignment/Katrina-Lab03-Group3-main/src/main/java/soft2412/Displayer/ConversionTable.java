package soft2412.Displayer;

import soft2412.Currency.*;
import java.text.DecimalFormat;

public class ConversionTable {

        private String[][] conversion_table;
        private int table_dim;
        private int cell_width = 12;
        private CurrencyManager currency_mang;

        public ConversionTable(CurrencyManager currency_mang) {

            this.currency_mang = currency_mang;

            this.table_dim = this.currency_mang.popularCurrencies.size() + 1;

            this.conversion_table = new String[table_dim][table_dim];
        }


        public void showDisplay(){
            this.fillDisplay();
            String horizontal = "";
            for (int i = 0; i < cell_width * this.table_dim+ (this.table_dim*2); i++){
                horizontal += "-";
            }

            this.update_values();

            System.out.println("\nConversion Table");
            System.out.println(horizontal);
            for (int i = 0; i < this.table_dim; i++) {
                for (int j = 0; j < this.table_dim; j++) {
                    String value = this.conversion_table[i][j];

                    if (value == null){
                        value = " ";
                    }

                    String padding = "";
                    if (value.length() != cell_width){
                        for (int k = 0; k < (cell_width - value.length()); k++) {
                            padding +=" ";
                        }
                        value += padding;
                    }
                    System.out.print("|"+value+"|");
                }
                System.out.println();
                System.out.println(horizontal);
            }
            System.out.println("All values capped at format ####.####");
        }

        private void fillDisplay(){
            // y,x

            this.conversion_table[0][0] = "From/To";

            int i = 1;
            for (int j = 0; j < currency_mang.popularCurrencies.size(); j++ ) {
                String x = currency_mang.popularCurrencies.get(j);

                this.conversion_table[i][0] = x;
                this.conversion_table[0][i] = x;
                i +=1;
            }
            for (int j = 1; j < this.table_dim; j++){
                this.conversion_table[j][j] = fillDiagonal();;
            }

        }

        public void update_values(){
            for (int i = 1; i < this.table_dim; i++){
                String current_overview = this.conversion_table[i][0];
                Currency current = this.currency_mang.currencies.get(current_overview);
                for (int j = 1; j < this.table_dim; j++) {
                    String against_overview = this.conversion_table[0][j];
                    double rate = 0.0;
                    if (against_overview != null && current != null){
                        if (!against_overview.equals(current_overview)){
                            rate = current.getLatestRate(against_overview);
                        }
                    }

                    if (rate <= 0){
                        this.conversion_table[i][j] = this.fillDiagonal();
                    }
                    else{
                        this.conversion_table[i][j] = this.fillCell(current,against_overview);
                    }
                }
            }
        }


        private String fillCell(Currency fromCurrency, String toCurrency){
            double current_value = fromCurrency.getLatestRate(toCurrency);
            double previous_value = fromCurrency.getPreviousRate(toCurrency);

            if (previous_value == -1.0){
                previous_value = current_value;
            }

            DecimalFormat df = new DecimalFormat("####.####");

            String value = " " + (df.format(current_value));
            String padding = "";
            if (value.length() != cell_width){
                for (int k = 0; k < (cell_width - value.length()-4); k++) {
                    padding +=" ";
                }
                value += padding;
            }

            double difference = current_value-previous_value;

            if (difference > 0){
                value += "(I) ";
            }
            else if (difference < 0){
                value += "(D) ";
            }
            else{
                value += "(-) ";
            }
            return value;
        }

        private String fillDiagonal(){
            String padding = "";
            for (int k = 0; k < (cell_width); k++) {
                if (k == (cell_width/2) || k == (cell_width/2)-1){
                    padding += "-";
                }
                else{
                    padding += " ";
                }

            }
            return padding;
        }

        public String get_cell_value(int x, int y){
            return this.conversion_table[y][x];
        }

        public void refresh_titles(){
            this.fillDisplay();
        }
}



