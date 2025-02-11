package mikhail.shell.security.methods

import mikhail.shell.web.application.mikhail.shell.security.methods.findGenerator
import mikhail.shell.web.application.mikhail.shell.security.methods.generatePrime
import mikhail.shell.web.application.mikhail.shell.security.methods.generateSafePrime
import mikhail.shell.web.application.mikhail.shell.security.methods.generateSecretKey
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.security.MessageDigest


class Lab1Tests {
    @Test
    fun DiffieHellmanProtocolTest() {

        val q = generatePrime(256)
        val p = generateSafePrime(q)
        val g = findGenerator(p, q)

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

        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val secretBytes = sharedSecretA.toByteArray()
            val hash = digest.digest(secretBytes)
            println("Результирующий ключ K (SHA-256 от общего секрета):")
            println(hash.joinToString("") { "%02x".format(it) })
            val hashBigInteger = BigInteger(1, hash)
            println(hashBigInteger)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (sharedSecretA == sharedSecretB)
            println("Общий секрет успешно установлен!")
        else
            println("Ошибка: вычисленные общие секреты не совпадают!")
        println()
    }

    @Test
    fun MQVProtocolTest() {
        println("====== Параметры протокола =====")
        val q = generatePrime(256)
        println("q = $q")
        val l = q.bitLength() / 2
        println("l = $l")
        val p = generateSafePrime(q)
        println("p = $p")

        println("==== Долговременные секретные ключи =====")
        val a = generateSecretKey(q)
        println("a = $a")
        val b = generateSecretKey(q)
        println("b = $b")
        println("===== Генератор циклической подгруппы  ====")
        val g = findGenerator(p, q)
        println("g = $g")
        println("==== Долговременные открытые ключи =====")
        val A = g.modPow(a, p)
        println("A = $A")
        val B = g.modPow(b,p)
        println("B = $B")

        println("==== Сеансовые секретные ключи =====")
        val x = generateSecretKey(q)
        println("x = $x")
        val y = generateSecretKey(q)
        println("y = $y")
        println("==== Сеансовые открытые ключи =====")
        val X = g.modPow(x, p)
        println("X = $X")
        val Y = g.modPow(y, p)
        println("Y = $Y")
        val h = BigInteger.TWO.pow(l)

        println("==== Оба пользователя вычисляют d и e =====")
        val d = h + X.mod(h)
        println("d = $d")
        val e = h + Y.mod(h)
        println("e = $e")


        println("==== Оба пользователя вычисляют общий секретный ключ =====")
        val SA = (Y * B.modPow(e, p)).modPow((x + d * a).mod(q), p)
        println("SA = $SA")
        val SB = (X * A.modPow(d, p)).modPow((y + e * b).mod(q), p)
        println("SB = $SB")
        val md = MessageDigest.getInstance("SHA-256")
        println("==== Хэш общего ключа =====")
        val K1 = md.digest(SA.toByteArray()).joinToString("") { "%02x".format(it) }
        println("K1 = $K1")
        val K2 = md.digest(SB.toByteArray()).joinToString("") { "%02x".format(it) }
        println("K2 = $K2")
        if (K1 == K2) {
            println("Ключи совпадают")
        }
    }
}