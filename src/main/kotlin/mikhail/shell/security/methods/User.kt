package mikhail.shell.security.methods

import kotlinx.coroutines.*
import mikhail.shell.web.application.mikhail.shell.security.methods.generateSecretKey
import java.io.BufferedReader
import java.io.PrintWriter
import java.math.BigInteger
import java.net.Socket

class User(private val number: Int) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var x: BigInteger
    lateinit var X: BigInteger
    private lateinit var sharedSecretKey: BigInteger
    lateinit var q: BigInteger
    lateinit var g: BigInteger
    lateinit var p: BigInteger
    lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader
    fun connect(server: Server) {
        server.acceptConnection(number)
        try {
            socket = Socket("127.0.0.1", 12345)
        } catch (e: Exception) {
            socket = Socket("127.0.0.1", 12346)
        }
        writer = PrintWriter(socket.getOutputStream())
        reader = BufferedReader(socket.getInputStream().reader())
        server.exchangeData(number)
        q = readLine()?.toBigInteger() ?: BigInteger.TWO
        g = readLine()?.toBigInteger() ?: BigInteger.TWO
        p = readLine()?.toBigInteger() ?: BigInteger.TWO
        x = generateSecretKey(q)
        X = g.modPow(x, p)
        writer.println()
        writer = PrintWriter(socket.getOutputStream())
        writer.println(X)
        reader = BufferedReader(socket.getInputStream().reader())
    }
    fun handshake() {
        val Y: BigInteger = reader.readLine().toBigInteger()
        sharedSecretKey = Y.modPow(x, p)
    }
}