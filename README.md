# Comb

Comb is a simple templating system for Clojure. You can use Comb to embed
fragments of Clojure code into a text file.

The `<% %>` tags are used to embed a section of Clojure code with side-effects.
This is commonly used for control structures like loops or conditionals.

For example:

    (template/eval "<% (dotimes [x 3] %>foo<% ) %>")
    => "foofoofoo"

The `<%= %>` tags will be subsituted for the value of the expression within them.
This is used for inserting values into a template.

For example:

    (template/eval "Hello <%= name %>" {:name "Alice"})
    => "Hello Alice"


