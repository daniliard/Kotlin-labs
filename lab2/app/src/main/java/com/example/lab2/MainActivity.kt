import kotlin.random.Random

class TooEarlyException(message: String) : Exception(message)

fun main() {
    println("=== Reaction Speed Test ===")
    println("You will have 5 attempts.")
    println("Wait for the signal and press Enter as fast as possible!")

    val results = mutableListOf<Long>()

    repeat(5) { attempt ->
        println("\nAttempt ${attempt + 1}: Get ready...")

        val delay = Random.nextLong(2000, 5000) // 2-5 секунд
        val startTime = System.currentTimeMillis()

        Thread.sleep(Random.nextLong(1000, delay - 500)) // випадкова рання затримка
        val checkTime = System.currentTimeMillis()

        if (System.`in`.available() > 0) {
            throw TooEarlyException("You pressed too early! Wait for the signal!")
        }

        val signalTime = System.currentTimeMillis()
        println("NOW! Press Enter!")

        val inputStart = System.currentTimeMillis()

        readln()
        val inputEnd = System.currentTimeMillis()

        if (inputStart - signalTime < 0) {
            println("Too early!")
            throw TooEarlyException("Pressed before signal.")
        }

        val reactionTime = inputEnd - inputStart
        println("Your reaction time: ${reactionTime} ms")
        results.add(reactionTime)
    }

    println("\n=== Results ===")
    println("Average: %.2f ms".format(results.average()))
    println("Fastest: ${results.minOrNull()} ms")
    println("Slowest: ${results.maxOrNull()} ms")
}
