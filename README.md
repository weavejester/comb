# Comb

Comb is a simple templating system for Clojure. You can use Comb to embed
fragments of Clojure code into a text file.

## Syntax

The `<% %>` tags are used to embed a section of Clojure code with side-effects.
This is commonly used for control structures like loops or conditionals.

For example:

    (require '[comb.template :as template])
    
    (template/eval "<% (dotimes [x 3] %>foo<% ) %>")
    => "foofoofoo"

The `<%= %>` tags will be subsituted for the value of the expression within them.
This is used for inserting values into a template.

For example:

    (template/eval "Hello <%= name %>" {:name "Alice"})
    => "Hello Alice"

## Installation

To install, add the following dependency to your `project.clj` file:

    [comb "0.1.1"]

## API Documentation

### template/eval

    (template/eval source)
    (template/eval source bindings)

Evaluate a template source using an optional map of bindings. The template
source can be a string, or any I/O source understood by the standard `slurp`
function.

Example of use:

    (template/eval "Hello <%= name %>" {:name "Bob"})

### template/fn

    (template/fn args source)

Compile a template source into a anonymous function. This is a lot faster
than `template/eval` for repeated calls, as the template source is only
parsed when the function is created.

Examples of use:

    (def hello
      (template/fn [name] "Hello <%= name %>"))

    (hello "Alice")

## License

Copyright © 2015 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
