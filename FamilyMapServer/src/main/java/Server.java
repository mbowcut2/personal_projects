import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import handler.*;

public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;

    private HttpServer server;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new Server().run(port);
    }

    private void run(int port) {

        System.out.println("Initializing HTTP Server");

        InetSocketAddress serverAddress = new InetSocketAddress(port);

        try {
            server = HttpServer.create(serverAddress, MAX_WAITING_CONNECTIONS);
        }

        catch (IOException e) {
            e.printStackTrace();
            return;
        }


        registerHandlers();

        System.out.println("Starting server");

        server.start();

        System.out.println("Server started. Listening on port " + port);

    }

    private void registerHandlers() {

        System.out.println("Creating contexts");

        /*
        create the contexts with this function for each request type, like this:
        server.createContext("/games/list", new ListGamesHandler());
         */

        server.createContext("/clear", new ClearHandler());
        //server.createContext("/event/[eventID]", new EventByIdHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/user/login", new LoginHandler());
        //server.createContext("/person/[personID]", new PersonByIdHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/", new FileHandler());




    }
}
