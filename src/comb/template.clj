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

(defn emit-string [s]
  (print "(print " (pr-str s) ")"))

(defn emit-expr [expr]
  (if (.startsWith expr "=")
    (print "(print " (subs expr 1) ")")
    (print expr)))

(defn- parse-string [src]
  (with-out-str
    (print "(do ")
    (loop [src src]
      (let [[_ before expr after] (re-matches parser-regex src)]
        (if expr
          (do (emit-string before)
              (emit-expr expr)
              (recur after))
          (do (emit-string after)
              (print ")")))))))

(defn compile-fn [args src]
  (core/eval
   `(core/fn ~args
      (with-out-str
        ~(-> src read-source parse-string read-string)))))

(defmacro fn
  "Compile a template into a function that takes the supplied arguments. The
  template source may be a string, or an I/O source such as a File, Reader or
  InputStream."
  [args source]
  `(compile-fn '~args ~source))

(defn eval
  "Evaluate a template using the supplied bindings. The template source may
  be a string, or an I/O source such as a File, Reader or InputStream."
  ([source]
     (eval source {}))
  ([source bindings]
     (let [keys (apply vector (map (comp symbol name) (keys bindings)))
           func (compile-fn [{:keys keys}] source)]
       (func bindings))))
