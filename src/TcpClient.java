import java.io.*;
import java.net.*;

public class TcpClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // SET
            out.println("SET 1 hello");
            System.out.println("Server: " + in.readLine());

            // GET
            out.println("GET 1");
            System.out.println("Server: " + in.readLine());

            // Sonlandırmak için
            out.println("BYE");
            System.out.println("Server: " + in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
