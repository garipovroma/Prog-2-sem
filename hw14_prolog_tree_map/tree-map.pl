%delay + review

%node(Key, Val, L, R, P)
split(Vert, K, LeftRes, RightRes) :-
		Vert = undefined, LeftRes = undefined, RightRes = undefined, !.
split(Vert, K, LeftRes, RightRes) :-
		Vert = node(Key, Val, L, R, P), K =< Key, !, split(L, K, LeftRes, Right), RightRes = node(Key, Val, Right, R, P). 
split(Vert, K, LeftRes, RightRes) :-
		Vert = node(Key, Val, L, R, P), K > Key, !, split(R, K, Left, RightRes), LeftRes = node(Key, Val, L, Left, P).

merge(Vert, undefined, Vert):- !.
merge(undefined, Vert, Vert):- !.
merge(Left, Right, Vert) :-
		Left = node(KeyL, ValL, LL, RL, PL), Right = node(KeyR, ValR, LR, RR, PR),
		PL >= PR, !, merge(RL, Right, Ress), Vert = node(KeyL, ValL, LL, Ress, PL).
merge(Left, Right, Vert) :-
		Left = node(KeyL, ValL, LL, RL, PL), Right = node(KeyR, ValR, LR, RR, PR), 
		PL < PR, !, merge(Left, LR, Ress), Vert = node(KeyR, ValR, Ress, RR, PR).
			
map_get(TreeMap, QueryKey, QueryValue) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		VertKey = QueryKey, VertValue = QueryValue, !.
map_get(TreeMap, QueryKey, QueryValue) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		QueryKey < VertKey, !, map_get(L, QueryKey, QueryValue).
map_get(TreeMap, QueryKey, QueryValue) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		QueryKey > VertKey, !, map_get(R, QueryKey, QueryValue).
		

add(TreeMap, Key, Value, Result, Priority) :-		
		not(map_get(TreeMap, Key, OtherValue)), !, split(TreeMap, Key, Left, Right), NewNode = node(Key, Value, undefined, undefined, Priority), 
		merge(Left, NewNode, Merged), merge(Merged, Right, Result).
		
add(TreeMap, Key, Value, Result, Priority) :-
		map_get(TreeMap, Key, OtherValue), !, map_remove(TreeMap, Key, ResultTree), add(ResultTree, Key, Value, Result, Priority).

map_put(TreeMap, Key, Value, Result) :-
		rand_int(2147483647, Priority), !,
		add(TreeMap, Key, Value, Result, Priority).

erase(TreeMap, QueryKey, NewVert) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		VertKey = QueryKey, !, merge(L, R, NewVert).
erase(TreeMap, QueryKey, NewVert) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		QueryKey < VertKey, !, erase(L, QueryKey, NewL), NewVert = node(VertKey, VertValue, NewL, R, P).
erase(TreeMap, QueryKey, NewVert) :-		
		TreeMap = node(VertKey, VertValue, L, R, P),
		QueryKey > VertKey, !, erase(R, QueryKey, NewR), NewVert = node(VertKey, VertValue, L, NewR, P).

map_remove(TreeMap, Key, Res) :-		
		erase(TreeMap, Key, Res).
		%split(TreeMap, Key + 1, Left, Right), split(Left, Key, ToMerge, Trash), merge(ToMerge, Right, Res).

map_build([], undefined) :- true, !.
map_build([(K, V) | T], TreeMap) :- 
		map_build(T, TreeTail), map_put(TreeTail, K, V, TreeMap).

lower_bound(TreeMap, Key, Res) :-
		TreeMap = node(VertKey, VertValue, L, R, P),
		VertKey >= Key, not(L = undefined), !, lower_bound(L, Key, Res).

lower_bound(TreeMap, Key, Res) :-
		TreeMap = node(VertKey, VertValue, L, R, P),
		VertKey >= Key, L = undefined, !, Res is VertKey.

map_ceilingKey(Map, Key, CeilingKey) :-
		split(Map, Key, Left, Right), 
		lower_bound(Right, Key, CeilingKey).
	

		
