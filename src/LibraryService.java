import  java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LibraryService extends Remote {
    public Book addBook(Book book) throws RemoteException;
    public Book searchBook(String isbn) throws RemoteException;
    public Book updateBook(Book book) throws RemoteException;
    public void deleteBook(String isbn) throws RemoteException;
    public List<Book> viewAllBooks() throws RemoteException;

}

