import java.io.*;
import java.net.*;
import java.util.HashMap;

public class TcpServer {
    public static void main(String[] args) {
        int port = 5000;
        HashMap<String, String> store = new HashMap<>();

        System.out.println("TCP Server dinleniyor: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Gelen: " + line);

                    Command cmdObj = CommandParser.parse(line);

                    if (cmdObj instanceof SetCommand sc) {
                        store.put(sc.getId(), sc.getMessage());
                        out.println("OK");

                    } else if (cmdObj instanceof GetCommand gc) {
                        String result = store.get(gc.getId());
                        out.println(result == null ? "NOT_FOUND" : result);

                    } else {
                        out.println("UNKNOWN_COMMAND");
                    }
                }

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
