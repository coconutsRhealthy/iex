package com.lennart.model;

import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 21/05/2019.
 */
public class Bux {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new Bux().printDifference();
    }

    private void testMethod() throws Exception {
        Map<String, String> allBuxTickers = getAllBuxTickers();
        Map<String, Double> pricePerTicker = getPricePerTicker(allBuxTickers);
        fillBuxDbLastPrice(pricePerTicker);
    }

    private void printDifference() throws Exception {
        Map<String, String> allBuxTickers = getAllBuxTickers();
        Map<String, Double> pricePerTicker = getPricePerTicker(allBuxTickers);
        Map<String, Double> tickerDifference = getTickerDifference(pricePerTicker);

        tickerDifference = sortByValueHighToLow(tickerDifference);

        for(Map.Entry<String, Double> entry : tickerDifference.entrySet()) {
            String companyName = allBuxTickers.get(entry.getKey());
            System.out.println(companyName + "       " + entry.getValue());
        }
    }

    private Map<String, Double> getTickerDifference(Map<String, Double> currentPricePerTicker) throws Exception {
        Map<String, Double> differenceMap = new HashMap<>();

        initializeDbConnection();

        for(Map.Entry<String, Double> entry : currentPricePerTicker.entrySet()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM bux WHERE ticker = '" + entry.getKey() + "';");
            rs.next();

            double previousValue = rs.getDouble("last_price");

            rs.close();
            st.close();

            double percentDiff = (entry.getValue() / previousValue) - 1;

            differenceMap.put(entry.getKey(), percentDiff);
        }

        closeDbConnection();

        return differenceMap;
    }

    private void fillBuxDbLastPrice(Map<String, Double> pricePerTicker) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM bux;");

        for(Map.Entry<String, Double> entry : pricePerTicker.entrySet()) {
            st.executeUpdate("INSERT INTO bux (ticker, last_price) VALUES ('" + entry.getKey() + "', '" + entry.getValue() + "')");
        }

        st.close();

        closeDbConnection();
    }

    private Map<String, Double> getPricePerTicker(Map<String, String> allBuxTickers) {
        Map<String, Double> pricesPerTicker = new HashMap<>();

        for(Map.Entry<String, String> entry : allBuxTickers.entrySet()) {
            String queryUrl = "https://cloud.iexapis.com/stable/stock/" + entry.getKey() + "/price?token=sk_7f820b75180d45f9a9674165bc9b2e3a";

            RestTemplate restTemplate = new RestTemplate();

            try {
                String result = restTemplate.getForObject(queryUrl, String.class);
                pricesPerTicker.put(entry.getKey(), Double.valueOf(result));
            } catch (Exception e) {
                System.out.println("Error! " + entry.getKey());
                e.printStackTrace();
            }
        }

        return pricesPerTicker;
    }

    private Map<String, String> getAllBuxTickers() {
        Map<String, String> allBuxTickers = new HashMap<>();

        allBuxTickers.put("ANF", "Abercrombie");
        allBuxTickers.put("ATVI", "Activision");
        allBuxTickers.put("ADBE", "Adobe Sytems");
        allBuxTickers.put("AA", "Alcoa");
        allBuxTickers.put("BABA", "Alibaba");
        allBuxTickers.put("AMZN", "Amazon");
        allBuxTickers.put("AMD", "AMD");
        allBuxTickers.put("AMGN", "Amgen");
        allBuxTickers.put("AAPL", "Apple");
        allBuxTickers.put("T", "AT&T");
        allBuxTickers.put("BIDU", "Baidu");
        allBuxTickers.put("BAC", "Bank Of America");
        allBuxTickers.put("BRK.B", "Berkshire Hathaway");
        allBuxTickers.put("BYND", "Beyond Meat");
        allBuxTickers.put("BB", "Blackberry");
        allBuxTickers.put("BLK", "Blackrock");
        allBuxTickers.put("BA", "Boeing");
        allBuxTickers.put("BKNG", "Booking");
        allBuxTickers.put("CPRI", "Capri");
        allBuxTickers.put("CAT", "Caterpillar");
        allBuxTickers.put("CELG", "Celgene");
        allBuxTickers.put("CVX", "Chevron");
        allBuxTickers.put("CSCO", "Cisco Systems");
        allBuxTickers.put("C", "Citigroup");
        allBuxTickers.put("KO", "Coca-Cola");
        allBuxTickers.put("DAL", "Delta Air Lines");
        allBuxTickers.put( "DBX", "Dropbox");
        allBuxTickers.put("ETFC", "E*Trade");
        allBuxTickers.put("EBAY", "Ebay");
        allBuxTickers.put("EXPE", "Expedia");
        allBuxTickers.put("XOM", "Exxon Mobil");
        allBuxTickers.put("FB", "Facebook");
        allBuxTickers.put("RACE", "Ferrari");
        allBuxTickers.put("FCAU", "Fiat Chrysler");
        allBuxTickers.put("FIT", "Fitbit");
        allBuxTickers.put("F", "Ford");
        allBuxTickers.put("FOSL", "Fossil");
        allBuxTickers.put("GRMN", "Garmin");
        allBuxTickers.put("GE", "General Electric");
        allBuxTickers.put("GM", "General Motors");
        allBuxTickers.put("GS", "Goldman Sachs");
        allBuxTickers.put("GOOGL", "Google");
        allBuxTickers.put("GPRO", "GoPro");
        allBuxTickers.put("HAL", "Halliburton");
        allBuxTickers.put("HOG", "Harley-Davidson");
        allBuxTickers.put("HAS", "Hasbro");
        allBuxTickers.put("HPQ", "HP");
        allBuxTickers.put("IBM", "IBM");
        allBuxTickers.put("ILMN", "Illumina");
        allBuxTickers.put("INTC", "Intel");
        allBuxTickers.put("JPM", "JPMorgan Chase");
        allBuxTickers.put("KHC", "Kraft-Heinz");
        allBuxTickers.put("LEVI", "Levi's");
        allBuxTickers.put("LBTYA", "Liberty Global");
        allBuxTickers.put("LYFT", "Lyft");
        allBuxTickers.put("MA", "Mastercard");
        allBuxTickers.put("MCD", "McDonald's");
        allBuxTickers.put("MRK", "Merck");
        allBuxTickers.put("MSFT", "Microsoft");
        allBuxTickers.put("MNST", "Monster");
        allBuxTickers.put("MS", "Morgan Stanley");
        allBuxTickers.put("NFLX", "Netflix");
        allBuxTickers.put("NKE", "Nike");
        allBuxTickers.put("NIO", "NIO");
        allBuxTickers.put("NVDA", "Nvidia");
        allBuxTickers.put("ORCL", "Oracle");
        allBuxTickers.put("PYPL", "Paypal");
        allBuxTickers.put("PEP", "Pepsico");
        allBuxTickers.put("PFE", "Pfizer");
        allBuxTickers.put("PM", "Philip Morris");
        allBuxTickers.put("PINS", "Pinterest");
        allBuxTickers.put("PG", "Procter Gamble");
        allBuxTickers.put("QCOM", "Qualcomm");
        allBuxTickers.put("RL", "Ralph Lauren");
        allBuxTickers.put("ROKU", "Roku");
        allBuxTickers.put("CRM", "Salesforce");
        allBuxTickers.put("SLB", "Schlumberger");
        allBuxTickers.put("SCHW", "Schwab");
        allBuxTickers.put("SHOP", "Shopify");
        allBuxTickers.put("SNAP", "Snap");
        allBuxTickers.put("SONO", "Sonos");
        allBuxTickers.put("SPOT", "Spotify");
        allBuxTickers.put("SQ", "Square");
        allBuxTickers.put("SBUX", "Starbucks");
        allBuxTickers.put("TSLA", "Testla");
        allBuxTickers.put("TRIP", "Tripadvisor");
        allBuxTickers.put("TWTR", "Twitter");
        allBuxTickers.put("UBER", "Uber");
        allBuxTickers.put("UA", "Under Armour");
        allBuxTickers.put("VZ", "Verizon");
        allBuxTickers.put( "V", "Visa");
        allBuxTickers.put("WMT", "Wal-Mart");
        allBuxTickers.put( "DIS", "Walt Disney");
        allBuxTickers.put("XRX", "Xerox");
        allBuxTickers.put("YUM", "Yum");
        allBuxTickers.put("ZNGA", "Zynga");

        return allBuxTickers;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/iex?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
