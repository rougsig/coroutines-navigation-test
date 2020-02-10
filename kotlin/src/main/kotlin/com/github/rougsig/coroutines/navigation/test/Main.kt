package com.github.rougsig.coroutines.navigation.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

fun main() {
  val input = GlobalScope.rootRoutingFlow(::nextFlow)

  println("Type `exit` for finish process")
  println()

  runBlocking {
    while (true) {
      val next = readLine() ?: "NoName"
      if (next == "exit") {
        break
      }
      input.send(next)
    }
  }
}

fun CoroutineScope.rootRoutingFlow(
  routing: suspend (name: String, input: ReceiveChannel<String>) -> Unit
): SendChannel<String> {
  val channel = Channel<String>()

  launch {
    routing("Root", channel)
    channel.close()
  }

  return channel
}

suspend fun nextFlow(name: String, input: ReceiveChannel<String>) {
  loop@ while (true) {
    print("Input next flow name ($name) >: ")
    val action = input.receive()
    println("$name -> $action")
    when (action) {
      "result" -> {
        val result = flowWithIntResult(action, input)
        println("Result from flow: $result")
      }
      "back" -> {
        break@loop
      }
      else -> {
        nextFlow(action, input)
      }
    }
  }
}

suspend fun flowWithIntResult(name: String, input: ReceiveChannel<String>): Int {
  loop@ while (true) {
    print("Enter int result ($name) >: ")
    val action = input.receive()
    val result = action.toIntOrNull()
    if (result == null) {
      println("NaN")
    } else {
      return result
    }
  }
}
