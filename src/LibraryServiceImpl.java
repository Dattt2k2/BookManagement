import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*; 
import java.util.ArrayList;
import java.util.List;

public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASS = "";     
    

    private Connection conn = null;
    private PreparedStatement stmt = null; 

    public LibraryServiceImpl() throws RemoteException {
        super();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Failed to connect to database", e);
        }
    }


    @Override
    public Book addBook(Book book) throws RemoteException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, year, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYear());
            stmt.setInt(6, book.getQuantity());

            int rowsInserted = stmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("Book added to database: " + book);
                return book;
            } else {
                System.out.println("Failed to add book to database: " + book);
                return null; 
            }

        } catch (SQLException e) {
            System.err.println("Error adding book to database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error adding book", e);
        } finally {
            closeStatement();
        }
    }

    @Override
    public Book searchBook(String isbn) throws RemoteException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Book book = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery(); 

            if (rs.next()) { 
                book = new Book();
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setQuantity(rs.getInt("quantity"));
                System.out.println("Book found in database: " + book);
                return book;
            } else {
                System.out.println("Book not found in database with ISBN: " + isbn);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error searching book in database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error searching book", e);
        } finally {
            closeStatement();
        }
    }

    @Override
    public Book updateBook(Book book) throws RemoteException {
        String sql = "UPDATE books SET title = ?, author = ?, publisher = ?, year = ?, quantity = ? WHERE isbn = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setInt(4, book.getYear());
            stmt.setInt(5, book.getQuantity());
            stmt.setString(6, book.getIsbn());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated in database: " + book);
                return book;
            } else {
                System.out.println("Book not found for update in database with ISBN: " + book.getIsbn());
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error updating book in database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error updating book", e);
        } finally {
            closeStatement();
        }
    }

    @Override
    public void deleteBook(String isbn) throws RemoteException {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            int rowsDeleted = stmt.executeUpdate(); 
            if (rowsDeleted > 0) {
                System.out.println("Book deleted from database with ISBN: " + isbn);
            } else {
                System.out.println("Book not found for deletion in database with ISBN: " + isbn);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting book from database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error deleting book", e);
        } finally {
            closeStatement(); 
        }
    }

    @Override
    public List<Book> viewAllBooks() throws RemoteException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try {
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(); 

            while (rs.next()) { 
                Book book = new Book();
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setQuantity(rs.getInt("quantity"));
                bookList.add(book);
            }
            System.out.println("Returning all books from database: " + bookList);
            return bookList;

        } catch (SQLException e) {
            System.err.println("Error viewing all books from database: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Error viewing all books", e);
        } finally {
            closeStatement(); 
        }
    }

    private void closeStatement() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing PreparedStatement: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}

