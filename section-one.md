# Section One: the basics
qualified keywords
(cljc bug) - aliased qualified keywords are expanded at read-time so if you
have a qualified keyword that is clj or cljs only referring to a spec, compiler
can complain

## Using spec
Spec is less about types and more about describing data and so specs revolve
around *predicates*

`s/valid?` is used to check if something satisfies a spec predicate.
```clj
(s/valid? number? 1) ;; => true
```

### Exercises
- define a function `at-least-one?` and another `at-most-six?` and use
  `s/valid?` to test some inputs
```clj
(def at-least-one? #(>= % 1))
(def at-most-six? (partial >= 6))
(s/valid? at-least-one? 5) ;;=> true
(s/valid? at-most-six? 3) ;;=> true

(def between-one-and-six?
  #(and (at-least-one? %)
        (at-most-six? %)))

(s/valid? between-one-and-six? 0) ;; => false
(s/valid? between-one-and-six? 7) ;; => false
(s/valid? between-one-and-six? 2) ;; => true
```

Anything that behaves like a one-arg function can behave like a spec - it
simply needs to return a truthy value:

```clj
(s/valid? {:a 1} :a) ;;=> true
(s/valid? {:a false} :a) ;;=> false
(s/valid? {:a nil} :a) ;;=> false
(s/valid? (fn [k _] true) :a) ;;=> ArityException Wrong number of args (1) ...
```

## Naming your specs
`s/def` is used to give a name to your spec. `s/def` takes a name and a
predicate / spec:
```clj

(s/def ::non-negative-number #(>= % 0))
```
(names are usually qualified keywords - unqualified keywords are not accepted, but symbols)

You can reuse specs:
```clj
(s/def ::inventory ::non-negative-number)
(s/def ::price ::non-negative-number)
(s/def ::average-rating ::non-negative-number)
(s/def ::number-of-pages ::non-negative-number)
```

## Composing specs
Spec provides facilities for composing your specs:
### `s/and`
```clj
(s/def ::die-roll (s/and integer?
                         ::non-negative-number ;; this is superfluous...
                         between-one-and-six?))
```

### `s/and` vs `and`:
```clj
(s/explain ::die-roll "1")
;; => val: "1" fails spec: :spec-basics/die-roll predicate: number?
(s/explain ::die-roll -1)
;; => val: -1 fails spec: :spec-basics/non-negative-number predicate: (>= % 0)
(s/explain ::die-roll 7)
;; => val: 7 fails spec: :spec-basics/die-roll predicate: between-one-and-six?
(s/def ::die-roll-2
  #(and (number? %)
        (s/valid? ::non-negative-number %) ;; keywords by themselves are not specs
        (between-one-and-six? %)))
(s/explain ::die-roll-2 7)
;; => val: 7 fails spec: :spec-basics/die-roll-2 predicate: (and (number? %) (valid? :spec-basics/non-negative-number %) (between-one-and-six? %))
```

### `s/or`
`s/or` expects a name followed by a spec/predicate
```clj
(s/def ::clj-index-of-return-value
  (s/or :found ::non-negative-number
        :not-found nil?))

(s/def ::java.indexOf-return-value
  (s/or :found ::non-negative-number
        :not-found #{-1}))
```

### Exercises
- a spec for a number that is divisible by three
- a spec for that a value can only be the name of the chess pieces
- single words that are longer than 5 letters
- a spec for UK postcodes
