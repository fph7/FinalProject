package professionalpractice3;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jsoup.Jsoup;

/**
 *@author Paul
 * 
 * This program fetches Trade Net data for a list of allowed companies.
 * A GUI allows a user to enter one of the valid symbols and then 
 * the relevant data items are fetched and displayed. The GUI uses a simple
 * form with a single button and text field.
 * 
 * Example tutorial from http://math.hws.edu/javanotes/c6/s1.html followed
 * for implementing basic GUI.
 * 
 * Example tutorial for card layout followed from https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html
 */
public class ProfessionalPractice3 {
   
static JTextField stockInput = new JTextField(1);
static JTextField portfolioInput = new JTextField(1);
static JTextField transactionInput = new JTextField(1);
static JTextField stockCount = new JTextField(1);
static JTextField stockSymbol = new JTextField(1);
static JTextField clientIDBuy = new JTextField(1);
static JLabel stockCountL = new JLabel("Stock Count: (negative values to sell, positive to buy)");
static JLabel stockSymbolL = new JLabel("Stock Symbol:");
static JLabel clientIDL = new JLabel("Client ID:");
static JLabel accountBalanceL = new JLabel("Account Balance:");
static JLabel profitL = new JLabel("Profit:");
static JLabel buyDisplayL = new JLabel();

static StockDisplayPanel stockDisplay = new StockDisplayPanel();
static PortfolioDisplayPanel portfolioDisplay = new PortfolioDisplayPanel();
static TransactionDisplayPanel transactionDisplay = new TransactionDisplayPanel();
static boolean transactionButton = false;
static boolean stockButton = false;
static boolean portfolioButton = false;
static boolean buyButton = false;

//list of approved symbols
static ArrayList<String> validSymbols = new ArrayList<String>(Arrays.asList("FB","AAPL",
        "GOOG", "MSFT", "CRM", "TWTR", "BABA", "SPY", "QQQ", "DIA"));

