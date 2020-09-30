package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortTimes(String inputName, String outputName) throws IOException {
        String regex = "(0[1-9]|1[0-2]):([0-5]\\d):[0-5]\\d [PA]M";
        int midDay = 12 * 3600;
        BufferedReader reader = new BufferedReader(new FileReader(inputName));
        List<String> text = reader.lines().collect(Collectors.toList()); // O(N) - ресурсоемкость
        System.out.println(text);
        int[] times = new int[text.size()]; // O(N) - ресурсоемкость
        int i = 0;
        for (String line : text) {
            // O(N) - ресурсоемкость
            if (!Pattern.matches(regex, line)) {
                throw new IllegalArgumentException();
            }
            String[] split = line.split(" ");
            String[] digits = split[0].split(":");
            if (digits[0].equals("12"))
                digits[0] = "00";
            int fullTime = 0;
            for (int j = 0; j < 3; j++) {
                fullTime = fullTime * 60 + Integer.parseInt(digits[j]);
            }
            if (split[1].equals("PM")) {
                fullTime += midDay;
            }
            times[i++] = fullTime % (midDay * 2);
        }
        text.clear();
        Sorts.mergeSort(times);
        //O(N*log(N)) - трудоемкость
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
        for (int time : times) {
            //O(N) - трудоемкость
            Integer hours = (time / 3600) % 12;
            if (hours.equals(0))
                hours = 12;
            Integer minutes = time / 60 % 60;
            Integer seconds = time % 60;
            String end = (time / midDay == 0) ? "AM" : "PM";
            writer.write(String.format("%02d:%02d:%02d ", hours, minutes, seconds) + end);
            writer.newLine();
        }
        writer.close(); // O(N) - ресурсоемкость
        // O(N) + O(N*log(N)) + O(N) = O(N*logN) - трудоемкость
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static class Address implements Comparable {

        public Address(String s, int i) {
            this.street = s;
            this.house = i;
        }

        String street;

        Integer house;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return Objects.equals(street, address.street) &&
                    Objects.equals(house, address.house);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, house);
        }

        @Override
        public int compareTo(@NotNull Object o) {
            if (getClass() != o.getClass()) throw new IllegalArgumentException();
            Address address = (Address) o;
            return (street.compareTo(address.street) == 0) ? house.compareTo(address.house) : street.compareTo(address.street);
        }

        @Override
        public String toString() {
            return street + " " + house;
        }
    }

    static class Human implements Comparable {

        String surname;

        String name;

        public Human(String s, String s1) {
            surname = s;
            name = s1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Human human = (Human) o;
            return Objects.equals(surname, human.surname) &&
                    Objects.equals(name, human.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(surname, name);
        }

        @Override
        public int compareTo(@NotNull Object o) {
            if (getClass() != o.getClass()) throw new IllegalArgumentException();
            Human human = (Human) o;
            return (surname.compareTo(human.surname) == 0) ? name.compareTo(human.name) : surname.compareTo(human.surname);
        }

        @Override
        public String toString() {
            return surname + " " + name;
        }
    }

    static public void sortAddresses(String inputName, String outputName) throws IOException {
        String regex = "[А-ЯЁA-Z][а-яА-ЯёЁ\\w]+ [А-ЯЁA-Z][а-яА-ЯёЁ\\w]+ - [А-ЯЁA-Z][а-яА-ЯёЁ\\w\\-]+ \\d+";
        InputStreamReader sr = new InputStreamReader(new FileInputStream(inputName), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(sr);
        List<String> text = reader.lines().collect(Collectors.toList()); // O(N) - ресурсоемкость
        List<Address> keys = new ArrayList<>(); // O(N) (в худшем сл.) - ресурсоемкость
        Map<Address, List<Human>> addressHumanMap = new HashMap<>();
        // O(N) (учитывая вложенные объекты Human) - ресурсоемкость
        for (String line : text) {
            //O(N) - трудоемкость
            Pattern rg = Pattern.compile(regex);
            Matcher m = rg.matcher(line);
            if (!Pattern.matches(regex, line))
                throw new IllegalArgumentException();
            String[] split = line.split(" ");
            Address address = new Address(split[3], Integer.parseInt(split[4]));
            Human human = new Human(split[0], split[1]);
            if (!addressHumanMap.containsKey(address)) {
                addressHumanMap.put(address, new ArrayList<>());
                keys.add(address);
            }
            addressHumanMap.get(address).add(human);
        }
        keys.sort(Address::compareTo);
        //O(N*log(N)) - трудоемкость
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(outputName), StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(os);
        for (Address address : keys) {
            //O(N) с учетом внутр.цикла - трудоемкость
            StringBuilder sb = new StringBuilder();
            sb.append(address.toString());
            sb.append(" - ");
            addressHumanMap.get(address).sort(Human::compareTo);
            for (Human human : addressHumanMap.get(address)) {
                sb.append(human.toString());
                sb.append(", ");
            }
            String string = sb.toString();
            writer.write(string.substring(0, string.length() - 2));
            writer.newLine();
        }
        writer.close(); // O(N) - ресурсоемкость
        // O(N) + O(N*log(N)) + O(N) = O(N*logN) - трудоемкость
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    static public void sortTemperatures(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) throws IOException {
        String regex = "\\d+";
        // Ассоц. массив <число - кол-во повторений числа>
        // K - кол-во неповт. чисел
        Map<Integer, Integer> map = new HashMap<>(); // O(K) - ресурсоемкость (O(N) - в худшем случае, O(1) - в лучшем)
        BufferedReader reader = new BufferedReader(new FileReader(inputName));
        List<String> text = reader.lines().collect(Collectors.toList()); // O(N) - ресурсоемкость
        for (String s : text) {
            // O(N) - трудоемкость
            if (!Pattern.matches(regex, s))
                throw new IllegalArgumentException();
            Integer num = Integer.parseInt(s);
            map.merge(num, 1, Integer::sum);
        }
        // Чтобы провести сортировку на массиве, создаем массив (мн-во исходных чисел) и присваиваем туда все ключи
        int[] nums = new int[map.size()]; // O(K) - ресурсоемкость
        int i = 0;
        for (Integer key : map.keySet()) {
            // O(K) - трудоемкость
            nums[i++] = key;
        }
        // Сортировка массива
        Sorts.mergeSort(nums);
        // O(K*log(K)) - трудоемкость
        int maxCount = 0;
        int neededNum = 0;
        for (i = 0; i < nums.length; i++) {
            // O(K) - трудоемкость
            if (map.get(nums[i]) > maxCount) {
                maxCount = map.get(nums[i]);
                neededNum = nums[i];
            }
        }
        String res = String.valueOf(neededNum);
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(outputName));
        BufferedWriter writer = new BufferedWriter(os);
        i = 0;
        for (String line : text) {
            //O(N) - трудоемкость
            if (text.get(i++).equals(res)) {
                continue;
            }
            writer.write(line);
            writer.newLine();
        }
        for (i = 0; i < maxCount; i++) {
            // O(maxCount) - трудоемкость (O(N) - в худшем случае, O(1) - в лучшем)
            writer.write(res);
            writer.newLine();
        }
        writer.close();// O(K) + O(N) + O(K) = O(N) - ресурсоемкость
        // O(N) + O(K) + O(K*log(K)) + O(K) + O(N) + O(maxCount) = O(N) + O(K*log(K))
        // (верхняя оценка - O(N*log(N))) - трудоемкость
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
