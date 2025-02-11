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
        val q = generatePrime(256)
        val l = q.bitLength() / 2
        val p = generateSafePrime(q)
        val a = generateSecretKey(q)
        val b = generateSecretKey(q)
        val g = findGenerator(p, q)
        val A = g.modPow(a, p)
        val B = g.modPow(b,p)
        val x = generateSecretKey(q)
        val y = generateSecretKey(q)
        val X = g.modPow(x, p)
        val Y = g.modPow(y, p)
        val h = BigInteger.TWO.pow(l)
        val d = h + X.mod(h)
        val e = h + Y.mod(h)
        val SA = (Y * B.modPow(e, p)).modPow((x + d * a).mod(q), p)
        val SB = (X * A.modPow(d, p)).modPow((y + e * b).mod(q), p)
        val md = MessageDigest.getInstance("SHA-256")
        val K1 = md.digest(SA.toByteArray()).joinToString("") { "%02x".format(it) }
        val K2 = md.digest(SB.toByteArray()).joinToString("") { "%02x".format(it) }
        println(K1)
        println(K2)
        if (K1 == K2) {
            println("Ключи совпадают")
        }
    }
}