        //creates the display panel for the gui
     private static class StockDisplayPanel extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         if(stockButton == true)
            {
            String values[];
             try {                 
                 String fieldText = stockInput.getText();
                 if(validSymbols.contains(fieldText.toUpperCase()))
                    {
                     values = getValues(fieldText);
                     for(int i = 0; i < 10; i++)
                       g.drawString(values[i], 10, 30+i*12);   
                    }
                 else
                 {
                 g.drawString("Invalid symbol!", 10, 30);
                 g.drawString("Valid symbols are FB, AAPL, GOOG,", 10, 42);
                 g.drawString("MSFT, CRM, TWTR, BABA, SPY,", 10, 54);
                 g.drawString("QQQ, and DIA.", 10, 66);
                 }
                     
             } catch (IOException ex) {
                 Logger.getLogger(ProfessionalPractice3.class.getName()).log(Level.SEVERE, null, ex);
             }
            }
         else
            {
            g.drawString( "Enter a symbol then press \"Search\"", 10, 30 );
            g.drawString("Valid symbols are FB, AAPL, GOOG,", 10, 42);
            g.drawString("MSFT, CRM, TWTR, BABA, SPY,", 10, 54);
            g.drawString("QQQ, and DIA.", 10, 66);
            }
      }
      
      public void paintValues(Graphics g, String[] values) {
          super.paintComponent(g);
      }
   }
   
   //this code executes on button press - a press is logged then a repaint is done
   private static class StockButton implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         stockButton = true;
         stockDisplay.repaint();
      }
   } 

   private static class PortfolioDisplayPanel extends JPanel {
      public void paintComponent(Graphics g) {
                  super.paintComponent(g);
         if(portfolioButton == true)
            {
            String fieldText = portfolioInput.getText();
                      try {
                          fieldText = GETClient.getPortfolio(fieldText);
                      } catch (IOException ex) {
                          Logger.getLogger(ProfessionalPractice3.class.getName()).log(Level.SEVERE, null, ex);
                      }
            g.drawString("Symbol     Count       Price        Value", 10, 30);
            String tokens[] = fieldText.split(",");
            for(int i = 0; i < tokens.length; i+=3)
                {  
                g.drawString(tokens[i], 10, 42+(i/3)*12);
                g.drawString(tokens[i+1], 64, 42+(i/3)*12);
                g.drawString("$"+tokens[i+2], 118, 42+(i/3)*12);
                g.drawString("$"+Float.toString(Float.parseFloat(tokens[i+1])*Float.parseFloat(tokens[i+2])), 172, 42+(i/3)*12);
                }
            
            }
         else
            {
            g.drawString( "Enter customer ID then press \"View Portfolio\"", 10, 30 );
            }
      }
      
      public void paintValues(Graphics g, String[] values) {
          super.paintComponent(g);
      }
   }
   
   private static class PortfolioButton implements ActionListener {
      public void actionPerformed(ActionEvent e) {
          portfolioButton = true;
          portfolioDisplay.repaint();
      }
   }
   
   private static class TransactionDisplayPanel extends JPanel {
      public void paintComponent(Graphics g) {
                  super.paintComponent(g);
         if(transactionButton == true)
            {
            String fieldText = transactionInput.getText();
                      try {
                          fieldText = GETClient.getTransactions(fieldText);
                      } catch (IOException ex) {
                          Logger.getLogger(ProfessionalPractice3.class.getName()).log(Level.SEVERE, null, ex);
                      }
            g.drawString("Symbol     Count       Date", 10, 30);
            String tokens[] = fieldText.split(",");
            for(int i = 0; i < tokens.length; i+=3)
                {  
                g.drawString(tokens[i], 10, 42+(i/3)*12);
                g.drawString(tokens[i+1], 64, 42+(i/3)*12);
                g.drawString(tokens[i+2], 118, 42+(i/3)*12);
                }
            
            }
         else
            {
            g.drawString( "Enter customer ID then press \"View Transactions\"", 10, 30 );
            }
      }
      
      public void paintValues(Graphics g, String[] values) {
          super.paintComponent(g);
      }
   }
   
   private static class TransactionButton implements ActionListener {
      public void actionPerformed(ActionEvent e) {
          transactionButton = true;
          transactionDisplay.repaint();
      }
   }
     
    private static class BuyButton implements ActionListener {
      public void actionPerformed(ActionEvent e) {
          String symbol = stockSymbol.getText().toUpperCase();
          String customerID = clientIDBuy.getText();
          String closing = "?";
          String balance = "?";
          int count = 0; 
          if(!validSymbols.contains(symbol))
          {
          buyDisplayL.setText("Invalid stock symbol!");
          }
          else
          {
          try{
            count = Integer.parseInt(stockCount.getText());}
          catch (NumberFormatException err){}
          if(count == 0)
            buyDisplayL.setText("Invalid stock count!");
          else//Valid symbol and stock.
            {
              try {
                  closing = getValue(GETClient.getRequestSymbol(symbol), "last");
                  balance = GETClient.getBalance(customerID);
              } catch (IOException ex) {}
            float balanceF = Float.parseFloat(balance);
            if(balanceF > 0)
                {
                float cost = count*Float.parseFloat(closing);
                if(balanceF >= cost)
                    {
                    buyDisplayL.setText("Purchase complete!");
                    balanceF -= cost;
                    try {                       
                        //alter stocks and balance and add transaction
                        String response = GETClient.updateStocks(customerID, symbol, count, closing);
                        String tokens[] = response.split(",");
                        buyDisplayL.setText(tokens[0]);
                        accountBalanceL.setText(tokens[1]);
                        profitL.setText(tokens[2]);
                    } catch (IOException ex) {Logger.getLogger(ProfessionalPractice3.class.getName()).log(Level.SEVERE, null, ex);}
                    }
                else
                    buyDisplayL.setText("Invalid Funds!");
                }
            }
          }
      }
   }
    
   //this sets up the gui and the button press calls other relevant code
    public static void main(String[] args) {   
      JFrame window = new JFrame("Trade Net Market Data");
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      MyCardLayout cardLayout = new MyCardLayout();
      cardLayout.addComponentToPane(window.getContentPane());
      window.setSize(350, 350);
      window.setVisible(true);       
    }
    
    
    //fetches the data using getclient and parses out relevant pieces into a string array
    public static String[] getValues(String symbolInput) throws IOException {
        //Symbol, Description, Exchange, Closing Price, Daily Net Change & Percentage, Volume, Average Volume, 52-Week High & Low
        String response = GETClient.getRequestSymbol(symbolInput);
        String[] values;
        String symbol = getValue(response, "symbol");
        String description = getValue(response, "description");
        String exchange = getValue(response, "exch");
        String closing = getValue(response, "last");
        String netChange = getValue(response, "change");
        String percChange = getValue(response, "change_percentage");
        String volume = getValue(response, "volume");
        String avgVolume = getValue(response, "average_volume");
        String high52 = getValue(response, "week_52_high");
        String low52 = getValue(response, "week_52_low");
        values = new String[]{"Symbol:"+symbol, "Description:"+description,
            "Exchange:"+exchange, "Closing:"+closing, "Net Change:"+netChange, 
            "% Change:"+percChange, "Volume:"+volume, "Average Volume:"+avgVolume, 
            "52 Week High:"+high52, "52 Week Low:"+low52};
        return values;
    }
    
    //assists getValues in getting a single target value from an input string
    public static String getValue(String input, String target) {
        return Jsoup.parse(input).select(target).first().text();
    }

    
