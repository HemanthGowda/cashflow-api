(ns cashflow_api.db
  (:use korma.db)
  (:require [environ.core :refer [env]]))

(defdb db (postgres {:db (get env :cashflow-db "cashflow_clojure")
                     :user (get env :cashflow-db-user "cashflow_clojure")
                     :password (get env :cashflow-db-pass "")
                     :host (get env :cashflow-db-host "localhost")
                     :port (get env :cashflow-db-port 5432)}))
