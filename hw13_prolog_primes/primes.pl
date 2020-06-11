%review
divisible(X, Y) :- 0 is X mod Y.

sieve_table(1).

sieve(I, N) :- I > N, !.
sieve(I, N) :- not(sieve_table(I)), II is I * I, sieve_fill(II, N, I).
sieve(I, N) :- I < N, I1 is I + 1, sieve(I1, N). 

sieve_fill(I, N, J) :- I < N, /*write(I), nl,*/ assert(sieve_table(I)), M is I + J, sieve_fill(M, N, J).

init(MAX_N) :-
		N is MAX_N + 1,
		sieve(2, N), !.

prime(X) :- not(sieve_table(X)).
composite(X) :- sieve_table(X).

sorted([]).
sorted([H]).
sorted([A, B | T]) :- A =< B, sorted([B | T]).
min_divisor_loop(I, N, RES) :- I * I > N.
min_divisor_loop(I, N, RES) :- I * I =< N, divisible(N, I), prime(I), RES is I.
min_divisor_loop(I, N, RES) :- I * I =< N, not(divisible(N, I)), I1 is I + 1, min_divisor_loop(I1, N).
min_divisor_loop(I, N, RES) :- I * I =< N, not(prime(N, I)), I1 is I + 1, min_divisor_loop(I1, N, RES).
min_divisor(X, R) :- not(prime(X)), min_divisor_loop(2, X, R).
min_divisor(X, R) :- prime(X), R is X.
prime_divisors(1, []).
prime_divisors(X, [X]) :- prime(X), !.
prime_divisors(X, [H | T]) :- not number(X), prime(H), prime_divisors(XX, T), sorted([H | T]), X is XX * H, !.
prime_divisors(X, [H | T]) :- number(X), X > 1, min_divisor(X, H), XX is X / H, prime_divisors(XX, T), sorted([H | T]), !.

concat([], B, B).
concat([H | T], B, [H | R]) :- concat(T, B, R).
check_pal([A | B]) :-
		reverse([A | B], BA),
		[A | B] = BA.

radix(X, BASE, [RES]) :-
		X < BASE, RES is X, !.
radix(X, BASE, [H | T]) :-
		H is X mod BASE,
		XX is div(X, BASE),
		radix(XX, BASE, T), !.
		
prime_palindrome(N, K) :- prime(N), radix(N, K, RES), check_pal(RES).
		
	
 
