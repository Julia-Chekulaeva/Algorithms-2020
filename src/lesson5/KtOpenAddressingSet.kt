package lesson5

import java.lang.IllegalStateException

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private enum class Deleted {
        DELETED
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return false
            }
            if (current == Deleted.DELETED)
                break
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        // O(1) - ресурсоемкость
        while (current != null) {
            if (current == element) {
                storage[index] = Deleted.DELETED
                size--
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
        // Трудоемкость - в лучшем случае O(1), в худшем - O(N)
        // Ресурсоемкость - O(1)
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    inner class OpenAddressingSetIterator internal constructor() : MutableIterator<T> {

        private var index = -1

        override fun hasNext(): Boolean {
            var currentIndex = index + 1
            while (currentIndex < capacity) {
                if (storage[currentIndex] != null && storage[currentIndex] != Deleted.DELETED) {
                    return true
                }
                currentIndex++
            }
            return false
            // Трудоемкость в лучшем случае - O(1), в худшем - O(M), где M - размер таблицы
            // Ресурсоемкость - O(1)
        }

        override fun remove() {
            if (index < 0 || storage[index] == Deleted.DELETED)
                throw IllegalStateException()
            storage[index] = Deleted.DELETED
            size--
            // Трудоемкость - O(1)
            // Ресурсоемкость - O(1)
        }

        override fun next(): T {
            var currentIndex = index + 1
            var current: Any?
            while (currentIndex < capacity) {
                current = storage[currentIndex]
                if (current != null && current != Deleted.DELETED) {
                    index = currentIndex
                    return current as T
                }
                currentIndex++
            }
            throw NoSuchElementException()
            // Трудоемкость в лучшем случае - O(1), в худшем - O(M), где M - размер таблицы
            // Ресурсоемкость - O(1)
        }
    }

    override fun iterator(): MutableIterator<T> {
        return OpenAddressingSetIterator()
    }
}