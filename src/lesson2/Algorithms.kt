@file:Suppress("UNUSED_PARAMETER")

package lesson2

import kotlin.math.min
import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    TODO()
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {
    var maxLength = 0 // K = maxLength
    var firstIndex = 0
    val firstLength = first.length
    val secondLength = second.length
    // O(1) - ресурсоемкость
    for (j in 0 until secondLength) {
        // Движемся по левой "границе" таблицы
        // В результате проходим таблицу до главной диагонали включительно
        val limit = min(firstLength, secondLength - j)
        var length = 0
        var currentLength = 0
        while (length < limit) {
            while (length < limit && first[length] == second[j + length]) {
                length++
                currentLength++
            }
            if (currentLength > maxLength) {
                firstIndex = length
                maxLength = currentLength
            }
            currentLength = 0
            length++
        }
    }
    for (i in 1 until firstLength) {
        // Движемся по верхней "границе" таблицы со словами
        // Проходим оставшиеся элементы
        val limit = min(firstLength - i, secondLength)
        var length = 0
        var currentLength = 0
        while (length < limit) {
            while (length < limit && first[i + length] == second[length]) {
                length++
                currentLength++
            }
            if (currentLength > maxLength ||
                (currentLength == maxLength && firstIndex > i + length)) {
                firstIndex = i + length
                maxLength = currentLength
            }
            currentLength = 0
            length++
        }
    }
    return first.substring(firstIndex - maxLength, firstIndex)
    // O(1) - ресурсоемкость
    // O(N*M) - трудоемкость
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {
    // N = limit, K - кол-во простых чисел, L - кол-во простых чисел от 2 до sqrt(N)
    val listOfPrimesUntilRoot = mutableListOf<Int>() // O(L) - ресурсоемкость
    val sqrt = sqrt(limit.toDouble()).toInt()
    var res = 0
    var rootInd = 0 // Индекс первого элемента после корня числа
    loop@
    for (i in 2..limit) {
        // O(N) - трудоемкость
        val sqrt2 = sqrt(i.toDouble()).toInt()
        while (rootInd < listOfPrimesUntilRoot.size && listOfPrimesUntilRoot[rootInd] <= sqrt2) {
            rootInd++
            // O(sqrt(N)) - трудоемкость (т.к. общее кол-во раз, когда этот цикл выполн. - sqrt(N))
            // Этот цикл позволяет уменьшить кол-во итераций следующего цикла на ранних стадиях,
            // т.е. сократить трудоемкость примерно в 2 раза.
        }
        for (j in 0 until rootInd) {
            // O(N*L) - трудоемкость (верхняя оценка)
            if (i % listOfPrimesUntilRoot[j] == 0)
                continue@loop
        }
        res++
        if (i <= sqrt)
            listOfPrimesUntilRoot.add(i)
    }
    return res
    // O(L) - ресурсоемкость
    // O(N) + O(sqrt(N)) + O(N*L) = O(N*L) - трудоемкость (верхняя оценка)
}
