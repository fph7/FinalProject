/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simplehttpserver;

/**
 *
 * @author Paul
 */
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/*
 * a simple static http server
*/
public class SimpleHttpServer {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
    server.createContext("/", new MyHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  static class MyHandler implements HttpHandler {
    public void handle(HttpExchange request) throws IOException {
      Headers headers = request.getRequestHeaders();
      String type = headers.get("Type").get(0);
      
      String response = "Simple HttpServer Loaded!";
      if(type.equals("symbol"))//we are returning symbol data
      {
      response = getRequestSymbol(headers.get("Symbol").get(0));
      }
      
      request.sendResponseHeaders(200, response.length());
      OutputStream os = request.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }
  
  
static String getRequestSymbol(String input) throws ClientProtocolException, IOException {
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            //Define a HttpGet request
            HttpGet request = new HttpGet("https://sandbox.tradier.com/v1/markets/quotes?symbols="+input);

            //Set Http Headers
            request.addHeader("Accept" , "application/xml");
            request.addHeader("Authorization", "Bearer Zzuj6u1JFV2BTRso0LcpLkLhZw4i");

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
                //String line = "";
                String line = responseBody.readLine();
                //while ((line = responseBody.readLine()) != null) {
                  //  System.out.println(line);
                return line;
                //}
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
  
}
