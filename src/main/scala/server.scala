package net.pushl.elrpc

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

import scala.concurrent._
import ExecutionContext.Implicits.global

class Server(port: Int, handler: HandlerTrait) extends Actor {
  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", port))
  def printPort() : Unit = Console.out.println(port)
  def parseMessages(data: String) : List[net.pushl.elrpc.Message] = {
    if(data.length == 0)
      Nil
    else {
      val length  = Integer.parseInt(data.take(6), 16)
      val (content, rest) = data.drop(6).splitAt(length)
      MessageFunctions.fromString(content.trim) match {
        case Some(message) =>
          message :: (parseMessages(rest))
        case None          => {
          Console.err.println("failed to decode: " + content.trim)
          parseMessages(rest)
        }
      }
    }
  }
  def encodeString(m: String) : ByteString = {
    val s = ("%06x".format(m.length)) + m
    ByteString(s, "utf-8")
  }
  def receive = {
    case Bound(localaddress) => {
      Console.err.println(localaddress)
    }
    case CommandFailed(_: Bind) => {
      Console.err.println("failed")
    }
    case Connected(remote, local) => {
      Console.err.println("connect")
      val connection = sender()
      connection ! Register(self)
      context become {
        // case data: ByteString =>
        //   connection ! Write(data)
        case CommandFailed(w: Write) =>
          Console.err.println("write failed")
        case Received(data) => {
          val messages = parseMessages(data.decodeString("utf-8"))
          Console.err.println(messages)
          val rets     = messages.flatMap(handler.handleMessage(_))
          rets.foreach(_.onSuccess {
                         case m => {

                           val tosend = encodeString(MessageFunctions.toString(m))
                           Console.err.println(tosend.decodeString("utf-8"))
                           connection ! Write(tosend)
                         }
                       })
        }
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          Console.err.println("connection closed")
          context stop self
      }
    }
  }
}
