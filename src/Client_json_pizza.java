// src/Client_json_pizza.java
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client_json_pizza {

    private Socket requestSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String message;
    ObjectMapper mapper = new ObjectMapper();

    Client_json_pizza() {
    }

    void run() {
        try {
            //1. creating a socket to connect to the server
            requestSocket = new Socket("localhost", 9999);
            System.out.println("Connected to localhost in port");
            //2. get Input and Output streams
            out = new PrintWriter(requestSocket.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
            Command objClient = null;
            //3: Communicating with the server

            try {
                //mando richiesta lista pizze
                objClient = new Command();
                objClient.setCommandName("getListaPizze");
                sendMessage(mapper.writeValueAsString(objClient));
                //aspetto lista di risposta
                message = in.readLine();
                ListaPizze lp = mapper.readValue(message, ListaPizze.class);
                System.out.println("lista ricevuta:");
                for (String i : lp.getListaPizza()) {
                    System.out.println(i);
                }

                // Scegli una pizza
                Scanner scanner = new Scanner(System.in);
                System.out.println("Scegli una pizza:");
                String pizzaScelta = scanner.nextLine();

                // Invia comando getPizza
                objClient.setCommandName("getPizza");
                objClient.setPizzaName(pizzaScelta);
                sendMessage(mapper.writeValueAsString(objClient));

                // Ricevi oggetto pizza
                message = in.readLine();
                Pizza pizza = mapper.readValue(message, Pizza.class);
                System.out.println("Pizza ricevuta: " + pizza.getName());

            } catch (Exception classNot) {
                System.err.println("data received in unknown format");
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
            //4: Closing connection
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg) {
        try {
            PrintWriter pw = new PrintWriter(out);
            pw.println(msg);
            pw.flush();
            System.out.println("client>" + msg);
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client_json_pizza client = new Client_json_pizza();
        client.run();
    }
}