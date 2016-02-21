package net.pushl.elrpc

sealed trait Message
case class Call       (UID: String, methodName:   SSymbol, args: SList) extends Message
case class Return     (UID: String, returnValue:  SExp)                 extends Message
case class ReturnError(UID: String, errorMessage: SExp)                 extends Message
case class EpcError   (UID: String, errorMessage: SExp)                 extends Message
case class Methods    (UID: String)                                     extends Message

object MessageFunctions {
  val CALL         = "call"
  val RETURN       = "return"
  val RETURN_ERROR = "return-error"
  val EPC_ERROR    = "epc-error"
  val METHODS      = "methods"

  def toString(m: Message) : String = {
    val (t, encoded) = m match {
      case Call(uid, methodname, args) =>
        (CALL, SList(List(SString(uid), methodname, args)))
      case Return(uid, returnValue) =>
        (RETURN, SList(List(SString(uid), returnValue)))
      case ReturnError(uid, returnMessage) =>
        (RETURN_ERROR, SList(List(SString(uid), returnMessage)))
      case EpcError(uid, errorMessage) =>
        (EPC_ERROR, SList(List(SString(uid), errorMessage)))
      case Methods(uid) =>
        (METHODS, SList(List(SString(uid))))
    }
    // FIXME: adhoc stringify.
    "(%s . %s)".format(t, SExpFunctions.toString(encoded))
  }

  def fromString(s: String) : Option[Message] = {
    // s = (call . (1 2 3))
    // ex. getMethodBody(CALL) -> "(1 2 3)"
    def getMethodBody(h: String) = {
      s.slice(h.length + 4, s.length-1)
    }
    def isValidMethodMessage(h: String) =
      s.startsWith("(%s . ".format(h)) && s.endsWith(")")

    val converters : List[Tuple2[String, Option[SExp] => Option[Message]]]= List(
      (CALL, {
         case Some(SList(List(uid: SString, method: SSymbol, args: SList))) => Some(Call(uid.value, method, args))
         case _ => None
       }),
      (RETURN, {
         case Some(SList(List(uid: SString, returnValue: SExp))) => Some(Return(uid.value, returnValue))
         case _ => None
       }),
      (RETURN_ERROR, {
         case Some(SList(List(uid: SString, errorMessage: SExp))) => Some(ReturnError(uid.value, errorMessage))
         case _ => None
       }),
      (EPC_ERROR, {
         case Some(SList(List(uid: SString, errorMessage: SExp))) => Some(EpcError(uid.value, errorMessage))
         case _ => None
       }),
      (METHODS, {
         case Some(SList(List(uid: SString))) => Some(Methods(uid.value))
         case _ => None
       })
      )
    val converted : List[Option[Message]] = converters.map(
      {case (method, conv) => {
         if(isValidMethodMessage(method))
           conv(SExpFunctions.fromString(getMethodBody(method)))
         else
           None
       }})
    converted.find(_.isDefined) match {
      case Some(m) => m
      case None    => None
    }
  }
}
