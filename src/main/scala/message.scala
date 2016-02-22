package net.pushl.elrpc

sealed trait Message
case class Call       (UID: Long, methodName:   SSymbol, args: SList) extends Message
case class Return     (UID: Long, returnValue:  SExp)                 extends Message
case class ReturnError(UID: Long, errorMessage: SExp)                 extends Message
case class EpcError   (UID: Long, errorMessage: SExp)                 extends Message
case class Methods    (UID: Long)                                     extends Message

object MessageFunctions {
  val CALL         = "call"
  val RETURN       = "return"
  val RETURN_ERROR = "return-error"
  val EPC_ERROR    = "epc-error"
  val METHODS      = "methods"

  def toString(m: Message) : String = {
    val encoded = m match {
      case Call(uid, methodname, args) =>
        SList(List(SSymbol(Symbol(CALL)), SInteger(uid), methodname, args))
      case Return(uid, returnValue) =>
        SList(List(SSymbol(Symbol(RETURN)), SInteger(uid), returnValue))
      case ReturnError(uid, returnMessage) =>
        SList(List(SSymbol(Symbol(RETURN_ERROR)), SInteger(uid), returnMessage))
      case EpcError(uid, errorMessage) =>
        SList(List(SSymbol(Symbol(EPC_ERROR)), SInteger(uid), errorMessage))
      case Methods(uid) =>
        SList(List(SSymbol(Symbol(METHODS)), SInteger(uid)))
    }
    SExpFunctions.toString(encoded)
  }

  def fromString(s: String) : Option[Message] = {
     val converters : List[Tuple2[String, SExp => Option[Message]]]= List(
      (CALL, {
         case SList(List(uid: SInteger, method: SSymbol, args: SList)) => Some(Call(uid.value, method, args))
         case _ => None
       }),
      (RETURN, {
         case SList(List(uid: SInteger, returnValue: SExp)) => Some(Return(uid.value, returnValue))
         case _ => None
       }),
      (RETURN_ERROR, {
         case SList(List(uid: SInteger, errorMessage: SExp)) => Some(ReturnError(uid.value, errorMessage))
         case _ => None
       }),
      (EPC_ERROR, {
         case SList(List(uid: SInteger, errorMessage: SExp)) => Some(EpcError(uid.value, errorMessage))
         case _ => None
       }),
      (METHODS, {
         case SList(List(uid: SInteger)) => Some(Methods(uid.value))
         case _ => None
       })
     )
    val messages = (SExpFunctions.fromString(s) match {
                      case Some(SList(SSymbol(t) :: c)) =>
                        converters.map({ case (method, conv) if method == t.name => conv(SList(c))
                                         case _                                  => None })
                      case None => List()
                    })
    messages.find(_.isDefined) match {
      case Some(m) => m
      case None    => None
    }
  }
}
