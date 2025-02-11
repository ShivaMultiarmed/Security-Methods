package mikhail.shell.web.application

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom


val random = SecureRandom()
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    val q = generatePrime(256)
    val p = generateSafePrime(q)
    val g = findGenerator(p, q)

    val random = SecureRandom()
    val x = generateSecretKey(q)
    val y = generateSecretKey(q)

    val X = g.modPow(x, p)
    val Y = g.modPow(y, p)

    println("=== Параметры протокола ===")
    println("p = $p")
    println("q = $q")
    println("g = $g")
    println()

    println("=== Секретные ключи ===")
    println("Секретный ключ A (x) = $x")
    println("Секретный ключ B (y) = $y")
    println()

    println("=== Открытые ключи ===")
    println("Открытый ключ A (X) = $X")
    println("Открытый ключ B (Y) = $Y")
    println()

    val sharedSecretA = Y.modPow(x, p)
    val sharedSecretB = X.modPow(y, p)

    println("Общий секрет (вычисленный A): $sharedSecretA")
    println("Общий секрет (вычисленный B): $sharedSecretB")
    if (sharedSecretA == sharedSecretB)
        println("Общий секрет успешно установлен!")
    else
        println("Ошибка: вычисленные общие секреты не совпадают!")
    println()

    try {
        val digest = MessageDigest.getInstance("SHA-256")
        val secretBytes = sharedSecretA.toByteArray()
        val hash = digest.digest(secretBytes)
        println("Результирующий ключ K (SHA-256 от общего секрета):")
        println(hash.joinToString("") { "%02x".format(it) })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun generateSecretKey(q: BigInteger): BigInteger {
    var secret: BigInteger
    do {
        secret = BigInteger(q.bitLength(), random)
    } while (secret < BigInteger.ONE || secret >= q)
    return secret
}

// Генерирует простое число длины bits
fun generatePrime(bits: Int): BigInteger {
    return BigInteger(bits, 100, random) // Reduced certainty to 10 for speed
}

// Генерирует 1024-bit простое число p = kq + 1
fun generateSafePrime(q: BigInteger): BigInteger {
    var p: BigInteger
    do {
        val k = BigInteger(768, random).setBit(767) // Ensure k is large enough
        p = k.multiply(q).add(BigInteger.ONE)
    } while (!p.isProbablePrime(100)) // Reduced certainty to 10
    return p
}

// Ищет генератор для подгруппы порядка q
fun findGenerator(p: BigInteger, q: BigInteger): BigInteger {
    val one = BigInteger.ONE
    val exponent = (p - one) / q

//    // Сначала пробуем набор известных малых кандидатов.
//    val smallCandidates = listOf(
//        BigInteger("2"),
//        BigInteger("3"),
//        BigInteger("5"),
//        BigInteger("7"),
//        BigInteger("11")
//    )
//    for (g in smallCandidates) {
//        if (g.modPow(exponent, p) != one) {
//            return g
//        }
//    }

    // Если стандартные кандидаты не подошли,
    // перебираем случайные значения из диапазона [2, p-2].
    val maxAttempts = 1000
    for (i in 0..<maxAttempts) {
        // Получаем случайное число, используя битовую длину p.
        // Чтобы число оказалось в диапазоне [2, p-2], берем остаток от деления на (p-3)
        // и прибавляем 2.
        val candidate = (BigInteger(p.bitLength(), random) % (p - BigInteger.valueOf(3))) + BigInteger.TWO
        if (candidate.modPow(exponent, p) != one) {
            return candidate
        }
    }
    throw RuntimeException("Не удалось найти генератор подгруппы порядка q за $maxAttempts попыток")
}