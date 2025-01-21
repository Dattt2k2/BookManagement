import java.io.Serializable;
public class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int year;
    private int quantity;

    public  Book(){

    }

    public Book(String isbn, String title, String author, String publisher, int year, int quantity){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.quantity = quantity;
    }

    public String getIsbn(){
        return isbn;
    }
    public void setIsbn(){
        this.isbn = isbn;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(){
        this.title = title;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(){
        this.author = author;
    }

    public String getPublisher(){
        return publisher;
    }

    public void setPublisher(){
        this.publisher = publisher;
    }

    public int getYear(){
        return year;
    }

    public void setYear(){
        this.year = year;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(){
        this.quantity = quantity;
    }

    @Override
    public String toString(){
        return "Book{"+
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", year=" + year +
                ", quantity=" + quantity +
                '}';
    }
}
