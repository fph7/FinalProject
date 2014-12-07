/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simplehttpserver;

/**
 *
 * @author Paul
 * 
 * Java simple HTTP server example code adapted from http://www.rgagnon.com/javadetails/java-have-a-simple-http-server.html
 * 
 * Example code for retrieving SQL connection adapted from https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/*
 * a simple static http server
*/
public class SimpleHttpServer {
    
//SQL request info
static String serverName = "localhost";
static String portNumber = "3306";
static String userName = "root";
static String password = "paul";
static String query;
static Connection connection;

  public static void main(String[] args) throws Exception {
    connection = getConnection();
    HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
    server.createContext("/", new MyHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
  }
  
  
  //Example code from: https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html
 static public Connection getConnection() throws SQLException {

    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);

        conn = DriverManager.getConnection(
                   "jdbc:" + "mysql" + "://" +
                   serverName +
                   ":" + portNumber + "/",
                   connectionProps);

    System.out.println("Connected to database");
    return conn;
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
      else if(type.equals("portfolio"))//get the portfolio for given customer ID
      {
          String customerID = headers.get("Customer").get(0);
          query = "select * from tradeaccountdata.stock where customerID="+customerID;
          Statement stmt;
          try {
              stmt = connection.createStatement();
              try {
                    ResultSet rs = stmt.executeQuery(query);
                    response = "";
                    if(rs.next())
                    {
                    response += rs.getString("stockSymbol")+',';
                    response += rs.getString("shares")+',';
                    response += rs.getString("price");
                    while (rs.next())
                        {
                            response += ',';
                            response += rs.getString("stockSymbol")+',';
                            response += rs.getString("shares")+',';
                            response += rs.getString("price");
                        }
                    }
                    //select * from tradeaccountdata.stock where customerID=1
                  } catch (SQLException ex) {
                      Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
                  }
          } catch (SQLException ex) {
              Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
      else if(type.equals("balance"))
      {
          String customerID = headers.get("Customer").get(0);
          query = "select accountBalance from tradeaccountdata.customerinformation where customerID="+customerID;
          Statement stmt;
          try {
              stmt = connection.createStatement();
              try {
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    response = rs.getString("accountBalance");
                  } catch (SQLException ex) {
                      Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
                      response = "Invalid customer ID!";
                  }
          } catch (SQLException ex) {
              Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
          }   
      }
      else if(type.equals("update stocks"))
      {                             
          String customerID = headers.get("Customer").get(0);//id of customer
          String symbol = headers.get("Symbol").get(0);//stock symbol
          String shares = headers.get("Shares").get(0);//number of shares bought
          String price = headers.get("Price").get(0);//cost paid and new cost of shares
          float profit = -1*Float.parseFloat(price)*Integer.parseInt(shares);
          float balance = profit;//amount balance will change by
          ResultSet rs;
          String currentShares = "";
          String stockID = "";
          String transactionID = "1";
          
          query = "select * from tradeaccountdata.stock where customerID="+customerID+" and stockSymbol=\""+symbol+"\"";
          Statement stmt;
          try {//updates shares and price
              stmt = connection.createStatement();                             
              try {                 
                    rs = stmt.executeQuery(query);
                    rs.next();
                    try
                    {
                    stockID = rs.getString("stockID");
                    }
                    catch (SQLException e){

                    query = "SELECT MAX(stockID) as maxID FROM tradeaccountdata.stock";
                    rs = stmt.executeQuery(query);
                    rs.next();
                    stockID = rs.getString("maxID");                    
                    if(stockID == null)
                        stockID = "1";                       
                    query = "INSERT INTO tradeaccountdata.stock (stockID, stockSymbol, shares, price, customerID) VALUES ('"+stockID+"', '"+symbol+"', '0', '"+price+"', '"+customerID+"')";
                    stmt.execute(query);
                    query = "select * from tradeaccountdata.stock where customerID="+customerID+" and stockSymbol=\""+symbol+"\"";
                    rs = stmt.executeQuery(query);
                    rs.next();
                    }
                    currentShares = rs.getString("shares");
                    int newShares = Integer.parseInt(currentShares)+Integer.parseInt(shares);
                    if(newShares >= 0)//didn't attempt to sell more shares then owned
                        {
                        //still need to update balance
                        query = "select accountBalance, profit from tradeaccountdata.customerInformation where customerID="+customerID;
                        rs = stmt.executeQuery(query);
                        rs.next();
                        String priorProfit = rs.getString("profit");
                        String priorBalance = rs.getString("accountBalance");
                        profit += Float.parseFloat(priorProfit);
                        balance += Float.parseFloat(priorBalance);
                        if(balance >= 0)
                            {                            
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            String date = dateFormat.format(cal.getTime());                           
                            query = "SELECT MAX(transactionID) as maxID FROM tradeaccountdata.transaction";
                            rs = stmt.executeQuery(query);
                            rs.next();
                            transactionID = rs.getString("maxID");
                            if(transactionID == null)
                                transactionID = "1";
                            else
                            {
                            int temp = Integer.parseInt(transactionID);
                            transactionID = Integer.toString(temp+1);
                            }
                            query = "INSERT INTO tradeaccountdata.transaction (transactionID, stock, sharesBought, datetime, customerID) VALUES ('"+transactionID+"', '"+symbol+"', '"+shares+"', '"+date+"', '"+customerID+"')";
                            stmt.execute(query);
                            //INSERT INTO `tradeaccountdata`.`transaction` (`transactionID`, `stock`, `sharesBought`, `datetime`, `customerID`) VALUES ('1', 'FB', '1', '1', '1');
                                
                            query = "UPDATE tradeaccountdata.customerInformation SET accountBalance="+Float.toString(balance)+", profit="+Float.toString(profit)+" WHERE customerID="+customerID;
                            stmt.execute(query);
                            query = "UPDATE tradeaccountdata.stock SET shares="+newShares+", price="+price+" WHERE stockID="+stockID;
                            stmt.execute(query);
                            response = "Purchase successful!,Account Balance:"+balance+",Profit:"+profit;
                            }
                        else
                            response = "Not enough funds available!";
                        }
                    else
                        response = "Not enough stocks available";
                    
                    //update balance
                    
                  } catch (SQLException ex) {
                      Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);}
          } catch (SQLException ex) {
              Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
          }   
      }
      else if(type.equals("transactions"))
      {
          String customerID = headers.get("Customer").get(0);
          query = "select * from tradeaccountdata.transaction where customerID="+customerID;
          Statement stmt;
          try {
              stmt = connection.createStatement();
              try {
                    ResultSet rs = stmt.executeQuery(query);
                    response = "";
                    rs.next();
                    response += rs.getString("stock")+',';
                    response += rs.getString("sharesBought")+',';
                    response += rs.getString("datetime");
                    while (rs.next())
                        {
                            response += ',';
                            response += rs.getString("stock")+',';
                            response += rs.getString("sharesBought")+',';
                            response += rs.getString("datetime");
                        }
                    //select * from tradeaccountdata.stock where customerID=1
                  } catch (SQLException ex) {
                      Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
                  }
          } catch (SQLException ex) {
              Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
      else
      {
          System.out.println("Bad 'Type' header");
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
