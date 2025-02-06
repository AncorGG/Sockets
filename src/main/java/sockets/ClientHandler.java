/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sockets;

import DAO.BookDao;
import com.google.gson.Gson;
import java.io.*;
import static java.lang.System.out;
import java.net.Socket;
import java.util.StringTokenizer;
import model.Book;

/**
 *
 * @author Ancor
 */
public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); OutputStream out = socket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            StringTokenizer tokens = new StringTokenizer(requestLine);
            String method = tokens.nextToken();
            String route = tokens.nextToken();

            System.out.println("Solicitud: " + method + " " + route);

            String response = handleRequest(method, route, in);

            out.write(response.getBytes());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String method, String route, BufferedReader in) throws IOException {
        if (method.equals("GET") && route.equals("/books")) {
            return HttpResponse.ok(new Gson().toJson(BookDao.getAllBooks()));
        }

        if (method.equals("GET") && route.startsWith("/books/")) {
            String[] parts = route.split("/");
            if (parts.length == 3) {
                try {
                    int bookId = Integer.parseInt(parts[2]);
                    Book book = BookDao.getBookById(bookId);

                    if (book != null) {
                        return HttpResponse.ok(new Gson().toJson(book));
                    }
                } catch (NumberFormatException e) {
                    return HttpResponse.notFound();
                }
            }
            return HttpResponse.ok(new Gson().toJson(BookDao.getAllBooks()));
        }

        if (method.equals("DELETE") && route.startsWith("/books/")) {
            String[] parts = route.split("/");
            if (parts.length == 3) {
                try {
                    int bookId = Integer.parseInt(parts[2]);
                    Book book = BookDao.getBookById(bookId);

                    if (book != null) {
                        BookDao.deleteBookById(bookId);
                        return HttpResponse.ok("Book deleted successfully.");
                    } else {
                        return HttpResponse.notFound();
                    }
                } catch (NumberFormatException e) {
                    return HttpResponse.notFound();
                }
            }
        }

        if (method.equals("POST") && route.startsWith("/books")) {
            try {
                int contentLength = 0;
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);

                Book newBook = new Gson().fromJson(body.toString(), Book.class);

                if (newBook != null) {
                    BookDao.addBook(newBook);
                    String responseJson = new Gson().toJson(newBook);
                    out.write(HttpResponse.created(responseJson).getBytes());

                    return HttpResponse.created(new Gson().toJson(newBook));
                } else {
                    return HttpResponse.notFound();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return HttpResponse.notFound();
            }
        }

        if (method.equals("PUT") && route.startsWith("/books/")) {
            try {
                String[] parts = route.split("/");
                if (parts.length != 3) {
                    return HttpResponse.notFound();
                }
                int bookId = Integer.parseInt(parts[2]);

                int contentLength = 0;
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);

                Book updatedBook = new Gson().fromJson(body, Book.class);

                if (updatedBook != null) {
                    Book existingBook = BookDao.getBookById(bookId);
                    if (existingBook == null) {
                        return HttpResponse.notFound();
                    }

                    updatedBook.setId(bookId);
                    BookDao.updateBook(updatedBook);

                    return HttpResponse.ok(new Gson().toJson(updatedBook));
                } else {
                    return HttpResponse.notFound();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return HttpResponse.notFound();
            }
        }

        return HttpResponse.notFound();
    }
}
