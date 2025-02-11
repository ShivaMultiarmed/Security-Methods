package mikhail.shell.security.methods

import mikhail.shell.web.application.mikhail.shell.security.methods.findGenerator
import mikhail.shell.web.application.mikhail.shell.security.methods.generatePrime
import mikhail.shell.web.application.mikhail.shell.security.methods.generateSafePrime
import java.io.BufferedReader
import java.io.PrintWriter
import java.math.BigInteger
import java.net.ServerSocket
import java.net.Socket

class Server {
    private val q: BigInteger = generatePrime(256)
    private val p: BigInteger = generateSafePrime(q)
    private val g: BigInteger = findGenerator(p, q)
    lateinit var X: BigInteger
    lateinit var Y: BigInteger
    private val PORT1 = 12345
    private val PORT2 = 12346
    private val serverSocket1 = ServerSocket(PORT1)
    private val serverSocket2 = ServerSocket(PORT2)
    lateinit var clientSocket1: Socket
    lateinit var clientSocket2: Socket
    private lateinit var reader1: BufferedReader
    private lateinit var reader2: BufferedReader
    private lateinit var writer1: PrintWriter
    private lateinit var writer2: PrintWriter
    fun acceptConnection(userNumber: Int) {
        if (userNumber == 1) {
            clientSocket1 = serverSocket1.accept()
        }
        if (userNumber == 2) {
            clientSocket2 = serverSocket2.accept()
        }
    }
    fun exchangeData(userNumber: Int) {
        if (userNumber == 1) {
            reader1 = BufferedReader(clientSocket1.getInputStream().reader())
            writer1 = PrintWriter(clientSocket1.getOutputStream())
            reader1.readLine()
            writer1.println(q)
            writer1.println(p)
            writer1.println(g)
            X = reader1.readLine().toBigInteger()
        } else if (userNumber == 2) {
            reader2 = BufferedReader(clientSocket2.getInputStream().reader())
            writer2 = PrintWriter(clientSocket2.getOutputStream())
            reader2.readLine()
            writer2.println(q)
            writer2.println(p)
            writer2.println(g)
            Y = reader2.readLine().toBigInteger()
        }
    }
}