package net.pushl.elrpc

sealed trait Message
class Call       (UID: String, methodName:   SSymbol, args: SList)
class Return     (UID: String, returnValue:  SExp)
class ReturnError(UID: String, errorMessage: SExp)
class EpcError   (UID: String, errorMessage: SExp)
class Methods    (UID: String)

