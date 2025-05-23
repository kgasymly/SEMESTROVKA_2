
import java.io.*;
import java.util.*;


/* Задача!

    B+ дерево для системы хранения, удаления и поиска студентов по их ID,
    Вывод студентов по диапазону ID и вывод полного списка студентов

 */
public class TaskMain {


    public static void main(String[] args) {
        File dir1 = new File("C:\\Users\\dasts\\Desktop\\Itis\\lol\\Semes\\TestsFiles");

        for (int i = 0; i < 100; i++) {
            BPlusTree<Student> studentTree = new BPlusTree<>(3);
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(dir1, "Students" + (i+1) + ".txt")))) {
                String line = "";

                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    String[] parts = line.split(", ");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        double gpa = Double.parseDouble(parts[2].replace(',', '.'));

                        //Добавление студента в дерево
                        Student student = new Student(id, name, gpa);
                        studentTree.insert(student);
                        System.out.println("[Добавлен] " + student);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            //Поиск студента по его айди с выводом его данных
            int tempId = 10010;

            Student tempStudent = new Student(tempId, "", 0.0);

            List<Student> result = studentTree.rangeQuery(tempStudent, tempStudent);
            if (!result.isEmpty()) {
                System.out.println("\n[Найден] " + result.get(0));
            } else {
                System.out.println("\n[Ошибка] Студент с ID " + tempId + " не найден");
            }
            System.out.println();


            // Удаление студента по его айди из дерева
            tempId = 10080;

            tempStudent = new Student(tempId, "", 0.0);
            result = studentTree.rangeQuery(tempStudent, tempStudent);
            if (!result.isEmpty()) {
                studentTree.remove(tempStudent);
                System.out.println("[Удален] " + result.get(0));
            } else {
                System.out.println("[Ошибка] Студент с ID " + tempId + " не найден");
            }

            // Поиск в диапазоне от айди до айди

            int fromId = 10050;
            int toId = 10090;

            Student from = new Student(fromId, "", 0.0);
            Student to = new Student(toId, "", 0.0);

            List<Student> students = studentTree.rangeQuery(from, to);

            System.out.println("\n[Результат] Студенты с ID от " + fromId + " до " + toId);
            if (students.isEmpty()) {
                System.out.println("Студенты не найдены");
            } else {
                students.forEach(System.out::println);
            }

            // Вывод всех студентов

            List<Student> allStudents = studentTree.rangeQuery(
                new Student(Integer.MIN_VALUE, "", 0.0),
                new Student(Integer.MAX_VALUE, "", 0.0)
            );

            System.out.println("\n[Все студенты]");
            allStudents.forEach(System.out::println);
            System.out.println("Всего студентов: " + allStudents.size() + "\n");

        }
    }

    public static class Student implements Comparable<Student> {
        int id;
        String name;
        double gpa;

        public Student(int id, String name, double gpa) {
            this.id = id;
            this.name = name;
            this.gpa = gpa;
        }

        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.id, other.id);
        }

        @Override
        public String toString() {
            return String.format("ID: " + id + " | Имя: " + name + " | Средний балл: " + gpa);
        }
    }
}
