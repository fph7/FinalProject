/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package professionalpractice3;

/**
 * Created by byron on 11/17/14.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


public class GETClient {

static String localServer = "127.0.0.1";
    
    static String updateStocks(String customerID, String stockSymbol, int shares, String price) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+localServer);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Type", "update stocks");
            request.addHeader("Customer", customerID);
            request.addHeader("Symbol", stockSymbol);
            request.addHeader("Shares", Integer.toString(shares));
            request.addHeader("Price", price);

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }

    static String getBalance(String customerID) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+localServer);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Type", "balance");
            request.addHeader("Customer", customerID);

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
    
    static String getRequestSymbol(String input) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+localServer);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Type", "symbol");
            request.addHeader("Symbol", input);

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
    
    static String getTransactions(String customerID) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+localServer);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Type", "transactions");
            request.addHeader("Customer", customerID);

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
    
    static String getPortfolio(String customerID) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+localServer);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Type", "portfolio");
            request.addHeader("Customer", customerID);

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
    
    static String getRequest(String input) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();
        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("http://"+input);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");

            //Invoke the service
            HttpResponse response = client.execute(request);

            //Verify if the response is valid
            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println(statusCode);
            if(statusCode!=200) {
                throw new RuntimeException("Failed with HTTP error code .... : " + statusCode);
            } else {
                //If valid, get the response
                responseBody = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
                String line = responseBody.readLine();
                while(line != null)
                    {
                    System.out.println(line);
                    line = responseBody.readLine();
                    }
                return line;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid input");
            return "";
        } finally {
            if(responseBody!=null)
                responseBody.close();
        }
    }
    
    public static void main(String[] args) throws ClientProtocolException, IOException {
      
        String response = getPortfolio("1");
        System.out.println(response);
    }
}
