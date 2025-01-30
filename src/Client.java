import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;

public class Client extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LibraryService libraryService;

    public void connectToServer(){
        try{
            libraryService = (LibraryService)
                    Naming.lookup("rmi://localhost:1099/LibraryService");
        } catch (Exception e){
            System.err.println("Error connecting to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Client(){
        super("Library Management");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToServer();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePanel(libraryService), "Home");
        mainPanel.add(new AddBookPanel(), "Add Book");
        mainPanel.add(new EditBookPanel(), "Edit Book");
        mainPanel.add(new DeleteBookPanel(), "Delete Book");

        JPanel menuPanel = createMenuPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createMenuPanel()
    {
        JPanel panel = new JPanel();
        JButton btnHome = new JButton("Home");
        JButton btnAddBook = new JButton("Add Book");
//        JButton btnViewBook  = new JButton("View Book");
        JButton btnEditBook = new JButton("Edit Book");
        JButton btnDeleteBook = new JButton("Delete Book");

        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        btnAddBook.addActionListener(e -> cardLayout.show(mainPanel, "Add Book"));
//        btnViewBook.addActionListener(e -> cardLayout.show(mainPanel, "View Book"));
        btnEditBook.addActionListener(e -> cardLayout.show(mainPanel, "Edit Book"));
        btnDeleteBook.addActionListener(e -> cardLayout.show(mainPanel, "Delete Book"));

        panel.add(btnHome);
        panel.add(btnAddBook);
//        panel.add(btnViewBook);
        panel.add(btnEditBook);
        panel.add(btnDeleteBook);
        return  panel;
    }



    public static void main(String[] args){
        try{
            Client client = new Client();
//            client.connectToServer();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}

class HomePanel extends JPanel{
    private JList<Book> bookList;
    private DefaultListModel<Book> listModel;
    private LibraryService libraryService;

    public HomePanel(LibraryService libraryService){
        this.libraryService = libraryService;
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookList);

        try{
            List<Book> books = libraryService.viewAllBooks();
            for (Book book : books){
                listModel.addElement(book);
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }

        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                Book selectedBook = bookList.getSelectedValue();
                if (selectedBook != null){


                }
            }
        });
    }
}

class AddBookPanel extends JPanel{

}


class EditBookPanel extends  JPanel{

}

class ViewBookPanel extends JPanel{

}

class DeleteBookPanel extends JPanel{

}