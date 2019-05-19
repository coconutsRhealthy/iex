package com.lennart.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

/**
 * Created by LennartMac on 19/05/2019.
 */
public class Model {

    public static void main(String[] args) {
        new Model().testMethod();
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
}
