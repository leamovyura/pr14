import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    static final int MAX_ENTRIES = 50;
    static LocalDateTime[] dates = new LocalDateTime[MAX_ENTRIES];
    static String[] entries = new String[MAX_ENTRIES];
    static int count = 0;

    static DateTimeFormatter fileFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    static DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Вітаю в программі Мій щоденник!!!!!");
        System.out.println("1 Створити новий щоденник");
        System.out.println("2 Завантажити щоденник з файлу");
        System.out.print("Ваш вибір: ");
        String choice = scanner.nextLine();

        if (choice.equals("2")) {
            loadDiary(scanner);
        } else {
            System.out.println("Створено новий щоденник");
        }

        chooseDisplayFormat(scanner);

        boolean exit = false;
        while (!exit) {
            printMenu();
            String cmd = scanner.nextLine();
            switch (cmd) {
                case "1":
                    addEntry(scanner);
                    break;
                case "2":
                    deleteEntry(scanner);
                    break;
                case "3":
                    listEntries();
                    break;
                case "4":
                    exit = true;
                    saveOnExit(scanner);
                    break;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
        scanner.close();
    }

    static void printMenu() {
        System.out.println("\nМеню:");
        System.out.println("1 Додати запис");
        System.out.println("2 Видалити запис за датою і часом");
        System.out.println("3 Переглянути всі записи");
        System.out.println("4 Вийти");
        System.out.print("Ваш вибір: ");
    }

    static void chooseDisplayFormat(Scanner scanner) {
        System.out.println("Виберіть формат дати і часу для відображення:");
        System.out.println("1 yyyy-MM-dd HH:mm (за замовчуванням)");
        System.out.println("2 dd.MM.yyyy HH:mm");
        System.out.println("3 Ввести свій формат:");
        System.out.print("Ваш вибір: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "2":
                displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                break;
            case "3":
                System.out.print("Введіть формат: ");
                String pattern = scanner.nextLine();
                try {
                    displayFormatter = DateTimeFormatter.ofPattern(pattern);
                } catch (IllegalArgumentException e) {
                    System.out.println("Некоректний формат.");
                    displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                }
                break;
            default:
                displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }
    }

    static void addEntry(Scanner scanner) {
        if (count >= MAX_ENTRIES) {
            System.out.println("Щоденник заповнений");
            return;
        }
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.print("Введіть дату і час (наприклад 2017-06-22 18:46): ");
            String input = scanner.nextLine();
            try {
                dateTime = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Некоректний формат дати і часу.");
            }
        }

        System.out.println("Введіть текст запису (порожній рядок для завершення):");
        String text = "";
        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;
            text += line + "\n";
        }
        text = text.trim();

        dates[count] = dateTime;
        entries[count] = text;
        count++;
        System.out.println("Запис додано");
    }

    static void deleteEntry(Scanner scanner) {
        if (count == 0) {
            System.out.println("Щоденник порожній");
            return;
        }
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.print("Введіть дату і час запису для видалення: ");
            String input = scanner.nextLine();
            try {
                dateTime = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Некоректний формат дати і часу.");
            }
        }

        int idx = -1;
        for (int i = 0; i < count; i++) {
            if (dates[i].equals(dateTime)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            System.out.println("Запис не знайдено");
            return;
        }

        for (int i = idx; i < count - 1; i++) {
            dates[i] = dates[i + 1];
            entries[i] = entries[i + 1];
        }
        dates[count - 1] = null;
        entries[count - 1] = null;
        count--;
        System.out.println("Запис видалено");
    }

    static void listEntries() {
        if (count == 0) {
            System.out.println("Щоденник порожній");
            return;
        }
        System.out.println("Всі записи:");
        for (int i = 0; i < count; i++) {
            System.out.println(dates[i].format(displayFormatter));
            System.out.println(entries[i]);
            System.out.println();
        }
    }

    static void loadDiary(Scanner scanner) {
        System.out.print("Введіть шлях до файлу для завантаження: ");
        String path = scanner.nextLine();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            count = 0;
            String line;
            while ((line = br.readLine()) != null && count < MAX_ENTRIES) {
                if (line.trim().isEmpty()) continue;
                LocalDateTime dt = LocalDateTime.parse(line, fileFormatter);
                StringBuilder text = new StringBuilder();
                while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                    text.append(line).append("\n");
                }
                dates[count] = dt;
                entries[count] = text.toString().trim();
                count++;
            }
            System.out.println("Щоденник завантажено. Записів: " + count);
        } catch (IOException | DateTimeParseException e) {
            System.out.println("Помилка завантаження файлу: " + e.getMessage());
            count = 0;
        }
    }

    static void saveOnExit(Scanner scanner) {
        System.out.print("Бажаєте зберегти щоденник? (так|ні): ");
        String answer = scanner.nextLine().toLowerCase();
        if (answer.equals("так") || answer.equals("yes")) {
            System.out.print("Введіть шлях до файлу для збереження: ");
            String path = scanner.nextLine();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
                for (int i = 0; i < count; i++) {
                    bw.write(dates[i].format(fileFormatter));
                    bw.newLine();
                    bw.write(entries[i]);
                    bw.newLine();
                    bw.newLine();
                }
                System.out.println("Щоденник збережено");
            } catch (IOException e) {
                System.out.println("Помилка збереження файлу: " + e.getMessage());
            }
        } else {
            System.out.println("Щоденник не збережено");
        }
    }
}
