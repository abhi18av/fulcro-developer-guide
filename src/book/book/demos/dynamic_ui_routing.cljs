(ns book.demos.dynamic-ui-routing
  (:require
    [com.fulcrologic.fulcro.routing.legacy-ui-routers :as r]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [cljs.loader :as loader]
    [taoensso.timbre :as log]))

(defsc Login [this {:keys [label login-prop]}]
  {:initial-state (fn [params] {r/dynamic-route-key :login :label "LOGIN" :login-prop "login data"})
   :ident         (fn [] [:login :singleton])
   :query         [r/dynamic-route-key :label :login-prop]}
  (dom/div {:style {:backgroundColor "green"}}
    (str label " " login-prop)))

(defsc NewUser [this {:keys [label new-user-prop]}]
  {:initial-state (fn [params] {r/dynamic-route-key :new-user :label "New User" :new-user-prop "new user data"})
   :ident         (fn [] [:new-user :singleton])
   :query         [r/dynamic-route-key :label :new-user-prop]}
  (dom/div {:style {:backgroundColor "skyblue"}}
    (str label " " new-user-prop)))

(defsc Root [this {:keys [top-router :com.fulcrologic.fulcro.routing.legacy-ui-routers/pending-route]}]
  {:initial-state (fn [params] (merge
                                 (r/routing-tree
                                   (r/make-route :ui-main [(r/router-instruction :top-router [:ui-main :singleton])])
                                   (r/make-route :login [(r/router-instruction :top-router [:login :singleton])])
                                   (r/make-route :new-user [(r/router-instruction :top-router [:new-user :singleton])]))
                                 {:top-router (comp/get-initial-state r/DynamicRouter {:id :top-router})}))
   :query         [:ui/react-key {:top-router (r/get-dynamic-router-query :top-router)}
                   :com.fulcrologic.fulcro.routing.legacy-ui-routers/pending-route
                   r/routing-tree-key]}
  (dom/div nil
    ; Sample nav mutations
    (dom/a {:onClick #(comp/transact! this `[(r/route-to {:handler :ui-main})])} "Main") " | "
    (dom/a {:onClick #(comp/transact! this `[(r/route-to {:handler :new-user})])} "New User") " | "
    (dom/a {:onClick #(comp/transact! this `[(r/route-to {:handler :login})])} "Login") " | "
    (dom/div (if pending-route "Loading" "Done"))
    (r/ui-dynamic-router top-router)))

; Use this as started-callback. These would happen as a result of module loads:
(defn application-loaded [app]
  ; Let the dynamic router know that two of the routes are already loaded.
  (comp/transact! app `[(r/install-route {:target-kw :new-user :component ~NewUser})
                        (r/install-route {:target-kw :login :component ~Login})
                        (r/route-to {:handler :login})]))

