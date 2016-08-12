(ns cashflow_api.handler
    (:use compojure.core
      ring.middleware.json)
    (:import (com.fasterxml.jackson.core JsonGenerator))
    (:require [compojure.handler :as handler]
      [compojure.route :as route]
      [ring.util.response :refer [response]]
      [cheshire.generate :refer [add-encoder]]
      [cashflow_api.models.users :as users]
      [cashflow_api.auth :refer [auth-backend authenticated-user unauthorized-handler make-token!]]
      [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
      [buddy.auth.accessrules :refer [restrict]]))

(add-encoder clojure.lang.Keyword
             (fn [^clojure.lang.Keyword kw ^JsonGenerator gen]
                 (.writeString gen (name kw))))

(defn get-users [_]
      {:status 200
       :body   {:count   (users/count-users)
                :results (users/find-all)}})

(defn create-user [{user :body}]
      (let [new-user (users/create user)]
           {:status  201
            :headers {"Location" (str "/users/" (:id new-user))}}))

(defn find-user [{{:keys [id]} :params}]
      (response (users/find-by-id (read-string id))))

(defn delete-user [{{:keys [id]} :params}]
      (users/delete-user {:id (read-string id)})
      {:status  204
       :headers {"Location" "/users"}})

(defroutes app-routes
           ;; USERS
           (context "/users" []
                    (GET "/" [] get-users)
                    (POST "/" [] create-user)
                    (context "/:id" [id]
                             (GET "/" [] find-user)
                             (DELETE "/" [id] delete-user)))

           (POST "/sessions" {{:keys [user-id password]} :body}
                 (if (users/password-matches? user-id password)
                   {:status 201
                    :body   {:auth-token (make-token! user-id)}}
                   {:status 409
                    :body   {:status  "error"
                             :message "invalid username or password"}}))

           (route/not-found (response {:message "Page not found"})))

(defn wrap-log-request [handler]
      (fn [req]
          (println req)
          (handler req)))

(def app
  (-> app-routes
      (wrap-authentication auth-backend)
      (wrap-authorization auth-backend)
      wrap-json-response
      (wrap-json-body {:keywords? true})))
