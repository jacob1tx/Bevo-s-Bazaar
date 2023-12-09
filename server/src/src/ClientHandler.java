/*
 * EE422C Final Project submission by
 * Jacob Marquardt
 * jgm3339
 * 17150
 * Slip days used: <1>
 * Spring 2023
 */

package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Observer;
import java.util.Observable;

class ClientHandler implements Runnable, Observer {

  private Server server;
  private Socket clientSocket;
  private BufferedReader fromClient;
  private PrintWriter toClient;

  protected ClientHandler(Server server, Socket clientSocket) {
    this.server = server;
    this.clientSocket = clientSocket;
    try {
      fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
      toClient = new PrintWriter(this.clientSocket.getOutputStream());
    } catch (IOException e) {
      //e.printStackTrace();
    }
  }

  protected void sendToClient(String string) {
    //System.out.println("Sending to client: " + string);
    toClient.println(string);
    toClient.flush();
  }

  @Override
  public void run() {
    String input;
    try {
      while ((input = fromClient.readLine()) != null) {
        //System.out.println("From client: " + input);
        String request = input.split(",")[0];
        switch (request) {
          case "login":
            this.sendToClient("login," + server.checkUserAndPassword(input));
            break;
          case "setup":
            this.sendToClient(server.getProducts());
            break;
          case "bid":
            server.updatePrice(input);
            break;
          case "history":
            server.getHistory(input.split(",")[1]);
            break;
          case "addHistory":
            server.addHistory(input);
            break;
          case "getBids":
            server.getBids(input);
            break;
        }
      }
    } catch (IOException e) {
      //e.printStackTrace();
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    this.sendToClient((String) arg);
  }
}