# Comb [![Build Status](https://github.com/weavejester/comb/actions/workflows/test.yml/badge.svg)](https://github.com/weavejester/comb/actions/workflows/test.yml)

Comb is a simple templating system for Clojure. You can use Comb to embed
fragments of Clojure code into a text file.

## Installation

Add the following dependency to your deps.edn file:

    comb/comb {:mvn/version "0.1.1"}

Or to your Leiningen project file:

    [comb "0.1.1"]

## Syntax

The `<% %>` tags are used to embed a section of Clojure code with side-effects.
This is commonly used for control structures like loops or conditionals.

For example:

```clojure
(require '[comb.template :as template])
    
(template/eval "<% (dotimes [x 3] %>foo<% ) %>")
=> "foofoofoo"
```

The `<%= %>` tags will be subsituted for the value of the expression within them.
This is used for inserting values into a template.

For example:

```clojure
(template/eval "Hello <%= name %>" {:name "Alice"})
=> "Hello Alice"
```

## API Documentation

### template/eval

```clojure
(template/eval source)
(template/eval source bindings)
```

Evaluate a template source using an optional map of bindings. The template
source can be a string, or any I/O source understood by the standard `slurp`
function.

Example of use:

```clojure
(template/eval "Hello <%= name %>" {:name "Bob"})
```

### template/fn

```clojure
(template/fn args source)
```

Compile a template source into a anonymous function. This is a lot faster
than `template/eval` for repeated calls, as the template source is only
parsed when the function is created.

Example of use:

```clojure
(def hello
  (template/fn [name] "Hello <%= name %>"))

(hello "Alice")
```

## License

Copyright © 2025 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
