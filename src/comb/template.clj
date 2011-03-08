(ns comb.template
  "Clojure templating library."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]))

(defn- read-source [source]
  (if (string? source)
    source
    (slurp source)))

(def delimiters ["<%" "%>"])

(def parser-regex
  (re-pattern
   (str "(?s)\\A"
        "(?:" "(.*?)"
        (first delimiters) "(.*?)" (last delimiters)
        ")?"
        "(.*)\\z")))

(defn- parse-string [src]
  (with-out-str
    (print "(str ")
    (loop [src src]
      (let [[_ before expr after] (re-matches parser-regex src)]
        (if expr
          (do (pr before)
              (print " " expr " ")
              (recur after))
          (do (prn after)
              (print ")")))))))

(defn compile-fn [args src]
  (core/eval
   `(core/fn ~args
     ~(-> src read-source parse-string read-string))))

(defmacro fn
  "Compile a template into a function that takes the supplied arguments. The
  template source may be a string, or an I/O source such as a File, Reader or
  InputStream."
  [args source]
  `(compile-fn '~args ~source))

(defn eval
  "Evaluate a template using the supplied bindings. The template source may
  be a string, or an I/O source such as a File, Reader or InputStream."
  [source bindings]
  (let [keys (map (comp symbol name) (keys bindings))
        func (compile-fn [{:keys keys}] source)]
    (func bindings)))
