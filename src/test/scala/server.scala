import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

import scala.concurrent._
import ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.language.postfixOps

import org.scalatest._
import net.pushl.elrpc._

class ServerTest extends FlatSpec with Matchers {
  val system = ActorSystem("elrpc")


  val handler = new DefaultHandler {
    override def methodsMap = Map(
      'echo -> ((uid: Long, args: SList) => {
                  List(Future(Return(uid, args.value.head)))
                }),
      'add  -> ((uid: Long, args: SList) => {
                  val vs = args.value.map({ case SInteger(v) => v
                                            case _           => 0 })
                  List(Future(Return(uid, SInteger(vs.sum))))
                })
    )
  }
  val server = system.actorOf(
    Props(classOf[Server], 36475, handler), "belrpc-server")

  Await.ready(system.whenTerminated, 1 minutes)
}
