import net.pushl.elrpc._
import org.scalatest._

class Message extends FlatSpec with Matchers {
  import SExpFunctions.SExpImplicits._
  """toString(Call("uid", 'aaa, (1)))""" should """ -> (call . ("uid" aaa (1)))""" in {
    MessageFunctions.toString(Call("uid", 'aaa, List(SInteger(1)))) should be ("""(call . ("uid" aaa (1)))""")
  }
  "fromString(Call('uid', 'aaa, (1)))"   should """ -> (call . ("uid" aaa (1)))""" in {
    MessageFunctions.fromString("""(call . ("uid" aaa (1)))""").get should be (Call("uid", 'aaa, List(SInteger(1))))
  }

  """toString(Return("uid", 1))"""       should """ -> (return . ("uid" 1))""" in {
    MessageFunctions.toString(Return("uid", SInteger(1))) should be ("""(return . ("uid" 1))""")
  }
  """fromString(Return("uid", 1))"""     should """ -> (return . ("uid" 1))""" in {
    MessageFunctions.fromString("""(return . ("uid" 1))""").get should be (Return("uid", SInteger(1)))
  }

  """toString(ReturnError("uid", "msg"))"""       should """ -> (return-error . ("uid" "msg"))""" in {
    MessageFunctions.toString(ReturnError("uid", SString("msg"))) should be ("""(return-error . ("uid" "msg"))""")
  }
  """fromString(ReturnError("uid", "msg"))"""     should """ -> (return-error . ("uid" "msg"))""" in {
    MessageFunctions.fromString("""(return-error . ("uid" "msg"))""").get should be (ReturnError("uid", SString("msg")))
  }

  """toString(EpcError("uid", "msg"))"""       should """ -> (return-error . ("uid" "msg"))""" in {
    MessageFunctions.toString(EpcError("uid", SString("msg"))) should be ("""(epc-error . ("uid" "msg"))""")
  }
  """fromString(EpcError("uid", "msg"))"""       should """ -> (epc-error . ("uid" "msg"))""" in {
    MessageFunctions.fromString("""(epc-error . ("uid" "msg"))""").get should be (EpcError("uid", SString("msg")))
  }
  """toString(Methods("uid"))"""       should """ -> (methods . ("uid"))""" in {
    MessageFunctions.toString(Methods("uid")) should be ("""(methods . ("uid"))""")
  }
  """fromString(Methods("uid", "msg"))"""       should """ -> (methods . ("uid" "msg"))""" in {
    MessageFunctions.fromString("""(methods . ("uid"))""").get should be (Methods("uid"))
  }
}

