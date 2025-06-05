package com.example.lab3

import kotlinx.coroutines.*
import kotlin.math.max

class Student(name: String, age: Int, grades: List<Int>) {
    // Приватні поля
    private var _name: String = name.trim().replaceFirstChar { it.uppercaseChar() }
    private var _age: Int = if (age >= 0) age else 0
    private var _grades: List<Int> = grades

    // Публічні властивості з геттерами і сеттерами
    var name: String
        get() = _name
        set(value) {
            _name = value.trim().replaceFirstChar { it.uppercaseChar() }
        }

    var age: Int
        get() = _age
        set(value) {
            if (value >= 0) _age = value
        }

    val grades: List<Int>
        get() = _grades

    // isAdult - геттер
    val isAdult: Boolean
        get() = _age >= 18

    // status - lazy
    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    // init block
    init {
        println("Student '$name' created via primary constructor")
    }

    // Вторинний конструктор (лише name)
    constructor(name: String) : this(name, 0, emptyList()) {
        println("Student '$name' created via secondary constructor")
    }

    // Функція повертає середнє оцінок
    fun getAverage(): Double =
        if (grades.isNotEmpty()) grades.average() else 0.0

    // Вищий порядок: приймає функцію і змінює всі оцінки
    fun processGrades(operation: (Int) -> Int) {
        _grades = _grades.map(operation)
    }

    // Оновити оцінки
    fun updateGrades(newGrades: List<Int>) {
        _grades = newGrades
    }

    // Оператор + : об'єднує оцінки двох студентів
    operator fun plus(other: Student): Student {
        val combinedGrades = this.grades + other.grades
        return Student(
            name = "$name & ${other.name}",
            age = max(this.age, other.age),
            grades = combinedGrades
        )
    }

    // Оператор * : множить всі оцінки на число
    operator fun times(multiplier: Int): Student {
        return Student(
            name,
            age,
            grades.map { it * multiplier }
        )
    }

    // Оператор == : порівнює студентів за іменем і середньою оцінкою
    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Student) return false
        return this.name == other.name && this.getAverage().toInt() == other.getAverage().toInt()
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + getAverage().toInt()
        return result
    }

    override fun toString(): String {
        return "Student(name='$name', age=$age, grades=$grades, avg=${getAverage()}, status=$status)"
    }
}

class Group(vararg students: Student) {
    private val list = students.toList()

    operator fun get(index: Int): Student = list[index]

    fun getTopStudent(): Student? = list.maxByOrNull { it.getAverage() }
}

suspend fun fetchGradesFromServer(): List<Int> {
    delay(2000)
    return listOf(85, 90, 78, 92, 88)
}

fun main() = runBlocking {
    // Створення студентів (іменовані аргументи)
    val student1 = Student(name = "  alina  ", age = 17, grades = listOf(80, 75, 90))
    val student2 = Student("Bohdan", 19, listOf(95, 88, 93))
    val student3 = Student("LazyGuy") // secondary constructor

    println("${student1.name}'s status: ${student1.status}")
    println("${student1.name}'s average: ${student1.getAverage()}")

    val scaled = student1 * 2
    println("Scaled grades for ${scaled.name}: ${scaled.grades}")

    val combined = student1 + student2
    println("Combined grades: ${combined.grades}")

    println("Are student2 and combined equal? ${student2 == combined}")

    val group = Group(student1, student2, scaled)
    println("Top student in group: ${group.getTopStudent()?.name}")

    student1.processGrades { it + 5 }
    println("Updated grades for ${student1.name} after processGrades: ${student1.grades}")

    println("Fetching grades asynchronously for ${student3.name}...")
    val fetchedGrades = async { fetchGradesFromServer() }
    student3.updateGrades(fetchedGrades.await())
    println("Grades updated for ${student3.name}: ${student3.grades}")
}
