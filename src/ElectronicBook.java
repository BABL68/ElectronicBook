import java.io.*;
import java.util.*;

public class ElectronicBook {
    private static List<Book> books = new ArrayList<>();
    private static final String FILE_NAME = "books.dat";

    public static void main(String[] args) {
        loadBooksFromFile();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nМеню Електронної Книги:");
            System.out.println("1. Створити нову книгу");
            System.out.println("2. Редагувати існуючу книгу");
            System.out.println("3. Видалити книгу");
            System.out.println("4. Показати всі книги");
            System.out.println("5. Відсортувати книги");
            System.out.println("6. Очистити всі книги");
            System.out.println("7. Вийти");
            System.out.print("Виберіть дію: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Спожити переведення рядка

            switch (choice) {
                case 1:
                    createBook(scanner);
                    break;
                case 2:
                    editBook(scanner);
                    break;
                case 3:
                    deleteBook(scanner);
                    break;
                case 4:
                    displayBooks();
                    break;
                case 5:
                    sortBooks();
                    break;
                case 6:
                    clearBooks();
                    break;
                case 7:
                    saveBooksToFile();
                    System.out.println("Вихід з програми...");
                    break;
                default:
                    System.out.println("Невірний вибір! Спробуйте ще раз.");
            }
        } while (choice != 7);
    }

    private static void createBook(Scanner scanner) {
        System.out.print("Введіть назву книги: ");
        String title = scanner.nextLine();
        System.out.print("Введіть короткий опис книги: ");
        String description = scanner.nextLine();
        System.out.print("Введіть ім'я автора: ");
        String author = scanner.nextLine();
        System.out.print("Введіть дату випуску (формат: рік-місяць-день): ");
        String releaseDate = scanner.nextLine();
        books.add(new Book(title, description, author, releaseDate));
    }

    private static void editBook(Scanner scanner) {
        if (books.isEmpty()) {
            System.out.println("Немає книг для редагування.");
            return;
        }
        displayBooks();
        System.out.print("Введіть назву книги для редагування: ");
        String title = scanner.nextLine();
        int index = findBookIndex(title);
        if (index != -1) {
            Book book = books.get(index);
            System.out.println("Редагування книги: " + book);
            System.out.print("Введіть нову назву: ");
            book.setTitle(scanner.nextLine());
            System.out.print("Введіть новий опис: ");
            book.setDescription(scanner.nextLine());
            System.out.print("Введіть нового автора: ");
            book.setAuthor(scanner.nextLine());
            System.out.print("Введіть нову дату випуску: ");
            book.setReleaseDate(scanner.nextLine());
        } else {
            System.out.println("Книга не знайдена.");
        }
    }

    private static void deleteBook(Scanner scanner) {
        if (books.isEmpty()) {
            System.out.println("Немає книг для видалення.");
            return;
        }
        displayBooks();
        System.out.print("Введіть назву книги для видалення: ");
        String title = scanner.nextLine();
        int index = findBookIndex(title);
        if (index != -1) {
            books.remove(index);
            System.out.println("Книга успішно видалена.");
        } else {
            System.out.println("Книга не знайдена.");
        }
    }

    private static void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("Немає книг для показу.");
            return;
        }
        System.out.println("\nСписок всіх книг:\n");
        for (int i = 0; i < books.size(); i++) {
            System.out.println("Книга №" + (i + 1));
            System.out.println("-------------------------------");
            System.out.println(books.get(i));
            System.out.println("-------------------------------\n");
        }
    }

    private static void sortBooks() {
        if (books.isEmpty()) {
            System.out.println("Немає книг для сортування.");
            return;
        }
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Книги успішно відсортовані.");
    }

    private static void clearBooks() {
        books.clear();
        System.out.println("Усі книги очищені.");
    }

    private static void loadBooksFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                books = (List<Book>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Помилка при завантаженні книг з файлу.");
            }
        } else {
            System.out.println("Файл не знайдений, починається нова сесія.");
        }
    }

    private static void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(books);
        } catch (IOException e) {
            System.out.println("Помилка при збереженні книг у файл.");
        }
    }

    private static int findBookIndex(String title) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getTitle().equalsIgnoreCase(title)) {
                return i;
            }
        }
        return -1;
    }
}
