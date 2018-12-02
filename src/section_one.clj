(ns section-one
  (:require [clojure.spec.alpha :as s]))

(def at-least-one? #(>= % 1))
(def at-most-six? (partial >= 6))
(s/valid? at-least-one? 5) ;;=> true
(s/valid? at-most-six? 3) ;;=> true

(def between-one-and-six?
  #(and (at-least-one? %)
        (at-most-six? %)))

(comment
 (s/valid? between-one-and-six? 0) ;; => false
 (s/valid? between-one-and-six? 7) ;; => false
 (s/valid? between-one-and-six? 2) ;; => true

 (s/valid? {:a 1} :a) ;;=> true
 (s/valid? {:a false} :a) ;;=> false
 (s/valid? {:a nil} :a) ;;=> false
 (s/valid? (fn [k _] true) :a) ;;=> ArityException Wrong number of args (1) ...
 )

(s/def ::non-negative-number #(>= % 0))

(s/def ::inventory ::non-negative-number)
(s/def ::dollar-value ::non-negative-number)
(s/def ::average-rating ::non-negative-number)
(s/def ::number-of-pages ::non-negative-number)

(s/def ::die-roll (s/and number? ;; this is superfluous
                         ::non-negative-number ;; so is this...
                         between-one-and-six?)) ;; examples are hard.

(comment
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
 )

(s/def ::divisible-by-three #(= 0 (mod % 3)))
(s/def ::chess-piece #{:rook :pawn :bishop :knight :queen :king})
(s/def ::word-longer-than-five-letters (s/and string?
                                              #(> (count %) 5)
                                              (fn single-word? [s]
                                                (nil? (clojure.string/index-of s " ")))))

(s/def ::capital-letter (set "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
(s/def ::numerical-char (set "0123456789"))
(s/def ::whitespace (set " "))

(s/def ::A ::capital-letter)
(s/def ::9 ::numerical-char)
(s/def ::- ::whitespace)
(s/def ::char-vec (s/conformer (fn [s]
                                 (if (string? s)
                                   (vec s)
                                   ::s/invalid))
                               #(apply str %)))

(s/def ::postcode (s/or :AA9A-9AA (s/and string?
                                         ::char-vec
                                         (s/tuple ::A ::A ::9 ::A ::- ::9 ::A ::A))
                        :A9A-9AA (s/and string?
                                        ::char-vec
                                        (s/tuple ::A ::9 ::A ::- ::9 ::A ::A))
                        :A9-9AA (s/and string?
                                       ::char-vec
                                       (s/tuple ::A ::9 ::- ::9 ::A ::A))
                        :A99-9AA (s/and string?
                                        ::char-vec
                                        (s/tuple ::A ::9 ::9 ::- ::9 ::A ::A))
                        :AA9-9AA (s/and string?
                                        ::char-vec
                                        (s/tuple ::A ::A ::9 ::- ::9 ::A ::A))
                        :AA99-9AA (s/and string?
                                         ::char-vec
                                         (s/tuple ::A ::A ::9 ::9 ::- ::9 ::A ::A))))
