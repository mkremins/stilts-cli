(ns stilts-cli.repl
  (:require [stilts.repl :as repl]))

(def rl (js/require "readline"))

(def repl-state (atom repl/init-state))

(defn eval-print! [interface line]
  (let [new-state (swap! repl-state repl/push-line line)]
    (cond (contains? new-state :result) (prn (:result new-state))
          (contains? new-state :error) (println (.-message (:error new-state))))
    (doto interface
      (.setPrompt (repl/prompt new-state))
      (.prompt))))

(defn start! []
  (let [interface (.createInterface rl 
                    #js {:input (.-stdin js/process)
                         :output (.-stdout js/process)})]
    (doto interface
      (.setPrompt (repl/prompt @repl-state))
      (.prompt)
      (.on "line" (partial eval-print! interface)))))
