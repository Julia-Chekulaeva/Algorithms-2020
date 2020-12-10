package lesson5

import org.junit.jupiter.api.assertThrows
import ru.spbstu.kotlin.generate.util.nextString
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractOpenAddressingSetTest {

    abstract fun <T : Any> create(bits: Int): MutableSet<T>

    protected fun doAddTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<Int>()
            val bitsNumber = random.nextInt(4) + 5
            val openAddressingSet = create<Int>(bitsNumber)
            assertTrue(openAddressingSet.size == 0, "Size of an empty set is not zero.")
            for (i in 1..50) {
                val nextInt = random.nextInt(32)
                val additionResult = openAddressingSet.add(nextInt)
                assertEquals(
                    nextInt !in controlSet, additionResult,
                    "An element was ${if (additionResult) "" else "not"} added when it ${if (additionResult) "was already in the set" else "should have been"}."
                )
                controlSet += nextInt
                assertTrue(nextInt in openAddressingSet, "A supposedly added element is not in the set.")
                assertEquals(controlSet.size, openAddressingSet.size, "The size of the set is not as expected.")
            }
            val smallSet = create<Int>(bitsNumber)
            assertFailsWith<IllegalStateException>("A table overflow is not being prevented.") {
                for (i in 1..4000) {
                    smallSet.add(random.nextInt())
                }
            }
        }
    }

    protected fun doRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val bitsNumber = random.nextInt(4) + 6
            val openAddressingSet = create<Int>(bitsNumber)
            for (i in 1..50) {
                val firstInt = random.nextInt(32)
                val secondInt = firstInt + (1 shl bitsNumber)
                openAddressingSet += secondInt
                openAddressingSet += firstInt
                val expectedSize = openAddressingSet.size - 1
                assertTrue(
                    openAddressingSet.remove(secondInt),
                    "An element wasn't removed contrary to expected."
                )
                assertFalse(
                    secondInt in openAddressingSet,
                    "A supposedly removed element is still in the set."
                )
                assertTrue(
                    firstInt in openAddressingSet,
                    "The removal of the element prevented access to the other elements."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
                assertFalse(
                    openAddressingSet.remove(secondInt),
                    "A removed element was supposedly removed twice."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
            }
        }
        // Мои тесты
        val emptySet = KtOpenAddressingSet<Int>(2)
        assertFalse { emptySet.remove(random.nextInt()) }
        assertEquals(0, emptySet.size)
        val bigSet = KtOpenAddressingSet<Int>(10)
        val n = 1000
        val elems = MutableList(n) { random.nextInt() }
        var size = 0
        for (i in 0 until n) {
            bigSet.add(elems[i])
            if (bigSet.size == size)
                elems.removeAt(i)
            else
                size++
        }
        val numbersCount = size
        for (i in 0 until numbersCount) {
            val index = random.nextInt(numbersCount - i) + i
            val a = elems[index]
            elems[index] = elems[i]
            elems[i] = a
        }
        var notExistingElem = random.nextInt(n)
        while (notExistingElem in elems) {
            notExistingElem = random.nextInt(n)
        }
        assertFalse { bigSet.remove(notExistingElem) }
        assertEquals(size, bigSet.size)
        for (i in 0 until numbersCount) {
            assertTrue { bigSet.remove(elems[i]) }
            size--
            assertFalse { bigSet.contains(elems[i]) }
            assertTrue { bigSet.containsAll(elems.subList(i + 1, numbersCount)) }
            assertEquals(size, bigSet.size)
        }
    }

    protected fun doIteratorTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
            }
            println("Control set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            assertFalse(
                openAddressingSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                openAddressingSet += element
            }
            val iterator1 = openAddressingSet.iterator()
            val iterator2 = openAddressingSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling OpenAddressingSetIterator.hasNext() changes the state of the iterator."
                )
            }
            val openAddressingSetIter = openAddressingSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (openAddressingSetIter.hasNext()) {
                controlSet.remove(openAddressingSetIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "OpenAddressingSetIterator doesn't traverse the entire set."
            )
            // Как я поняла, проверять в данном случае нужно на исключение NoSuchElementException,
            // а не на IllegalStateException, поэтому я исправила это в тесте ниже.
            assertFailsWith<NoSuchElementException>("Something was supposedly returned after the elements ended") {
                openAddressingSetIter.next()
            }
            println("All clear!")
        }
        // Мой тест (включает те же тесты для случая, когда элементов много и размер таблицы большой)
        val n = 10000
        val mySet = KtOpenAddressingSet<Int>(14)
        val controlSet = mutableSetOf<Int>()
        // Проверка пустого множества
        assertFalse(mySet.iterator().hasNext())
        // Добавление в множество
        for (i in 0 until n) {
            val elem = random.nextInt(n)
            mySet.add(elem)
            controlSet.add(elem)
        }
        val iterator1 = mySet.iterator()
        val iterator2 = mySet.iterator()
        // Проверка, влияет ли вызов hasNext на состояние итератора
        while (iterator1.hasNext()) {
            assertEquals(iterator2.next(), iterator1.next())
        }
        val newIterator = mySet.iterator()
        // Проверка, проходит ли итератор по всему множеству
        while (newIterator.hasNext()) {
            controlSet.remove(newIterator.next())
        }
        assertTrue(controlSet.isEmpty())
        // Проверка выброса исключения в случае, когда элементы закончились (т.е. следующего эл-та нет)
        assertFailsWith<NoSuchElementException>("Something was supposedly returned after the elements ended") {
            newIterator.next()
        }
    }

    protected fun doIteratorRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
                if (i == removeIndex) {
                    toRemove = string
                }
            }
            println("Initial set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            for (element in controlSet) {
                openAddressingSet += element
            }
            controlSet.remove(toRemove)
            println("Control set: $controlSet")
            println("Removing element \"$toRemove\" from open addressing set through the iterator...")
            val iterator = openAddressingSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = openAddressingSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                }
            }
            assertEquals(
                0, counter,
                "OpenAddressingSetIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, openAddressingSet.size,
                "The size of the set is incorrect: was ${openAddressingSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    openAddressingSet.contains(element),
                    "Open addressing set doesn't have the element $element from the control set."
                )
            }
            for (element in openAddressingSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Open addressing set has the element $element that is not in control set."
                )
            }
            println("All clear!")
        }
        // Мои тесты
        val mySet = KtOpenAddressingSet<Int>(14)
        val n = 10000
        val setForChecking = KtOpenAddressingSet<Int>(14)
        for (i in 0 until n) {
            val elem = random.nextInt(n * 10)
            mySet.add(elem)
            setForChecking.add(elem)
        }
        val iterator = mySet.iterator()
        val controlIter = setForChecking.iterator()
        var i = 0
        var size = setForChecking.size
        assertThrows<IllegalStateException> { iterator.remove() }
        for (elem in controlIter) {
            assertEquals(elem, iterator.next())
            if (i % 3 == 0) {
                iterator.remove()
                size--
                assertEquals(size, mySet.size)
                assertThrows<IllegalStateException> { iterator.remove() }
                assertEquals(size, mySet.size)
            }
            i++
        }
        assertFalse { iterator.hasNext() }
    }
}