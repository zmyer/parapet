package io.parapet.components.network

import cats.effect.IO
import io.parapet.CatsApp
import io.parapet.components.network.ZmqTcpServer
import io.parapet.components.network.ZmqTcpServer._
import io.parapet.core.Process
import io.parapet.implicits._
import io.parapet.instances.DslInstances.catsInstances.effect._
import io.parapet.instances.DslInstances.catsInstances.flow._

object TcpServerApp extends CatsApp {

  val port = 5555

  class EchoProcess extends Process[IO] {

    override val handle: Receive = {
      case Req(data) =>
        eval(println(s"echo process received: " + new String(data))) ++
          reply(sender => Rep(data) ~> sender)
      case _ => empty
    }
  }

  val echoProcess = new EchoProcess
  val zmqTcpServer: Process[IO] = ZmqTcpServer[IO](port, echoProcess.self)

  override val processes: Array[Process[IO]] = Array(echoProcess, zmqTcpServer)
}
