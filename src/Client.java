import java.awt.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.CardLayout;

public class Client extends JFrame {
    public JFrame frame;
    public JPanel panel;

    public Client(){
        frame = new JFrame("Library Management");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());
        initializeUI();
    }

    private void initializeUI(){
        frame.add("addBookPanel", addBook());
        frame.add("editBookPanel", editBook());
        frame.add("viewBookPanel", viewBook());
        frame.add("deleteBookPanel", deleteBook());
        frame.add("searchBookPanel", searchBook());

        frame.setVisible(true);
    }
    private JPanel addBook(){
        frame = new JFrame();
        frame.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Add New Book"));

        panel.add(new JLabel("ISBN:"));
        JTextField txtISBN = new JTextField();
        panel.add(txtISBN);

        panel.add(new JLabel());
        JTextField txtTitle = new JTextField();
        panel.add(txtTitle);

        panel.add(new JLabel());
        JTextField txtAuthor = new JTextField();
        panel.add(txtAuthor);

        panel.add(new JLabel());
        JTextField txtPublisher = new JTextField();
        panel.add(txtPublisher);

        panel.add(new JLabel("Year:"));
        JTextField txtYear = new JTextField();
        panel.add(txtYear);

        panel.add(new JLabel("Quantity:"));
        JTextField txtQuantity = new JTextField();
        panel.add(txtQuantity);

        JButton btnAdd = new JButton("Add Book");
        btnAdd.addActionListener(e -> addBook());
        panel.add(btnAdd);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> frame.dispose());
        panel.add(btnCancel);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        return panel;
    }

    private JPanel editBook(){
        panel.add(new JLabel("Edit book"));

        return panel;
    }

    private JPanel viewBook(){

        return panel;
    }

    private  JPanel deleteBook(){

        return panel;
    }

    private JPanel searchBook(){

        return panel;
    }
    public static void main(String[] args){
        try{
            Client client = new Client();
            client.connectToServer();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void connectToServer(){
        try{
            LibraryService libraryService = (LibraryService)
                    Naming.lookup("rmi://localhost:1099/LibraryService");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
