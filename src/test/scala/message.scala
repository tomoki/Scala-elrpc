import net.pushl.elrpc._
import org.scalatest._

class Message extends FlatSpec with Matchers {
  import SExpFunctions.SExpImplicits._
  """toString(Call(12341, 'aaa, (1)))""" should """ -> (call . (12341 aaa (1)))""" in {
    MessageFunctions.toString(Call(12341, 'aaa, List(SInteger(1)))) should be ("""(call 12341 aaa (1))""")
  }
  "fromString(Call('uid', 'aaa, (1)))"   should """ -> (call 12341 aaa (1))""" in {
    MessageFunctions.fromString("""(call 12341 aaa (1))""").get should be (Call(12341, 'aaa, List(SInteger(1))))
  }

  """toString(Return(12341, 1))"""       should """ -> (return 12341 1)""" in {
    MessageFunctions.toString(Return(12341, SInteger(1))) should be ("""(return 12341 1)""")
  }
  """fromString(Return(12341, 1))"""     should """ -> (return 12341 1)""" in {
    MessageFunctions.fromString("""(return 12341 1)""").get should be (Return(12341, SInteger(1)))
  }

  """toString(ReturnError(12341, "msg"))"""       should """ -> (return-error 12341 "msg")""" in {
    MessageFunctions.toString(ReturnError(12341, SString("msg"))) should be ("""(return-error 12341 "msg")""")
  }
  """fromString(ReturnError(12341, "msg"))"""     should """ -> (return-error 12341 "msg")""" in {
    MessageFunctions.fromString("""(return-error 12341 "msg")""").get should be (ReturnError(12341, SString("msg")))
  }

  """toString(EpcError(12341, "msg"))"""       should """ -> (return-error 12341 "msg"))""" in {
    MessageFunctions.toString(EpcError(12341, SString("msg"))) should be ("""(epc-error 12341 "msg")""")
  }
  """fromString(EpcError(12341, "msg"))"""       should """ -> (epc-error 12341 "msg")""" in {
    MessageFunctions.fromString("""(epc-error 12341 "msg")""").get should be (EpcError(12341, SString("msg")))
  }
  """toString(Methods(12341))"""       should """ -> (methods 12341)""" in {
    MessageFunctions.toString(Methods(12341)) should be ("""(methods 12341)""")
  }
  """fromString(Methods(12341, "msg"))"""       should """ -> (methods 12341 "msg")""" in {
    MessageFunctions.fromString("""(methods 12341)""").get should be (Methods(12341))
  }
}

