(ns cashflow_api.entities
  (:use korma.core
        cashflow_api.db))

(declare users)

(defentity users
  (pk :id)
  (table :users)
  (entity-fields :email))

(defentity auth-tokens
  (pk :id)
  (table :auth_tokens)
  (belongs-to users {:fk :user_id}))

