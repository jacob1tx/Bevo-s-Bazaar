package src;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


class Client {

  private BufferedReader fromServer;
  private PrintWriter toServer;
  private final Scanner consoleInput = new Scanner(System.in);
  protected Boolean loggedIn = false;
  protected String username;
  protected Boolean updating;
  private static Client self;
  protected ArrayList<Item> items = new ArrayList<>();
  static int seconds;
  static Timer timer = null;

  protected AppController controller;
  protected static String purchaseHistory;
  protected static String allBids;
  public static void main(String[] args){
    self = new Client();
    try {
      self.setUpNetworking();
      SceneGenerator.generate(null); // generate display
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected static Client sendSelf() {
    return self;
  }
  private void setUpNetworking() throws Exception {
    String host = "10.165.71.170";
    @SuppressWarnings("resource")
    Socket socket = new Socket(host, 4343);
    System.out.println("Connecting to... " + socket);
    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toServer = new PrintWriter(socket.getOutputStream());

    Thread readerThread = new Thread(() -> {
      String input;
      try {
        while ((input = fromServer.readLine()) != null) {
          //System.out.println("From server: " + input);
          String request = input.split(",")[0];
          switch (request){
            case "login":
              checkCredentials(input);
              break;
            case "setup":
              setProducts(input);
              break;
            case "bid":
              updateBid(input);
              break;
            case "history":
              displayHistory(input);
              break;
            case "showBids":
              showBids(input);
              break;
            default:
              processRequest(input);
          }
          updating = false;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    readerThread.start();
  }

  private void displayHistory(String input) {
    if (input.split(",")[1].equals(controller.username)) {
      purchaseHistory = input.substring("history,".length() + controller.username.length() + 1);
      controller.showHistory();
    }
  }

  private void showBids(String input) {
    if (input.split(",")[1].equals(controller.username)) {
      allBids = input.substring("showBids,".length() + controller.username.length() + 1);
      controller.showBids();
    }
  }


  private void processRequest(String input) {
    return;
  }

  protected void sendToServer(String string) {
    //System.out.println("Sending to server: " + string);
    updating = true;
    if (string.split(",")[0].equals("bid"))
      string += "," + username;
    toServer.println(string);
    toServer.flush();
  }

  private void checkCredentials(String input) {
    String user = input.split(",")[1];
    loggedIn = !user.equals("null");
    if (loggedIn) {
      username = user;
      SceneGenerator.controller.close();
    }
    else
      SceneGenerator.controller.printError();
  }

  protected void setProducts(String input) {
    String [] itemString = input.split(";");
    String [] firstString = itemString[0].split(",");
    if (firstString.length > 1) {
      items.add(new Item(firstString[2], firstString[3], firstString[4], Integer.valueOf(firstString[5]), firstString[6], firstString[7], Boolean.parseBoolean(firstString[8])));
      // start timer
      if (timer != null) { // allows a new user timer to begin
        timer.cancel();
      }
      seconds = Integer.parseInt(firstString[1]);
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          int MAXTIME = 5000;
          if (seconds == MAXTIME)
            timer.cancel();
          seconds++;
          controller.updateTime();
        }
      }, 1000, 1000);
    }

    for (int i = 1; i < itemString.length; i++) {
      String [] itm = itemString[i].split(",");
      items.add(new Item(itm[0], itm[1], itm[2], Integer.valueOf(itm[3]), itm[4], itm[5], Boolean.parseBoolean(itm[6])));
    }

  }


  private void logIn() {
    while (!loggedIn) {
      System.out.print("Enter username or 'guest': ");
      String user = consoleInput.nextLine().trim();
      System.out.println();
      if (user.equals("guest"))
        sendToServer("login," + user);
      else {
        System.out.print("Enter Password: ");
        String password = consoleInput.nextLine();
        sendToServer("login," + user + "," + password);
      }
      while (updating){}
      if (!loggedIn)
        SceneGenerator.controller.printError();
    }
  }

  private void updateBid (String input) {
    String [] comp = input.split(",");
    if (comp[1].equals("low")) {
      if (comp[2].equals(controller.username))
        controller.setError("Bid higher!");
    }
    else
      controller.updatePrice(comp[2], Integer.parseInt(comp[1]), comp[3], Boolean.parseBoolean(comp[4]));
  }
}