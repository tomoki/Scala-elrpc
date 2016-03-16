# Scala elrpc

This is a Scala implementation of [EPC:The Emacs RPC](https://github.com/kiwanami/emacs-epc).
Currently, this is very unstable, and we need your help!

## Install
Adds following lines to your `build.sbt`.

```scala
val elrpc = uri("git://github.com/tomoki/Scala-elrpc.git")
lazy val root = project.in(file(".")).dependsOn(elrpc)
```

## Usage
In Scala, start new server.

```scala
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
val server = system.actorOf(Props(classOf[Server], 36475, handler), "belrpc-server")
Await.ready(system.whenTerminated, 10 minutes)
```

Next, connect to Scala server from Emacs.

```emacs
;; you need to install epc.
(require 'epc)

(setq epc (epc:start-epc-debug 36475))

(deferred:$
  (epc:call-deferred epc 'echo '(10))
  (deferred:nextc it
    (lambda (x) (message "Return : %S" x))))

(deferred:$
  (epc:call-deferred epc 'add '(10 40))
  (deferred:nextc it
    (lambda (x) (message "Return : %S" x))))

;; calling synchronously
(message "%S" (epc:call-sync epc 'echo '(10)))

;; Request peer's methods
(message "%S" (epc:sync epc (epc:query-methods-deferred epc)))

(epc:stop-epc epc)
```

