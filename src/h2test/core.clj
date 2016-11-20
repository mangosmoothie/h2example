(ns h2test.core
  (:require [clojure.java.jdbc :as j]
            [ragtime.jdbc :as ragtime]
            [ragtime.repl :as ragrepl])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(def db-spec
  {
   :classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (str (System/getProperty "user.dir") "/" "example")
   })

(defn pool [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setMaxIdleTimeExcessConnections (* 30 60))
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)

(defn load-config []
  {:datastore  (ragtime/sql-database
                (str "jdbc:h2:file:" (System/getProperty "user.dir") "/" "example"))
   :migrations (ragtime/load-directory "migrations")})

(defn migrate []
  (ragrepl/migrate (load-config)))

(defn rollback []
  (ragrepl/rollback (load-config)))

(defn write-all [n1 n2]
  (let [id1 (java.util.UUID/randomUUID)
        id2 (java.util.UUID/randomUUID)]
    (j/with-db-connection [t-con (db-connection)]
      (j/insert! t-con :tb1 {:uuid id1 :name n1})
      (j/insert! t-con :tb1 {:uuid id2 :name n2})
      (j/insert! t-con :tb1_tb1 {:tb1_a id1 :tb1_b id2}))))