//This class created from example code at https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html
static class MyCardLayout implements ItemListener {
    JPanel cards; //a panel that uses CardLayout
    final static String BUY = "Buy and Sell Stocks";
    final static String STOCKINFOPANEL = "List Stock Information";
    final static String PORTFOLIO = "View Portfolio";
    final static String TRANSACTION = "View Transactions";
    
    public void addComponentToPane(Container pane) {
        //Put the JComboBox in a JPanel to get a nicer look.
        JPanel comboBoxPane = new JPanel(); //use FlowLayout
        String comboBoxItems[] = {STOCKINFOPANEL, PORTFOLIO, BUY, TRANSACTION};
        JComboBox cb = new JComboBox(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);
        comboBoxPane.add(cb);
        
        //List Stock Information
        JButton okButton = new JButton("List Stock Info");
        okButton.addActionListener(new StockButton());
        JPanel stockContent = new JPanel();
        stockContent.setLayout(new BorderLayout());
        stockContent.add(stockDisplay, BorderLayout.CENTER);
        stockContent.add(okButton, BorderLayout.SOUTH);
        stockContent.add(stockInput, BorderLayout.NORTH);
        
        //List Portfolio Contents
        okButton = new JButton("View Portfolio");
        okButton.addActionListener(new PortfolioButton());
        JPanel portfolioContent = new JPanel();
        portfolioContent.setLayout(new BorderLayout());
        portfolioContent.add(portfolioDisplay, BorderLayout.CENTER);
        portfolioContent.add(okButton, BorderLayout.SOUTH);
        portfolioContent.add(portfolioInput, BorderLayout.NORTH);
        
        //List Portfolio Contents
        okButton = new JButton("Make Sale");
        okButton.addActionListener(new BuyButton());
        JPanel buyContent = new JPanel();
        buyContent.setLayout(new GridLayout(15, 0));
        buyContent.add(stockSymbolL);
        buyContent.add(stockSymbol);
        buyContent.add(stockCountL);
        buyContent.add(stockCount);
        buyContent.add(clientIDL);
        buyContent.add(clientIDBuy);
        buyContent.add(okButton);
        buyContent.add(buyDisplayL);
        buyContent.add(accountBalanceL);
        buyContent.add(profitL);
        
        //List Portfolio Contents
        okButton = new JButton("Display Transactions");
        okButton.addActionListener(new TransactionButton());
        JPanel transactionContent = new JPanel();
        transactionContent.setLayout(new BorderLayout());
        transactionContent.add(transactionDisplay, BorderLayout.CENTER);
        transactionContent.add(okButton, BorderLayout.SOUTH);
        transactionContent.add(transactionInput, BorderLayout.NORTH);
        
        
        //Create the panel that contains the "cards".
        cards = new JPanel(new CardLayout());
        cards.add(stockContent, STOCKINFOPANEL);
        cards.add(portfolioContent, PORTFOLIO);
        cards.add(buyContent, BUY);
        cards.add(transactionContent, TRANSACTION);
        
        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }
    
        public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, (String)evt.getItem());
    }
        
}

}

