package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
        // В остальной функции - O(1)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        var closest = root ?: return false
        var previous = closest
        var comparison = closest.value.compareTo(element)
        // O(1) - ресурсоемкость (т.к. хранятся только ссылки)
        while (comparison != 0) {
            // O(log(N)) - трудоемкость (средний случай, худший случай - N)
            previous = closest
            closest = when {
                comparison > 0 -> closest.left ?: return false
                else -> closest.right ?: return false
            }
            comparison = closest.value.compareTo(element)
        }
        val isRoot = closest == root
        val isRight = previous.right == closest
        val right = closest.right
        val left = closest.left
        // O(1) - ресурсоемкость
        val res = when {
            left == null -> right
            right == null -> left
            else -> {
                var minLeft = right!!
                var prev = minLeft
                // O(1) - ресурсоемкость
                while (minLeft.left != null) {
                    // O(log(N)) - трудоемскоть (средний случай, худший случай - N)
                    prev = minLeft
                    minLeft = minLeft.left!!
                }
                if (prev != minLeft) {
                    prev.left = minLeft.right
                    minLeft.right = right
                }
                minLeft.left = left
                minLeft
            }
        }
        when {
            isRoot -> root = res
            isRight -> previous.right = res
            else -> previous.left = res
        }
        size--
        return true
        // O(1) - ресурсоемкость
        // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    abstract inner class TreeIterator internal constructor() : MutableIterator<T> {

        // O(log(N)) - ресурсоемкость в среднем случае, O(N) - в худшем
        private val way: Stack<Node<T>> = Stack()

        private var wasDeleted: Boolean = false

        protected fun hasNext(root: Node<T>?): Boolean {
            var node = root ?: return false
            // O(1) - ресурсоемкость (т.к. хранятся только ссылки)
            if (way.isEmpty()) {
                return true
            }
            while (node.right != null) {
                // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                node = node.right!!
            }
            return node != way.peek()
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }

        protected fun next(root: Node<T>?): T {
            wasDeleted = false
            var node: Node<T>? = root ?: throw NoSuchElementException()
            // O(1) - ресурсоемкость (т.к. хранятся только ссылки)
            if (way.isEmpty()) {
                while (node != null) {
                    // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                    way.push(node)
                    node = node.left
                }
                return way.peek().value
            }
            val element = way.pop()
            // O(1) - ресурсоемкость
            if (element.right == null) {
                node = element
                while (node!!.value <= element.value && way.isNotEmpty()) {
                    // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                    node = way.pop()
                }
                if (node.value <= element.value)
                    throw NoSuchElementException()
                way.push(node)
                return node.value
            } else {
                node = element.right
                while (node != null) {
                    // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                    way.push(node)
                    node = node.left
                }
                return way.peek().value
            }
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }

        fun remove(root: Node<T>?) {
            if (way.isEmpty() || wasDeleted)
                throw IllegalStateException()
            val element = way.pop().value
            // O(1) - ресурсоемкость
            remove(element)
            previous(element, root)
            wasDeleted = true
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
            // У обеих функций (remove, previous) такие оценки
        }

        private fun previous(element: T, root: Node<T>?) {
            if (root == null) return
            var node = root
            var extraIters = 0
            // O(1) - ресурсоемкость (т.к. хранятся только ссылки)
            way.clear()
            while (node != null) {
                // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                way.add(node)
                if (node.value > element) {
                    extraIters++
                    node = node.left
                } else {
                    extraIters = 0
                    node = node.right
                }
            }
            for (i in 0 until extraIters) {
                // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
                way.pop()
            }
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }
    }

    inner class BinarySearchTreeIterator internal constructor() : TreeIterator() {

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        override fun hasNext(): Boolean {
            return hasNext(root)
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        override fun next(): T {
            return next(root)
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }

        override fun remove() {
            remove(root)
            // O(1) - ресурсоемкость
            // O(log(N)) - трудоемкость в среднем случае, O(N) - в худшем
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */

    inner class SubTree(
        private val tree: KtBinarySearchTree<T>, private val fromElement: T, private val fromStart: Boolean,
        private val toElement: T, private val toEnd: Boolean
    ) : SortedSet<T> {

        private fun inBounds(element: T) =
            (fromStart || element >= fromElement) && (toEnd || element < toElement)
        // O(1) - трудоемкость и ресурсоемкость

        override val size: Int
            get() = getSize(root())
        // O(N) - трудоемкость
        // O(log(N)) в среднем и O(N) в худшем случаях - ресурсоемкость (из-за ф-и root)

        private fun getSize(node: Node<T>?): Int {
            // В результате обходятся все ветви
            // O(N) - трудоемкость, O(1) - ресурсоемкость
            if (node == null)
                return 0
            return getSize(node.right) + 1 + getSize(node.left)
        }

        private fun root(): Node<T>? {
            val node = if (fromStart)
                tree.root else
                cutLeftPart(tree.root, fromElement)
            return if (toEnd)
                node else
                cutRightPart(node, toElement)
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
            // (в обоих случаях, при левой и правой "обрезке")
        }

        override fun add(element: T): Boolean {
            if (!inBounds(element))
                throw IllegalArgumentException()
            return tree.add(element)
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
        }

        override fun addAll(elements: Collection<T>): Boolean {
            var res = false
            for (elem in elements) {
                res = this.add(elem) || res
            }
            return res
            // O(M*log(N)) в среднем и O(M*N) в худшем случаях - трудоемкость и ресурсоемкость
        }

        override fun contains(element: T): Boolean {
            return inBounds(element) && tree.contains(element)
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            var res = false
            for (elem in elements) {
                res = this.remove(elem) || res
            }
            return res
            // O(M*log(N)) в среднем и O(M*N) в худшем случаях - трудоемкоcть
            // O(M) - ресурсоемкость
        }

        override fun first(): T {
            var current: Node<T> = root() ?: throw NoSuchElementException()
            while (current.left != null) {
                current = current.left!!
            }
            return current.value
        }

        override fun remove(element: T): Boolean {
            return if (inBounds(element))
                tree.remove(element) else
                throw IllegalArgumentException()
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкоcть
            // O(1) - ресурсоемкость
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            var res = false
            for (elem in elements) {
                if (inBounds(elem)) {
                    res = remove(elem) || res
                }
            }
            return res
        }

        override fun last(): T {
            var current: Node<T> = root() ?: throw NoSuchElementException()
            while (current.right != null) {
                current = current.right!!
            }
            return current.value
        }

        override fun clear() {
            for (elem in tree) {
                if (inBounds(elem)) {
                    tree.remove(elem)
                }
            }
        }

        override fun tailSet(fromElement: T): SortedSet<T> {
            val from = if (fromStart) fromElement else maxOf(fromElement, this.fromElement)
            return SubTree(
                tree, from, false,
                toElement, true
            )
        }

        inner class SubTreeIterator : TreeIterator() {
            override fun hasNext(): Boolean {
                return hasNext(root())
            }

            override fun next(): T {
                return next(root())
            }

            override fun remove() {
                remove(root())
            }

        }

        override fun iterator(): MutableIterator<T> {
            return SubTreeIterator()
        }

        override fun headSet(toElement: T): SortedSet<T> {
            val to = if (toEnd) toElement else maxOf(toElement, this.toElement)
            return SubTree(
                tree, fromElement, true,
                to, false
            )
        }

        override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
            val from = if (fromStart) fromElement else maxOf(fromElement, this.fromElement)
            val to = if (toEnd) toElement else maxOf(toElement, this.toElement)
            return SubTree(
                tree, from, false,
                to, false
            )
        }

        override fun containsAll(elements: Collection<T>): Boolean {
            var res = true
            for (elem in elements) {
                res = res && this.contains(elem)
            }
            return res
        }

        override fun isEmpty(): Boolean {
            return root() == null
        }

        override fun comparator(): Comparator<in T>? {
            return null
        }

        private fun cutLeftPart(node: Node<T>?, fromElement: T): Node<T>? {
            // В результате рекурсия доходит до конца дерева
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
            val newNode = Node((node ?: return null).value)
            newNode.left = node.left
            newNode.right = node.right
            if (newNode.value < fromElement) {
                return cutLeftPart(newNode.right, fromElement)
            } else {
                newNode.left = cutLeftPart(newNode.left, fromElement)
                return newNode
            }
        }

        private fun cutRightPart(node: Node<T>?, toElement: T): Node<T>? {
            // В результате рекурсия доходит до конца дерева
            // O(log(N)) в среднем и O(N) в худшем случаях - трудоемкость и ресурсоемкость
            val newNode = Node((node ?: return null).value)
            newNode.left = node.left
            newNode.right = node.right
            if (newNode.value >= toElement) {
                return cutRightPart(newNode.left, toElement)
            } else {
                newNode.right = cutRightPart(newNode.right, toElement)
                return newNode
            }
        }
    }

    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        if (fromElement > toElement) {
            throw IllegalArgumentException()
        }
        return SubTree(this, fromElement, false, toElement, false)
        // O(1) - трудоемкость и ресурсоемкость создания
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}