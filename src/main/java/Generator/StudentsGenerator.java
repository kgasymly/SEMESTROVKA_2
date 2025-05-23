package Generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

class StudentsGenerator {
    private static final String[] FIRST_NAMES = {"Александр", "Дмитрий", "Максим", "Сергей", "Андрей",
            "Алексей", "Артём", "Илья", "Кирилл", "Михаил",
            "Никита", "Матвей", "Роман", "Егор", "Арсений",
            "Иван", "Денис", "Евгений", "Даниил", "Константин"
    };
    private static final String[] LAST_NAMES = {
            "Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов",
            "Попов", "Васильев", "Павлов", "Семёнов", "Голубев",
            "Виноградов", "Богданов", "Воробьёв", "Фёдоров", "Николаев",
            "Крылов", "Максимов", "Орлов", "Андреев", "Макаров"
    };
    private static final Random random = new Random();

    public static void main(String[] args) {
        int numFiles = 100;
        int minStudents = 100;
        int maxStudents = 10000;

        for (int i = 1; i <= numFiles; i++) {
            int numStudents = minStudents + (i - 1) * (maxStudents - minStudents) / (numFiles - 1);
            String filename = "Students" + i + ".txt";
            generateStudentFile(filename, numStudents);
            System.out.println("Файл " + filename + " сгенерирован, кол-во входных данных: " + numStudents);
        }
    }

    private static void generateStudentFile(String filename, int numStudents) {

        File dir1 = new File("C:\\Users\\dasts\\Desktop\\Itis\\lol\\Semes\\TestsFiles");
        try (FileWriter fw = new FileWriter(new File(dir1,filename))) {
            int startId = 10000;

            for (int i = 0; i < numStudents; i++) {
                int id = startId + i;
                String name = generateRandomName();
                double gpa = 2.0 + random.nextDouble() * 3.0;

                fw.write(id + ", " + name + ", " + String.format("%.2f", gpa) + "\n");
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }
}