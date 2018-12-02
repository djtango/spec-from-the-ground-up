(ns section-two
  (:require [clojure.spec.alpha :as s]))

(s/def ::number-or-string
  (s/or :number number?
        :string string?))

(s/conform ::number-or-string "1") ;; => [:string "1"]

(defn extract-number [number-or-string]
  (let [conformed (s/conform ::number-or-string number-or-string)]
    (if (= ::s/invalid conformed)
      :not-a-number-or-string!
      (let [[tag value] conformed]
        (case tag
          :number value
          :string (Float/parseFloat value))))))

(s/def ::name string?)
(s/def ::age pos?)
(s/def ::breed #{:corgi :pug :poodle :german-shepherd :golden-retriever :labrador})
(s/def ::dog (s/keys :req [::name ::age ::breed ::colour]))
(s/def ::colour #{:black :brown :white :golden})
(s/valid? ::dog {::name "Fenton" ::age 1 ::breed :labrador ::colour :black})

(s/def ::ok boolean?)
(s/def ::error string?)
(s/def ::warning string?)
(s/def ::slack-web-api-response (s/keys :req-un [::ok] :opt-un [::error ::warning]))
(s/valid? ::slack-web-api-response {:ok true :stuff "This is good"}) ;; => true
(s/valid? ::slack-web-api-response {:ok false :error "something_bad"}) ;; => true

(s/valid? (s/coll-of number?) [1 2 3])
(s/valid? (s/coll-of odd?) [1 3 5])

(s/def ::board-position #{:O :X :empty})
(s/def ::tic-tac-toe-row (s/coll-of ::board-position :count 3))
(s/valid? ::tic-tac-toe-row [:O :X :O])

(s/def ::between-0-1 (s/and number?
                            #(>= 1 % 0)))
(defn normalized? [fractions]
  (try (= 1.0 (reduce + 0 fractions))
       (catch Exception _ nil))) ;; don't do this for real with floating points...
(s/def ::normalized-vector (s/coll-of ::between-0-1
                                      :kind normalized?
                                      :min-count 1
                                      :into []))
(s/valid? ::normalized-vector [0.5 0.5]) ;;=> true

(s/def ::header-names #{"Cache-Control" "Connection" "Content-Type" "Cookie"})
(s/def ::headers (s/map-of ::header-names string?))
(s/def ::method #{:GET :POST :PUT})
(s/def ::body string?)
(s/def ::noddy-http-req (s/keys :req [::headers ::method ::body]))

(s/def ::user-id uuid?)
(s/def ::user (s/keys :req [::name ::user-id]))
(s/valid? (s/map-of ::user-id ::user) {(java.util.UUID/randomUUID) {::name "foo" ::user-id (java.util.UUID/randomUUID)}})

(s/valid? (s/* string?) ["a" "b"]) ;; => true
(s/valid? (s/* string?) []) ;; => true
(s/valid? (s/+ string?) []) ;; => false
(s/valid? (s/+ string?) ["a"]) ;; => true
(s/valid? (s/? string?) ["a"]) ;; => true
(s/valid? (s/? string?) ["a" "b"]) ;; => false
(s/valid? (s/? string?) []) ;; => true

(s/def ::reframe-event (s/cat :event-name keyword?
                              :arg any?))
(s/valid? ::reframe-event [:add-todo "write more specs"]) ;;=> true
(s/conform ::reframe-event [:add-todo "write more specs"]) ;; => {:event-name :add-todo :arg "write more specs"}
(s/def ::datom (s/tuple number? keyword? any? number? boolean?))

(s/def ::divisible-by-three #(= 0 (mod % 3)))
(s/def ::divisible-by-five #(= 0 (mod % 5)))
(s/def ::fizzbuzz
  (s/or :fizzbuzz (s/and ::divisible-by-five ::divisible-by-three)
        :fizz ::divisible-by-three
        :buzz ::divisible-by-five))

(s/def :number/a number?)
(s/def :number/b number?)
(s/def :string/a string?)
(s/def :string/b string?)
(s/def ::map-1 (s/keys :req [:number/a]))
(s/def ::map-2 (s/keys :req [:string/a]))
(s/def ::map-3 (s/keys :req [:number/a] :opt-un [:string/b]))
(s/def ::map-4 (s/keys :req-un [:string/a] :opt [:number/b]))

(s/def ::cli-option-pair (s/cat :flag string? :value (s/alt :string string? :boolean boolean?)))
(s/def ::cli-option-pairs (s/+ ::cli-option-pair))
(s/def ::csv-row (s/cat :i integer? :s string? :k keyword?))
