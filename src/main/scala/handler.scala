package net.pushl.elrpc

import scala.concurrent._
import ExecutionContext.Implicits.global

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

trait HandlerTrait {
  def handleMessage(m: Message) =
    m match {
      case a@Call(_, _, _)      => handleCall(a)
      case a@Return(_, _)       => handleReturn(a)
      case a@ReturnError(_, _)  => handleReturnError(a)
      case a@EpcError(_, _)     => handleEpcError(a)
      case a@Methods(_)         => handleMethods(a)
    }

  def handleCall(m: Call)               : List[Future[Message]]
  def handleReturn(m: Return)           : List[Future[Message]]
  def handleReturnError(m: ReturnError) : List[Future[Message]]
  def handleEpcError(m: EpcError)       : List[Future[Message]]
  def handleMethods(m: Methods)         : List[Future[Message]]
}

class DefaultHandler extends HandlerTrait {
  import SExpFunctions.SExpImplicits._
  def methodsMap : Map[Symbol, (Long, SList) => List[Future[Message]]] =
    Map.empty[Symbol, (Long, SList) => List[Future[Message]]]

  override def handleCall(m: Call) : List[Future[Message]] = {
    methodsMap.get(m.methodName) match {
      case Some(func) =>   func(m.UID, m.args)
      case None =>         List(Future(ReturnError(m.UID, "method not found")))
    }
  }
  override def handleReturn(m: Return) = {
    Console.err.println(m)
    List()
  }
  override def handleReturnError(m: ReturnError) = {
    Console.err.println(m)
    List()
  }
  override def handleEpcError(m: EpcError) = {
    Console.err.println(m)
    List()
  }
  override def handleMethods(m: Methods) : List[Future[Message]] = {
    // TODO: implement specs.
    val specs = SList(methodsMap.map({case (name, func) =>  SList(List(SSymbol(name), List(), ""))}).toList)
    List(Future(Return(m.UID, specs)))
  }
}
