(ns cashflow_api.auth-test
  (:use clojure.test
        cashflow_api.test-core)
  (:require [cashflow_api.auth :as auth]
            [cashflow_api.models.users :as u]
            [korma.core :as sql]))

(use-fixtures :each with-rollback)

(deftest authenticating-users
  (let [user (u/create {:name "Test" :email "user@example.com" :password "s3cr3t"})]

    (testing "Authenticates with valid token"
      (let [token (auth/make-token! (:id user))]
        (is (= user (auth/authenticate-token {} token)))))

    (testing "Does not authenticate with nonexistent token"
      (is (nil? (auth/authenticate-token {} "youhavetobekiddingme"))))

    (testing "Does not authenticate with expired token"
      (let [token (auth/make-token! (:id user))
            sql (str "UPDATE auth_tokens "
                     "SET created_at = NOW() - interval '7 hours' "
                     "WHERE id = ?")]
        (sql/exec-raw [sql [token]])
        (is (nil? (auth/authenticate-token {} token)))))))