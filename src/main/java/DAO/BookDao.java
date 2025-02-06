/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DDBB.DatabaseConn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Book;

/**
 *
 * @author Ancor
 */
public class BookDao {

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConn.getConnection(); Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return books;
    }

    public static Book getBookById(int id) {
        Book book = new Book();

        String query = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("year")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return book;
    }

    public static void addBook(Book book) {
        try (Connection conn = DatabaseConn.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, author, year) VALUES (?, ?, ?)")) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Book deleteBookById(int id) {
        Book book = getBookById(id);
        if (book == null) {
            return null;
        }

        String query = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return book;
    }
    
    public static boolean updateBook(Book updatedBook) {
        String query = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";

        try (Connection conn = DatabaseConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, updatedBook.getTitle());
            stmt.setString(2, updatedBook.getAuthor());
            stmt.setInt(3, updatedBook.getYear());
            stmt.setInt(4, updatedBook.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
