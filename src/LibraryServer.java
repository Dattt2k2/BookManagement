import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class LibraryServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            LibraryService libraryService = new LibraryServiceImpl();

            Naming.rebind("rmi://localhost:1099/LibraryService", libraryService);

            System.out.println("Library Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}