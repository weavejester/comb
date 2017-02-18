(ns comb.template
  "Clojure templating library."
  (:refer-clojure :exclude [fn eval])
  (:require [clojure.core :as core]
            [clojure.string :as s]))

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

(defn emit-string
  ([s]
     (print "(print " (pr-str s) ")"))
  ([s emit-next-line-break?]
     (if emit-next-line-break?
       (emit-string s)
       (emit-string (s/replace-first s #"\n" "")))))

(defn emit-expr [expr]
  (let [emit-result? (.startsWith expr "=")
        emit-next-line-break? (not (.endsWith expr "-"))
        substring-start (if emit-result? 1 0)
        substring-end (if emit-next-line-break? (count expr) (- (count expr) 1))
        final-expr (subs expr substring-start substring-end)]
    (if emit-result?
      (print "(print " final-expr ")")
      (print final-expr))
    emit-next-line-break?))

(defn parse-string [src]
  (with-out-str
    (print "(do ")
    (loop [src src emit-next-line-break? true]
      (let [[_ before expr after] (re-matches parser-regex src)]
        (if expr
          (do (emit-string before emit-next-line-break?)
              (recur after (emit-expr expr)))
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
     (let [keys (map (comp symbol name) (keys bindings))
           func (compile-fn [{:keys keys}] source)]
       (func bindings))))
