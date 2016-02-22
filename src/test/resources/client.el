(require 'epc)

;; (setq epc (epc:start-epc "perl" '("echo-server.pl")))
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
