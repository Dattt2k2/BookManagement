import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.util.List;

public class Client extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LibraryService libraryService;
    private HomePanel homePanel;
    private Book book;

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
        homePanel = new HomePanel(libraryService);
        homePanel.loadBookList();

        mainPanel.add(homePanel, "Home");
//        HomePanel homePanel = new HomePanel(libraryService);
        mainPanel.add(new AddBookPanel(libraryService, homePanel), "Add Book");
        mainPanel.add(new EditBookPanel(libraryService, homePanel, book ), "Edit Book");

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

        btnHome.addActionListener(e -> {cardLayout.show(mainPanel, "Home"); homePanel.loadBookList();});
        btnAddBook.addActionListener(e -> cardLayout.show(mainPanel, "Add Book"));
//        btnViewBook.addActionListener(e -> cardLayout.show(mainPanel, "View Book"));
        btnEditBook.addActionListener(e -> {
            Book seletedBook = homePanel.getSelectedBook();
            if(seletedBook != null){
                mainPanel.remove(mainPanel.getComponent(2));
                mainPanel.add(new EditBookPanel(libraryService, homePanel, seletedBook), "Edit Book");
                cardLayout.show(mainPanel, "Edit Book");
            }else{
                JOptionPane.showMessageDialog(
                        null,
                        "Please select a book from the table",
                        "No selection",
                        JOptionPane.WARNING_MESSAGE);

            }
        });
//        btnEditBook.addActionListener(e -> cardLayout.show(mainPanel, "Edit Book"));
        panel.add(btnHome);
        panel.add(btnAddBook);
//        panel.add(btnViewBook);
        panel.add(btnEditBook);
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

class HomePanel extends JPanel implements BookUpdateListener {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private LibraryService libraryService;
    private Book selectedBook;
    private JTextField searchField;

