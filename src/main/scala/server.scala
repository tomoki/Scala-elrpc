package net.pushl.elrpc

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

import scala.concurrent._
import ExecutionContext.Implicits.global

import Tcp._

object ServerHander {
  def props(conn: ActorRef, handler: HandlerTrait) : Props =
    Props(new ServerHandler(conn,handler))
}

class ServerHandler(conn: ActorRef, handler: HandlerTrait) extends Actor {
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
                       conn ! Write(tosend)
                     }
                   })
    }
    case "close" =>
      conn ! Close
    case _: ConnectionClosed =>
      Console.err.println("connection closed")
      context stop self
  }
}

class Server(port: Int, handler: HandlerTrait) extends Actor {
  import context.system
  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", port))
  def printPort() : Unit = Console.out.println(port)


  def receive = {
    case Bound(localaddress) => {
      Console.err.println(localaddress)
      Console.out.println(localaddress.getPort)
    }
    case CommandFailed(_: Bind) => {
      Console.err.println("failed")
    }
    case Connected(remote, local) => {
      Console.err.println("connect")
      sender() ! Register(context.actorOf(ServerHander.props(sender(), handler)))
    }
  }
}
