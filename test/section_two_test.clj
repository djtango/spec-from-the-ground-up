(ns section-two-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [next-steps :as sut])) ;; subject-under-test

(deftest fizzbuzz
  (testing "conforming 3 5 and 15 should return fizz buzz and fizzbuzz respectively"
    (is (= [:fizz 3] (s/conform ::sut/fizzbuzz 3)))
    (is (= [:buzz 5] (s/conform ::sut/fizzbuzz 5)))
    (is (= [:fizzbuzz 15] (s/conform ::sut/fizzbuzz 15)))))

(deftest all-the-keys
  (testing "these maps should validate successfully for specs of the same name"
    (let [map-1 {:number/a 1}
          map-2 {:string/a "1"}
          map-3 {:number/a 1, :b "2"}
          map-4 {:a "1", :number/b 2}
          map-5 {:a "1"}]
      (is (s/valid? ::sut/map-1 map-1))
      (is (s/valid? ::sut/map-2 map-2))
      (is (s/valid? ::sut/map-3 map-3))
      (is (s/valid? ::sut/map-4 map-4))
      (is (s/valid? ::sut/map-4 map-5)) ;; NB use the same spec for 4 and 5
      )))

(deftest normalized-vector
  (testing "a normalized vector should contain fractions that in total add up to one"
    (is (s/valid? ::sut/normalized-vector [0.5 0.5]))
    (is (s/valid? ::sut/normalized-vector [0.25 0.25 0.25 0.25]))
    (is (s/valid? ::sut/normalized-vector [1])))
  (testing "these are not normalized vectors"
    (is (not (s/valid? ::sut/normalized-vector [0.1])))
    (is (not (s/valid? ::sut/normalized-vector [0.9])))
    (is (not (s/valid? ::sut/normalized-vector [1.1])))
    (is (not (s/valid? ::sut/normalized-vector [])))
    (is (not (s/valid? ::sut/normalized-vector ["foo"])))))

(deftest cli-option-pairs
  (testing "this is an example of some CLI inputs that should pass"
    (let [cli-opts ["-server" "foo" "-verbose" true "-user" "joe"]]
      (is (s/valid? ::cli-option-pairs cli-opts)))))
