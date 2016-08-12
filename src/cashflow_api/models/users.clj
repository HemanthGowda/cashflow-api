(ns cashflow_api.models.users
    (:use korma.core)
    (:require
        [cashflow_api.entities :as e]
        [buddy.hashers :as hashers]
        [clojure.set :refer [map-invert]]))

(derive ::admin ::user)

(defn find-all []
      (select e/users))

(defn find-by [field value]
      (some-> (select* e/users)
              (where {field value})
              (limit 1)
              select first (dissoc :password_digest)))

(defn find-by-id [id]
      (find-by :id id))

(defn find-by-email [email]
      (find-by :email email))

(defn create [user]
      (-> (insert* e/users)
          (values
              (-> user
                  (assoc :password_digest (hashers/encrypt (:password user)))
                  (dissoc :password)))
          insert (dissoc :password_digest)))

(defn update-user [user]
      (update e/users
              (set-fields
                  (-> user
                      (dissoc :id :password)))
              (where {:id (user :id)})))

(defn count-users []
      (let [agg (select e/users
                        (aggregate (count :*) :cnt))]
           (get-in agg [0 :cnt] 0)))

(defn delete-user [user]
      (delete e/users
              (where {:id (user :id)})))

(defn password-matches?
      "Check to see if the password given matches the digest of the user's saved password"
      [id password]
      (some-> (select* e/users)
              (fields :password_digest)
              (where {:id id})
              select first :password_digest
              (->> (hashers/check password))))