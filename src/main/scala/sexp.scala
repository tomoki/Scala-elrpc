package net.pushl.elrpc

import scala.util.parsing.combinator.JavaTokenParsers

sealed trait SExp
case class SSymbol (value: Symbol)          extends SExp
case class SString (value: String)          extends SExp
case class SInteger(value: Long)            extends SExp
case class SFloat  (value: Double)          extends SExp
case class SList   (value: List[SExp])      extends SExp
case class SAList  (value: Map[SExp, SExp]) extends SExp

object SExpFunctions {
  object SExpImplicits {
    import scala.language.implicitConversions
    implicit def sSymbolToSymbol(s: SSymbol) : Symbol =
      s.value
    implicit def sStringToString(s: SString) : String =
      s.value
    implicit def sIntegerToLong(s: SInteger) : Long =
      s.value
    implicit def sFloatToFloat(s: SFloat) : Double =
      s.value
    implicit def sListToList(s: SList) : List[SExp] =
      s.value
    implicit def sAListToMap(s: SAList) : Map[SExp, SExp] =
      s.value

    implicit def symbolToSSymbol(s: Symbol) : SSymbol =
      SSymbol(s)
    implicit def sStringToString(s: String) : SString =
      SString(s)
    implicit def longToSInteger(s: Long) : SInteger =
      SInteger(s)
    implicit def doubleToSFloat(s: Double) : SFloat =
      SFloat(s)
    implicit def listToSList(s: List[SExp]) : SList =
      SList(s)
    implicit def mapToSAList(s: Map[SExp, SExp]) : SAList =
      SAList(s)
  }
  def toString(s: SExp) : String = {
    s match {
      case SSymbol(v)        => v.name
      case SString(v)        => "\"%s\"".format(v)
      case SInteger(v)       => v.toString
      case SFloat(v)         => v.toString
      case SList(v)          => "(" + v.map(toString(_)).mkString(" ") + ")"
      case SAList(v)         => "(" + v.map(p => "(%s . %s)".format(toString(p._1),
                                                                    toString(p._2))).mkString(" ") + ")"
    }
  }
  def fromString(s: String) : Option[SExp] = {
    SExpParser(s) match {
      case SExpParser.Success(s, _)   => Some(s)
      case SExpParser.NoSuccess(m, _) => {
        println(m)
        None
      }
    }
  }
  object SExpParser extends JavaTokenParsers {
    def ssymbol  = regex("""[A-Za-z+-/\*]+""".r)  ^^ (s => SSymbol(Symbol(s)))
    def sstring  = stringLiteral                  ^^ (s => SString(s.slice(1, s.length-1)))
    def sdouble  = floatingPointNumber            ^^ (d => try {
                                                        SInteger(d.toLong)
                                                      } catch {
                                                        case _: NumberFormatException => SFloat(d.toDouble)
                                                      })
    def slist    = "(" ~> rep(all) <~ ")"         ^^ (l => SList(l))
    def salist   = "(" ~> rep(conspair) <~ ")"    ^^ (l => SAList(l.foldLeft(Map.empty[SExp, SExp])
                                                                     ((l,r) => (l + (r._1 -> r._2)))))
    def conspair : Parser[Tuple2[SExp, SExp]] = "(" ~ all ~ "." ~ all ~ ")" ^^ {
      case _ ~ a ~ _ ~ b ~ _ => (a, b)
    }
    def all: Parser[SExp] =  ssymbol | sstring | sdouble | salist | slist
    def apply(s: String) = parseAll(all, s)
  }
}
