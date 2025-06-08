import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ElectronicBookGUI extends JFrame {
    private ArrayList<Book> books = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private boolean darkMode = false;
    private JLabel themeStatusLabel;
    private JScrollPane scrollPane;
    private static final String FILE_NAME = "books.dat";


    public ElectronicBookGUI() {
        setTitle("Електронна Книга");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        topPanel.add(new JLabel(" Пошук: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        themeStatusLabel = new JLabel("Тема: Світла");
        themeStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        topPanel.add(themeStatusLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(0, 1, 5, 5));
        JButton addButton = new JButton("Додати книгу");
        JButton editButton = new JButton("Редагувати");
        JButton deleteButton = new JButton("Видалити");
        JButton sortButton = new JButton("Сортувати книги");
        JButton clearButton = new JButton("Очистити все");
        JButton pagesButton = new JButton("Редактор сторінок");
        JButton exitButton = new JButton("Вийти");

        sidePanel.add(addButton);
        sidePanel.add(editButton);
        sidePanel.add(deleteButton);
        sidePanel.add(sortButton);
        sidePanel.add(clearButton);
        sidePanel.add(pagesButton);
        sidePanel.add(exitButton);
        add(sidePanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"Назва", "Опис", "Автор", "Дата"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> createBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearBooks());
        exitButton.addActionListener(e -> exitProgram());
        pagesButton.addActionListener(e -> editPages());
        sortButton.addActionListener(e -> sortBooks());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Тема");
        JMenuItem lightItem = new JMenuItem("Світла тема");
        JMenuItem darkItem = new JMenuItem("Темна тема");
        lightItem.addActionListener(e -> switchTheme(false));
        darkItem.addActionListener(e -> switchTheme(true));
        themeMenu.add(lightItem);
        themeMenu.add(darkItem);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);

        loadBooksFromFile();
        switchTheme(false);
        setVisible(true);
    }

    private void createBook() {
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField releaseDateField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Назва книги:"));
        panel.add(titleField);
        panel.add(new JLabel("Опис книги:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Автор:"));
        panel.add(authorField);
        panel.add(new JLabel("Дата випуску (рік-місяць-день):"));
        panel.add(releaseDateField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Створити нову книгу", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Book book = new Book(titleField.getText(), descriptionField.getText(), authorField.getText(), releaseDateField.getText());
            book.getPages().add(new Page(""));
            books.add(book);
            refreshTable();
            saveBooksToFile();
        }
    }

    private void editBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Виберіть книгу.");
            return;
        }
        Book book = books.get(row);
        JTextField titleField = new JTextField(book.getTitle());
        JTextField descriptionField = new JTextField(book.getDescription());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField releaseDateField = new JTextField(book.getReleaseDate());
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Назва книги:"));
        panel.add(titleField);
        panel.add(new JLabel("Опис книги:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Автор:"));
        panel.add(authorField);
        panel.add(new JLabel("Дата випуску:"));
        panel.add(releaseDateField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Редагувати книгу", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            book.setTitle(titleField.getText());
            book.setDescription(descriptionField.getText());
            book.setAuthor(authorField.getText());
            book.setReleaseDate(releaseDateField.getText());
            refreshTable();
            saveBooksToFile();
        }
    }

    private void editPages() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Виберіть книгу для редагування сторінок.");
            return;
        }

        Book book = books.get(row);
        if (book.getPages() == null || book.getPages().isEmpty()) {
            book.setPages(new ArrayList<>());
            book.getPages().add(new Page(""));
        }

        JDialog dialog = new JDialog(this, "Редактор сторінок — " + book.getTitle(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JLabel pageLabel = new JLabel("", SwingConstants.CENTER);
        pageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton prevButton = new JButton("⬅ Попередня");
        JButton nextButton = new JButton("➡ Наступна");
        JButton addButton = new JButton("➕ Додати");
        JButton deleteButton = new JButton("🗑 Видалити");
        JButton saveButton = new JButton("💾 Зберегти");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(pageLabel, BorderLayout.SOUTH);

        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        final int[] currentPage = {0};
        Runnable updateView = () -> {
            textArea.setText(book.getPages().get(currentPage[0]).getContent());
            pageLabel.setText("Сторінка: " + (currentPage[0] + 1) + " із " + book.getPages().size());
        };
        updateView.run();

        prevButton.addActionListener(e -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                updateView.run();
            }
        });
        nextButton.addActionListener(e -> {
            if (currentPage[0] < book.getPages().size() - 1) {
                currentPage[0]++;
                updateView.run();
            }
        });
        addButton.addActionListener(e -> {
            book.getPages().add(new Page(""));
            currentPage[0] = book.getPages().size() - 1;
            updateView.run();
        });
        deleteButton.addActionListener(e -> {
            if (book.getPages().size() > 1) {
                book.getPages().remove(currentPage[0]);
                if (currentPage[0] >= book.getPages().size()) currentPage[0]--;
                updateView.run();
            }
        });
        saveButton.addActionListener(e -> {
            book.getPages().get(currentPage[0]).setContent(textArea.getText());
            saveBooksToFile();
            updateView.run();
        });

        dialog.setVisible(true);
    }

    private void sortBooks() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        String[] fields = {"Назва", "Автор", "Дата"};
        String[] directions = {"За зростанням", "За спаданням"};

        JComboBox<String> fieldBox = new JComboBox<>(fields);
        JComboBox<String> directionBox = new JComboBox<>(directions);

        panel.add(new JLabel("Сортувати за:"));
        panel.add(fieldBox);
        panel.add(new JLabel("Напрям сортування:"));
        panel.add(directionBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Сортування книг", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedField = (String) fieldBox.getSelectedItem();
            String selectedDirection = (String) directionBox.getSelectedItem();

            Comparator<Book> comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
            if ("Автор".equals(selectedField)) {
                comparator = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
            } else if ("Дата".equals(selectedField)) {
                comparator = Comparator.comparing(Book::getReleaseDate, String.CASE_INSENSITIVE_ORDER);
            }
            if ("За спаданням".equals(selectedDirection)) {
                comparator = comparator.reversed();
            }

            books.sort(comparator);
            refreshTable();
            saveBooksToFile();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getTitle(), b.getDescription(), b.getAuthor(), b.getReleaseDate()});
        }
    }

    private void filter() {
        String text = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(text)
                    || b.getAuthor().toLowerCase().contains(text)
                    || b.getReleaseDate().toLowerCase().contains(text)) {
                tableModel.addRow(new Object[]{b.getTitle(), b.getDescription(), b.getAuthor(), b.getReleaseDate()});
            }
        }
    }

    private void switchTheme(boolean dark) {
        darkMode = dark;
        Color bg = dark ? new Color(30, 30, 30) : Color.WHITE;
        Color fg = dark ? Color.LIGHT_GRAY : Color.BLACK;
        Color panelBg = dark ? new Color(45, 45, 45) : new JPanel().getBackground();
        Color buttonBg = dark ? new Color(60, 60, 60) : UIManager.getColor("Button.background");

        getContentPane().setBackground(panelBg);
        for (Component comp : getContentPane().getComponents()) {
            updateComponentTheme(comp, fg, panelBg, buttonBg);
        }

        table.setBackground(bg);
        table.setForeground(fg);
        table.setSelectionBackground(dark ? new Color(70, 70, 70) : Color.LIGHT_GRAY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(dark ? Color.GRAY : Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(dark ? new Color(50, 50, 50) : Color.LIGHT_GRAY);
        header.setForeground(dark ? Color.LIGHT_GRAY : Color.BLACK);

        scrollPane.getViewport().setBackground(bg);

        themeStatusLabel.setForeground(fg);
        themeStatusLabel.setText("Тема: " + (dark ? "Темна" : "Світла"));
    }

    private void updateComponentTheme(Component comp, Color fg, Color bg, Color btnBg) {
        if (comp instanceof JPanel || comp instanceof JScrollPane) {
            comp.setBackground(bg);
            if (comp instanceof Container) {
                for (Component child : ((Container) comp).getComponents()) {
                    updateComponentTheme(child, fg, bg, btnBg);
                }
            }
        } else if (comp instanceof JLabel || comp instanceof JTextField) {
            comp.setForeground(fg);
            comp.setBackground(bg);
        } else if (comp instanceof JButton) {
            comp.setForeground(fg);
            comp.setBackground(btnBg);
        }
    }

    private void loadBooksFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                books = (ArrayList<Book>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                books = new ArrayList<>();
            }
        } else {
            books = new ArrayList<>();
        }
        books.removeIf(book ->
                book == null ||
                        book.getTitle() == null || book.getTitle().isBlank() ||
                        book.getAuthor() == null || book.getAuthor().isBlank() ||
                        book.getDescription() == null || book.getDescription().isBlank() ||
                        book.getReleaseDate() == null || book.getReleaseDate().isBlank()
        );

        refreshTable();
    }

    private void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(books);
        } catch (IOException ignored) {}
    }

    private void clearBooks() {
        books.clear();
        refreshTable();
        saveBooksToFile();
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row != -1) {
            books.remove(row);
            refreshTable();
            saveBooksToFile();
        }
    }

    private void exitProgram() {
        saveBooksToFile();
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ElectronicBookGUI::new);
    }
}
