(ns section-one-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [section-one :as sut])) ;; subject-under-test

(deftest numbers-divisible-by-three
  (testing "these numbers should be divisible-by-three"
    (is (s/valid? ::sut/divisible-by-three 3))
    (is (s/valid? ::sut/divisible-by-three 6))
    (is (s/valid? ::sut/divisible-by-three 9))
    (is (s/valid? ::sut/divisible-by-three 99)))
  (testing "these numbers should not be divisible-by-three"
    (is (not (s/valid? ::sut/divisible-by-three 1)))
    (is (not (s/valid? ::sut/divisible-by-three 2)))
    (is (not (s/valid? ::sut/divisible-by-three 4)))
    (is (not (s/valid? ::sut/divisible-by-three 5)))
    (is (not (s/valid? ::sut/divisible-by-three 100)))))

(deftest chess-pieces
  (testing "these should all be valid chess pieces"
    (is (s/valid? ::sut/chess-piece :pawn))
    (is (s/valid? ::sut/chess-piece :bishop))
    (is (s/valid? ::sut/chess-piece :rook))
    (is (s/valid? ::sut/chess-piece :knight))
    (is (s/valid? ::sut/chess-piece :queen))
    (is (s/valid? ::sut/chess-piece :king)))
  (testing "these should not be valid chess pieces"
    (is (not (s/valid? ::sut/chess-piece nil)))
    (is (not (s/valid? ::sut/chess-piece 1)))
    (is (not (s/valid? ::sut/chess-piece "rook")))
    (is (not (s/valid? ::sut/chess-piece [])))))

(deftest word-longer-than-five-letters
  (testing "these should be valid words"
    (is (s/valid? ::sut/word-longer-than-five-letters "foobar"))
    (is (s/valid? ::sut/word-longer-than-five-letters "clojure"))
    (is (s/valid? ::sut/word-longer-than-five-letters "lambda")))
  (testing "these should not be valid words"
    (is (not (s/valid? ::sut/word-longer-than-five-letters "foo")))
    (is (not (s/valid? ::sut/word-longer-than-five-letters ["a" "b" "c" "d" "e" "f"])))
    (is (not (s/valid? ::sut/word-longer-than-five-letters (range 5))))
    (is (not (s/valid? ::sut/word-longer-than-five-letters "rich hickey")))))

(deftest postcodes
  (testing "these are valid postcodes"
    (is (s/valid? ::sut/postcode "EC1A 1BB"))
    (is (s/valid? ::sut/postcode "W1A 0AX"))
    (is (s/valid? ::sut/postcode "M1 1AE"))
    (is (s/valid? ::sut/postcode "B33 8TH"))
    (is (s/valid? ::sut/postcode "CR2 6XH"))
    (is (s/valid? ::sut/postcode "DN55 1PT")))
  (testing "these are not valid postcodes"
    (is (not (s/valid? ::sut/postcode "asdfbc")))
    (is (not (s/valid? ::sut/postcode "ec1a 1bb")))
    (is (not (s/valid? ::sut/postcode 1)))))
