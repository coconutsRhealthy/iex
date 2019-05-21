package com.lennart.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 19/05/2019.
 */
public class Model {

    private Connection con;

    public static void main(String[] args) throws Exception {
        Model model = new Model();

        Map<String, Double> tickerLastPriceMap = model.getTickerLastPriceMap();
        model.storePricesInDb(tickerLastPriceMap);
    }

    private void testMethod() {
        final String uri = "https://sandbox.iexapis.com/beta/stock/AAPL/financials/2?token=" +
                        "Tsk_542a86f7e5fc48ad822e223e27d3e1ab&period=annual";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        JSONObject jsonpObject = new JSONObject(result);

        printJsonObject(jsonpObject);
    }

    private void printJsonObject(JSONObject jsonObj) {
        for (Object keyStr : jsonObj.keySet()) {
            Object keyvalue = jsonObj.get((String) keyStr);

            if(keyvalue instanceof JSONArray) {
                System.out.println(keyStr);

                JSONArray jsonArray = (JSONArray) keyvalue;
                for(int i = 0; i < jsonArray.length(); i++) {
                    printJsonObject((JSONObject) jsonArray.get(i));
                    System.out.println();
                }
            } else if (keyvalue instanceof JSONObject) {
                System.out.println(keyStr);

                printJsonObject((JSONObject)keyvalue);
            } else {
                System.out.println(keyStr + "   " + keyvalue);
            }
        }

        System.out.println();
    }

    private void storePricesInDb(Map<String, Double> tickerLastPriceMap) throws Exception {
        initializeDbConnection();

        for(Map.Entry<String, Double> entry : tickerLastPriceMap.entrySet()) {
            Statement st = con.createStatement();
            st.executeUpdate("INSERT INTO stockprices (ticker, last_price) VALUES ('" + entry.getKey() + "', '" + entry.getValue() + "')");
            st.close();
        }

        closeDbConnection();
    }

    private Map<String, Double> getTickerLastPriceMap() throws Exception {
        Map<String, Double> tickerLastPriceMap = new HashMap<>();
        List<String> allTickers = getAllTickers();


        for(String ticker : allTickers) {
            String queryUrl = "https://cloud.iexapis.com/stable/stock/" + ticker + "/price?token=sk_7f820b75180d45f9a9674165bc9b2e3a";

            RestTemplate restTemplate = new RestTemplate();

            try {
                String result = restTemplate.getForObject(queryUrl, String.class);
                tickerLastPriceMap.put(ticker, Double.valueOf(result));
            } catch (Exception e) {
                System.out.println("Error! " + ticker);
                e.printStackTrace();
            }
        }

        return tickerLastPriceMap;
    }

    private List<String> getAllTickers() throws Exception {
        List<String> allLines = readTickerFile();
        List<String> allTickers = new ArrayList<>();

        for(String line : allLines) {
            String ticker = line.substring(0, line.indexOf('\t'));
            allTickers.add(ticker);
        }

        for(String ticker : allTickers) {
            System.out.println(ticker);
        }

        return allTickers;
    }

    public List<String> readTickerFile() throws Exception  {
        File textFile = getLatestFilefromDir("/Users/lennartpopma/Documents/iexproject");

        List<String> textLines;
        try (Reader fileReader = new FileReader(textFile)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            textLines = new ArrayList<>();

            while (line != null) {
                textLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return textLines;
    }

    private File getLatestFilefromDir(String dirPath){
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/iex?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
