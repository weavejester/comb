(ns comb.template
  "Clojure templating library."
  (:refer-clojure :exclude [fn eval]))

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
  (lazy-seq
   (let [[_ before expr after] (re-matches parser-regex src)]
     (if expr
       (list* before
              (read-string expr) 
              (parse-string after))
       (list after)))))

(defmacro fn
  "Compile a template into a function that takes the supplied arguments. The
  template source of the template may be a string, or an IO source such as a
  File, Reader or InputStream."
  [args source]
  `(clojure.core/fn ~args
     (str ~@(parse-string (read-source source)))))
