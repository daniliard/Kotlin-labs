import kotlinx.coroutines.*

class Student(name: String, age: Int, grades: List<Int>) {
    private var _name: String = name.trim().replaceFirstChar { it.uppercaseChar() }
    private var _age: Int = age
    private var _grades: List<Int> = grades

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

    val isAdult: Boolean
        get() = _age >= 18

    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    init {
        println("Student '$name' created via primary constructor")
    }

    constructor(name: String) : this(name, 0, emptyList()) {
        println("Student '$name' created via secondary constructor")
    }

    fun getAverage(): Double = grades.average()

    fun processGrades(operation: (Int) -> Int) {
        _grades = _grades.map(operation)
    }

    fun updateGrades(newGrades: List<Int>) {
        _grades = newGrades
    }

    operator fun plus(other: Student): Student {
        val combinedGrades = this.grades + other.grades
        return Student(name = "$name & ${other.name}", age = maxOf(age, other.age), grades = combinedGrades)
    }

    operator fun times(multiplier: Int): Student {
        return Student(name, age, grades.map { it * multiplier })
    }

    override operator fun equals(other: Any?): Boolean {
        if (other !is Student) return false
        return this.name == other.name && this.getAverage().toInt() == other.getAverage().toInt()
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
    val student1 = Student(name = "  alina  ", age = 17, grades = listOf(80, 75, 90))
    val student2 = Student("Bohdan", 19, listOf(95, 88, 93))
    val student3 = Student("LazyGuy")

    println("${student1.name}'s status: ${student1.status}")
    println("${student1.name}'s average: ${student1.getAverage()}")

    val scaled = student1 * 2
    println("Scaled grades: ${scaled.grades}")

    val combined = student1 + student2
    println("Combined grades: ${combined.grades}")

    println("Are student2 and combined equal? ${student2 == combined}")

    val group = Group(student1, student2, scaled)
    println("Top student: ${group.getTopStudent()?.name}")

    println("Fetching grades asynchronously...")
    val fetchedGrades = async { fetchGradesFromServer() }
    student3.updateGrades(fetchedGrades.await())
    println("Grades updated for ${student3.name}: ${student3.grades}")
}
