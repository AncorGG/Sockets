/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sockets;

/**
 *
 * @author Ancor
 */
public class HttpResponse {
    public static String ok(String body) {
        return "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + body;
    }

    public static String created(String body) {
        return "HTTP/1.1 201 Created\r\nContent-Type: application/json\r\n\r\n" + body;
    }

    public static String notFound() {
        return "HTTP/1.1 404 Not Found\r\nContent-Type: application/json\r\n\r\n{\"error\": \"Not Found\"}";
    }
}
