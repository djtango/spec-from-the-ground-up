#Section Two
## Next Steps
Spec provides `s/conform` as a means of annotating and destructuring your data:
```clj
(s/def ::number-or-string
  (s/or :number number?
        :string string?))

(defn extract-number [number-or-string]
  (let [conformed (s/conform ::number-or-string number-or-string)]
    (if (= ::s/invalid conformed)
      :not-a-number-or-string!
      (let [[tag value] conformed]
        (case tag
          :number value
          :string (Float/parseFloat value))))))
```
`s/conform` appears in various points...

###Collections
####`s/keys`
The map is the bread and butter of clojure - `s/keys` is about describing map
requirements. Contrary to many other common typing patterns or validation
libraries, spec requires you to describe your entities independently from the
structure of the map attributes.

This is an opinionated design decision and is meant to encourage code reuse of
common project entities and the use of map subsets or supersets.
Usage:

```clj
(s/def ::name string?)
(s/def ::age pos?)
(s/def ::breed #{:corgi :pug :poodle :german-shepherd :golden-retriever :labrador})
(s/def ::dog (s/keys :req [::name ::age ::breed ::colour]))
(s/def ::colour #{:black :brown :white :golden})
(s/valid? ::dog {::name "Fenton" ::age 1 ::breed :labrador ::colour :black})
```

`s/keys` supports specifying optional fields:
```clj

(s/def ::favourite-meal #{:shoes :chicken :homework})
(s/def ::dog (s/keys :req [::name ::age ::breed ::color] :opt [::favourite-meal]))
```
A lot of clojure maps in the wild still use unqualified keywords - `s/keys`

allows you to specify unqualified keywords:
```clj
;;https://api.slack.com/web#responses
(s/def ::ok boolean?)
(s/def ::error string?)
(s/def ::warning string?)
(s/def ::slack-web-api-response (s/keys :req-un [::ok] :opt-un [::error ::warning]))
(s/valid? ::slack-web)
```

`s/keys` will check the value assigned to any key against a registered spec of
the same name where possible.

`s/coll-of` is used to describe homogenous collections:
```clj
(s/valid? (s/coll-of number?) [1 2 3])
(s/valid? (s/coll-of odd?) [1 3 5])
```

`s/coll-of` allows you to supply options to give additional details:
```clj
(s/def ::board-position #{:O :X :empty})
(s/def ::tic-tac-toe-row (s/coll-of ::board-position :count 3))
(s/valid? ::tic-tac-toe-row [:O :X :O]) ;; => true
```

`s/map-of` can be used to specify maps of arbitrary size but predictable k-v
pairings:

```clj
(s/def ::user-id uuid?)
(s/def ::user (s/keys :req [::name ::user-id]))
(s/valid? (s/map-of ::user-id ::user) {(java.util.UUID/randomUUID) {::name "foo" ::user-id (java.util.UUID/randomUUID)}})
```

`s/conform` ;;fizz buzz


### Regex ops
spec provides regex expressions for describing structure in a sequence of data.
These ops borrow from regex:
- `s/*`: 0 or more occurences
- `s/+`: 1 or more occurences
- `s/?`: 0 or 1 occurence
```clj
(s/valid? (s/* string?) ["a" "b"]) ;; => true
(s/valid? (s/* string?) []) ;; => true
(s/valid? (s/+ string?) []) ;; => false
(s/valid? (s/+ string?) ["a"]) ;; => true
(s/valid? (s/? string?) ["a"]) ;; => true
(s/valid? (s/? string?) ["a" "b"]) ;; => false
(s/valid? (s/? string?) []) ;; => true
```

In addition there is `s/tuple`, `s/cat` and `s/alt`
`s/tuple` allows you to specify ordered structure in a sequence:
```clj
(s/def ::datom (s/tuple number? keyword? any? number? boolean?))
```

`s/cat` allows you to give names to the elements:
```clj
(s/def ::reframe-event (s/cat :event-name keyword?
                              :arg any?))
(s/valid? ::reframe-event [:add-todo "write more specs"]) ;;=> true
```
What is special about `s/cat` is that conforming `s/cat` returns a map with the
named positional elements:
```clj
(s/conform ::reframe-event [:add-todo "write more specs"]) ;; => {:event-name :add-todo :arg "write more specs"}
```

### Exercises
- using `s/or` and conform, set up a spec `::fizzbuzz` which will conform any
  input with its fizzbuzz outcome
- in the same namespace define specs for which these are valid:
```clj
(def map-1 {:number/a 1})
(def map-2 {:string/a "1"})
(def map-3 {:number/a 1, :b "2"})
(def map-4 {:a "1", :number/b 2})
(def map-5 {:a "1"}) ;; using the same spec that worked for map-4

(s/valid? ::map-1 map-1) ;;=> true
```
- define a ::normalized-vector spec that describes a vector of fractional
  numbers that sums up to one
- using `s/cat` define a seq of command-line option flags to value pairs:
```clj
(s/valid? ::cli-option-pairs ["-server" "foo" "-verbose" true "-user" "joe"])
```
- spec a CSV-like input [1 "foo" :a] using `s/coll-of` and `s/cat`
- what happens when you use `s/*` or `s/+` instead of `s/coll-of`, and why?
- spec a ring-like HTTP req object
