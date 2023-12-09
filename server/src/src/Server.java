/*
 * EE422C Final Project submission by
 * Jacob Marquardt
 * jgm3339
 * 17150
 * Slip days used: <1>
 * Spring 2023
 */

package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import com.google.gson.Gson;

class Server extends Observable {
  Login login;
  static Timer timer;
  static int seconds = 0;
  static int guestNum = 0;
  ArrayList<Item> items = new ArrayList<>();
  HashMap<String, ArrayList<String>> biddingHistory = new HashMap<>();
  HashMap<String, ArrayList<String>> History = new HashMap<>();
  ArrayList<String> purchasedAlready = new ArrayList<>();
  public static void main(String[] args) {

    new Server().runServer();
  }

  private void runServer() {
   login = new Login();
   initializeItems();
    try {
      setUpNetworking();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initializeItems() {
    try {
      Scanner reader = new Scanner(new File("items.txt"));
      while (reader.hasNextLine()){
        String [] input = reader.nextLine().split(",");
        items.add(new Item(input[0], input[1], input[2], Integer.valueOf(input[3]), "none", input[4], false));
      }
    } catch (FileNotFoundException | IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    ServerSocket serverSock = new ServerSocket(4343);
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() { // begin sale timer
      @Override
      public void run() {
        int MAXTIME = 5000;
        if (seconds == MAXTIME)
          timer.cancel();
        seconds++;
      }
    }, 1000, 1000);
    while (true) {
      Socket clientSocket = serverSock.accept();
      System.out.println("Connecting to... " + clientSocket);

      ClientHandler handler = new ClientHandler(this, clientSocket);
      this.addObserver(handler);

      Thread t = new Thread(handler);
      t.start();

    }
  }
  protected String checkUserAndPassword(String input) {
    String [] in = input.split(",");
    String user = in[1];
    if (user.equals("guest")){
      //System.out.println("Logging in as guest");
      guestNum++;
      return user + guestNum;
    }
    String password = in[2];
    if (login.tryLogin(user, password)) {
      //System.out.println("Logging in as " + user);
      return user;
    }
    return null;
  }

  protected String getProducts() {
    StringBuilder output = new StringBuilder();
    output.append("setup,");
    output.append(seconds).append(",");
    for (Item i : items) {
      output.append(i.toString());
      output.append(";");
    }
    return output.toString();
  }

  protected void updatePrice(String text) {
    String [] comp = text.split(",");
    int itemNumber = Integer.parseInt(comp[2]);
    Item thisItem = items.get(itemNumber);
    if (thisItem.isSold()) return; // verify not sold
    double currentPrice = Double.parseDouble(thisItem.getPrice());
    double newPrice = Double.parseDouble(comp[1]);
    double binPrice = Double.parseDouble(thisItem.getBINPrice());
    String username = comp[3];
    if (newPrice > currentPrice) { // new highest bidder
      if (newPrice >= binPrice) // item can no longer be bid on
        thisItem.setSold(true);
      doTheBid(newPrice, thisItem, username, itemNumber);
    }
    else if (newPrice == currentPrice && thisItem.getHighestBidder().equals("none")) { // first time an item is being bid on, can bid the opening price
      doTheBid(newPrice, thisItem, username, itemNumber);
    }
    else {
      this.setChanged();
      this.notifyObservers("bid,low," + username);
    }
  }

  private void doTheBid(double newPrice, Item thisItem, String username, int itemNumber) {
    String update = Double.toString(newPrice);
    if (update.split("\\.")[1].length() == 1)
      update += "0";
    thisItem.setPrice(update);
    thisItem.setHighestBidder(username);
    addToBids(thisItem.getTitle(), username, update); // add to list of bids
    this.setChanged();
    this.notifyObservers("bid," + itemNumber + "," + update + "," + username + "," + thisItem.isSold());
  }
  protected void addHistory(String input) {
    String [] comp = input.split(",");
    String username = comp[1];
    String item = comp[2];
    if (purchasedAlready.contains(item)) // item has already been purchased by this or another user
      return;
    purchasedAlready.add(item); // add to list of items purchased before
    if (History.containsKey(username)) { // user has purchased items before
      ArrayList<String> history = History.get(username);
      if (!history.contains(item)) {
        history.add(item);
        History.remove(username);
        History.put(username, history);
      }
    }
    else {
      ArrayList<String> history = new ArrayList<>();
      history.add(item);
      History.put(username, history);
    }
  }
  protected void getHistory(String user){
    if (!History.containsKey(user)) {
      this.setChanged();
      this.notifyObservers("history," + user + ",none");
    }
    else {
      StringBuilder history = new StringBuilder();
      for (String h : History.get(user))
        history.append(h).append(",");
      this.setChanged();
      this.notifyObservers("history," + user + "," + history);
    }
  }

  public void getBids(String input) {
    String [] comp = input.split(",");
    String user = comp[2];
    String item = comp[1];
    if (!biddingHistory.containsKey(item)) {
      this.setChanged();
      this.notifyObservers("showBids," + user + "," + item + ",none");
    } else {
      StringBuilder bids = new StringBuilder();
      for (String h : biddingHistory.get(item))
        bids.append(h).append(";");
      this.setChanged();
      this.notifyObservers("showBids," + user + "," + item + "," + bids);
    }
  }

  private void addToBids(String item, String user, String price) {
    ArrayList<String> history;
    if (biddingHistory.containsKey(item)) // item has been bid on before
      history = biddingHistory.remove(item);
    else
      history = new ArrayList<>();
    history.add(user + " - $" + price);
    biddingHistory.put(item, history);
  }
}