    public HomePanel(LibraryService libraryService){
        this.libraryService = libraryService;
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Author", "Publisher", "Year", "Quantity"};

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Show all");
        searchPanel.add(new JLabel("Search ISBN:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);



        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(30);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bookTable.setSelectionBackground(new Color(184, 207, 229));
        JScrollPane scrollPane = new JScrollPane(bookTable);



        JButton deleteButton = new JButton("Delete Book");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadBookList();
        getSelectedBook();
        deleteButton.addActionListener(e ->{
            Book bookToDelete = getSelectedBook();
            if(bookToDelete != null){
                try{
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure want to Delete this book?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );
                    if(confirm == JOptionPane.YES_OPTION){
                        libraryService.deleteBook(bookToDelete.getIsbn());
                        loadBookList();
                        selectedBook = null;
                    }
                } catch (RemoteException ex){
                    JOptionPane.showMessageDialog(
                            this,
                            "Error deleting book:" +ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }else{
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a book to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        searchButton.addActionListener(e ->{
            String searchIsbn = searchField.getText().trim();
            if(!searchIsbn.isEmpty()){
                try{
                    Book book = libraryService.searchBook(searchIsbn);
                    if(book != null){
                        tableModel.setRowCount(0);
                        tableModel.addRow(new Object[]{
                                book.getIsbn(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getPublisher(),
                                book.getYear(),
                                book.getQuantity()
                        });
                    }else{
                        JOptionPane.showMessageDialog(
                                this,
                                "No Book found with ISBN: " + searchIsbn,
                                "Not Found",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
            }
        });

        refreshButton.addActionListener(e->{
            searchField.setText("");
            loadBookList();
        });

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

//    Book getSelectedBook(){
//        bookTable.getSelectionModel().addListSelectionListener(e -> {
//            if(!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1){
//                int selectedRow = bookTable.getSelectedRow();
//                selectedBook = new Book(
//                        (String) tableModel.getValueAt(selectedRow, 0),
//                        (String) tableModel.getValueAt(selectedRow, 1),
//                        (String) tableModel.getValueAt(selectedRow, 2),
//                        (String) tableModel.getValueAt(selectedRow, 3),
//                        (int) tableModel.getValueAt(selectedRow, 4),
//                        (int) tableModel.getValueAt(selectedRow, 5)
//                );
//            }
//        });
//
//        return null;
//    }
public Book getSelectedBook() {
    int selectedRow = bookTable.getSelectedRow();
    if (selectedRow != -1) {
        return new Book(
                (String) tableModel.getValueAt(selectedRow, 0),
                (String) tableModel.getValueAt(selectedRow, 1),
                (String) tableModel.getValueAt(selectedRow, 2),
                (String) tableModel.getValueAt(selectedRow, 3),
                (int) tableModel.getValueAt(selectedRow, 4),
                (int) tableModel.getValueAt(selectedRow, 5)
        );
    }
    return null;
}
    void loadBookList(){
        try{
            List<Book> books = libraryService.viewAllBooks();
            tableModel.setRowCount(0);
            for (Book book : books){
                tableModel.addRow(new Object[]{
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getYear(),
                        book.getQuantity()
                });
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }
    public void onBookListUpdated(){
        loadBookList();
    }



}

class AddBookPanel extends JPanel{
    private JTextField isbnField, titleField, authorField, publisherField, yearField, quantityField;
    private LibraryService libraryService;
    private BookUpdateListener listener;

    private void addLabel(String text, int y, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        gbc.gridx = 0;
        gbc.gridy = y;
        add(label, gbc);
    }
    private JTextField addTextField(int y, GridBagConstraints gbc) {
        JTextField textField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = y;
        add(textField, gbc);
        return textField;
    }
    public AddBookPanel(LibraryService libraryService, BookUpdateListener listener) {
        this.libraryService = libraryService;
        this.listener = listener;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField(15);
        formPanel.add(isbnField);

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField(15);
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField(15);
        formPanel.add(authorField);

        formPanel.add(new JLabel("Publisher:"));
        publisherField = new JTextField(15);
        formPanel.add(publisherField);

        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField(15);
        formPanel.add(yearField);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(15);
        formPanel.add(quantityField);

        JPanel centerPanel = new JPanel();
        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Book");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addBook());

        setPreferredSize(new Dimension(400, 300));
    }

    private void addBook(){
        try{
            String isbn = isbnField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();
            int year = Integer.parseInt(yearField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            Book newBook = new Book(isbn, title, author, publisher, year,quantity);
            libraryService.addBook(newBook);

            JOptionPane.showMessageDialog(this, "Book added successfully");
            clearFields();
            if(listener != null){
                listener.onBookListUpdated();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Year and Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add book. Server error.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    private void clearFields() {
        isbnField.setText("");
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        yearField.setText("");
        quantityField.setText("");
    }
}


//class EditBookPanel extends JPanel {
//    private JTextField isbnField, titleField, authorField, publisherField, yearField, quantityField;
//    private LibraryService libraryService;
//    private BookUpdateListener listener;
//    private HomePanel homePanel;
//    private  Book book;
//
//    public EditBookPanel(LibraryService libraryService, BookUpdateListener listener) {
//        this.libraryService = libraryService;
//        this.listener = listener;
//        this.homePanel = homePanel;
//
//        setLayout(new BorderLayout());
//
//        JLabel titleLabel = new JLabel("Edit Book", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        add(titleLabel, BorderLayout.NORTH);
//
//        // Form panel
//        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
//        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        formPanel.add(new JLabel("ISBN:"));
//        isbnField = new JTextField(15);
//        isbnField.setEditable(false); // ISBN shouldn't be editable
//        formPanel.add(isbnField);
//
//        formPanel.add(new JLabel("Title:"));
//        titleField = new JTextField(15);
//        formPanel.add(titleField);
//
//        formPanel.add(new JLabel("Author:"));
//        authorField = new JTextField(15);
//        formPanel.add(authorField);
//
//        formPanel.add(new JLabel("Publisher:"));
//        publisherField = new JTextField(15);
//        formPanel.add(publisherField);
//
//        formPanel.add(new JLabel("Year:"));
//        yearField = new JTextField(15);
//        formPanel.add(yearField);
//
//        formPanel.add(new JLabel("Quantity:"));
//        quantityField = new JTextField(15);
//        formPanel.add(quantityField);
//
//        JPanel centerPanel = new JPanel(new BorderLayout());
//        centerPanel.add(formPanel, BorderLayout.NORTH);
//        add(centerPanel, BorderLayout.CENTER);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel();
//        JButton loadButton = new JButton("Load Selected Book");
//        JButton updateButton = new JButton("Update Book");
//        loadButton.setBackground(new Color(70, 130, 180));
//        loadButton.setForeground(Color.WHITE);
//        updateButton.setBackground(new Color(46, 139, 87));
//        updateButton.setForeground(Color.WHITE);
//
//        buttonPanel.add(loadButton);
//        buttonPanel.add(updateButton);
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        // Load button action
//        loadButton.addActionListener(e -> {
//            Book selectedBook = homePanel.getSelectedBook();
//            if (selectedBook != null) {
//                loadBookData(selectedBook);
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Please select a book from the home page first",
//                        "No Selection",
//                        JOptionPane.WARNING_MESSAGE);
//            }
//        });
//
//        // Update button action
//        updateButton.addActionListener(e -> updateBook());
//    }
//
//    private void loadBookData(Book book) {
//        isbnField.setText(book.getIsbn());
//        titleField.setText(book.getTitle());
//        authorField.setText(book.getAuthor());
//        publisherField.setText(book.getPublisher());
//        yearField.setText(String.valueOf(book.getYear()));
//        quantityField.setText(String.valueOf(book.getQuantity()));
//    }
//
//    private void updateBook() {
//        try {
//            if (isbnField.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(this,
//                        "Please load a book first",
//                        "No Book Loaded",
//                        JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            Book updatedBook = new Book(
//                    isbnField.getText(),
//                    titleField.getText(),
//                    authorField.getText(),
//                    publisherField.getText(),
//                    Integer.parseInt(yearField.getText()),
//                    Integer.parseInt(quantityField.getText())
//            );
//
//            libraryService.updateBook(updatedBook);
//            JOptionPane.showMessageDialog(this, "Book updated successfully");
//
//            if (listener != null) {
//                listener.onBookListUpdated();
//            }
//
//            clearFields();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this,
//                    "Please enter valid numbers for Year and Quantity",
//                    "Invalid Input",
//                    JOptionPane.ERROR_MESSAGE);
//        } catch (RemoteException ex) {
//            JOptionPane.showMessageDialog(this,
//                    "Error updating book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void clearFields() {
//        isbnField.setText("");
//        titleField.setText("");
//        authorField.setText("");
//        publisherField.setText("");
//        yearField.setText("");
//        quantityField.setText("");
//    }
//}

class EditBookPanel extends JPanel {
    private JTextField isbnField, titleField, authorField, publisherField, yearField, quantityField;
    private LibraryService libraryService;
    private BookUpdateListener listener;
    private Book book;

    public EditBookPanel(LibraryService libraryService, BookUpdateListener listener, Book selectedBook) {
        this.libraryService = libraryService;
        this.listener = listener;
        this.book = selectedBook;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField(15);
        isbnField.setEditable(false); // ISBN shouldn't be editable
        formPanel.add(isbnField);

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField(15);
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField(15);
        formPanel.add(authorField);

        formPanel.add(new JLabel("Publisher:"));
        publisherField = new JTextField(15);
        formPanel.add(publisherField);

        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField(15);
        formPanel.add(yearField);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(15);
        formPanel.add(quantityField);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update Book");
        updateButton.setBackground(new Color(46, 139, 87));
        updateButton.setForeground(Color.WHITE);
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load the book data immediately
        if (selectedBook != null) {
            loadBookData(selectedBook);
        }

        // Update button action
        updateButton.addActionListener(e -> updateBook());
    }

    private void loadBookData(Book book) {
        isbnField.setText(book.getIsbn());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        publisherField.setText(book.getPublisher());
        yearField.setText(String.valueOf(book.getYear()));
        quantityField.setText(String.valueOf(book.getQuantity()));
    }

    private void updateBook() {
        try {
            Book updatedBook = new Book(
                    isbnField.getText(),
                    titleField.getText(),
                    authorField.getText(),
                    publisherField.getText(),
                    Integer.parseInt(yearField.getText()),
                    Integer.parseInt(quantityField.getText())
            );

            libraryService.updateBook(updatedBook);
            JOptionPane.showMessageDialog(this, "Book updated successfully");

            if (listener != null) {
                listener.onBookListUpdated();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for Year and Quantity",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating book: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
