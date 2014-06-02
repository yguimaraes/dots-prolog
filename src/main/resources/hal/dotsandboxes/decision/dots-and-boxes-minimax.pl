% Importa a biblioteca assoc para representar o estado do tabuleiro com as association lists
:- use_module(library(assoc)).

% Turns out Sicstus 3 doesn't have the apply library (I used maplist 
% origionally) so I re-implemented maplist as map/3 near the bottom of this 
% file.
%:- use_module(library(apply)).

% Importa a biblioteca list para realizar operacoes comuns em listas(ex:tamanho).
:- use_module(library(lists)).

% Biblioteca random para gerar numeros aleatorios e nao selecionar sempre o mesmo dos movimentos otimizados
:- use_module(library(random)).

% Prints the elements of a list separated by a space
println(List):-
	println(List, ' ').
println([], _):-nl.
println([Head|Tail], Separator):-
	write(Head), write(Separator), println(Tail).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% These predicates are used for main()...
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% state_with(+Width, +Height, +EdgePlayerPairs, +Players, -State)
%
% State is a gamestate with the given features.
%
% EdgePlayerPairs if a list of edge-player pairs of the form:
% 
% [edge(0,1,right)-p1|_]
%
state_with(Width, Height, EdgePlayerPairs, Players, State):-
	empty_game_state(Width, Height, Players, EmptyState),
	state_with_edges(EmptyState, EdgePlayerPairs, State).

%% state_with_edges(+State, +Edges, -StateWithEdges)
%
% StateWithEdges is State with the list of Edges added.
%
state_with_edges(State, [], State).
state_with_edges(State, [Edge-Player|Rest], StateWithEdges):-
	state_put_edge(State, Edge, Player, NewState),
	state_with_edges(NewState, Rest, StateWithEdges).

main(Width, Height, EdgePlayerPairs, SearchDepth, Players):-
	state_with(Width, Height, EdgePlayerPairs, Players, State),
	next_player(State, FavoredPlayer),
	
	% Run minimax, handling any errors (e.g. out of memory conditions)
	catch(minimax(State, FavoredPlayer, SearchDepth, move(Score, Edge)), 
	      Error, handle_error(Error)),
	
	write(move(Score, Edge)).

handle_error(Error):-
	error_message(Error, Message),
	write(user_error, 'Error occoured running minimax: '), 
	write(user_error, Message), 
	write(user_error, '\n'),
	halt(3).

error_message(resource_error(0,memory), 'Resource error: insufficient memory').
error_message(Error, Error).

/*
 * The code following is the implementation of minimax with supporting 
 * predicates to work with the rules of dots and boxes.
 * 
 * I wrote everything from scratch myself rather than using the base Fox and 
 * Goose game provided to gain more experience, and a better understanding of 
 * Prolog.
 */

% The directions 
direction(right).
direction(down).

player(p1).
player(p2).


%% empty_game_state(+Width, +Height, ?Players, ?State)
% 
% True if State is an empty gamestate. 
% @param ?Players a list of two distinct players
% 
empty_game_state(Width, Height, Players, State):-
	State = gamestate(Width, Height, Edges, Players),
	empty_assoc(Edges),
	Players = [P1|[P2]], player(P1), player(P2), P1 \= P2.


%% edge_in_game(+Width, +Height, -Edge)
%
% Enumerates the edges available in a game state of the given width and height.
% An edge is of the form edge(X, Y, right|down). 
%
edge_in_game(Width, Height, Edge) :-
	Edge = edge(X, Y, down), 
	in_range(0, Width, X), 
	YLimit is Height - 1, in_range(0, YLimit, Y).
edge_in_game(Width, Height, Edge) :-
	Edge = edge(X, Y, right), 
	XLimit is Width - 1, in_range(0, XLimit, X), 
	in_range(0, Height, Y).


%% state_open_edge(+State, -Edge)
% 
% Edge is an open edge if it has not yet been added to the game state.
% 
state_open_edge(State, Edge) :-
	State = gamestate(Width, Height, Edges, _),
	edge_in_game(Width, Height, Edge),
	(get_assoc(Edge, Edges, edge_data(player(_), age(_))) ->
		fail ; true).


%% state_closed_edge(+State, -Edge, -EdgeData)
% 
% Edge is a closed edge if it *has* been added to the game state.
% 
state_closed_edge(State, Edge, EdgeData) :-
	State = gamestate(Width, Height, Edges, _),
	edge_in_game(Width, Height, Edge),
	get_assoc(Edge, Edges, EdgeData).


%% state_put_edge(+GameState, +Edge, +Player, -NewState)
% 
% Adds the edge to the state, associating it with the player. NewState will be
% the input gamestate with the edge added.
% 
state_put_edge(GameState, Edge, Player, NewState) :-
	% break up the gamestate
	GameState = gamestate(Width, Height, Edges, Players),
	
	% Ensure player is a player
	player(Player), member(Player, Players),
	
	% Ensure the edge is the bounds of the game
	edge_in_game(Width, Height, Edge),
	
	(newest_edge(GameState, _, edge_data(_, age(NewestAge))) ->
		Age is NewestAge + 1 ;
		Age is 0),
	
	put_assoc(Edge, Edges, edge_data(player(Player), age(Age)), NewEdges),
	
	% unify newstate with the updated data
	NewState = gamestate(Width, Height, NewEdges, Players), !.


%% in_range(+Min, +Max, ?N)
% 
% Enumerates the integers between Min and Max. N is an integer >= Min and < Max.
%
in_range(Min, Max, N) :- Min < Max, N = Min.
in_range(Min, Max, N) :- 
	NextMin is Min + 1, NextMin < Max, in_range(NextMin, Max, N).

%% minimax(+GameState, +FavoredPlayer, +Depth, -Result)
% 
% Gives a score to a gamestate by recursively applying minimax to child states.
%
% Result is of the form result(score, edge(x, y, direction))
%
minimax(GameState, FavoredPlayer, Depth, Result) :-
	% The recursive case of minimax. Here we generate a new gamestate for 
	% each possible move that can be made at this depth and recursivley 
	% evaluate the new states with minimax.
	
	% Depth must be greater than zero otherwise we can't recurse.
	Depth > 0,
	
	% Find the player who is to move this round
	next_player(GameState, Player),
	
	% Collect all possible edges we can add in a list
	findall(Edge, state_open_edge(GameState, Edge), Edges),
	
	% Evaluate all possible moves from this point by mapping apply_edge onto
	% the list of edges and collecting the resulting move(Edge, Score) pairs
	% in a list.
	map_tr(apply_edge(GameState, FavoredPlayer, Depth, Player),
		Edges, % The list of possible edges
		Moves), % A will be a list of move(Edge, Score) elements
	
	% Sort the moves by score
	sort(Moves, SortedMoves),
	
	% Find the score we wish to return. We will return a random move who has
	% a score equal to this.
	(FavoredPlayer == Player ->
		% If the current player is the favored player we select the 
		% highest move from the options, unifying our score and move
		% variables with this best move's score and edge.
		last(SortedMoves, move(TargetScore, _)) ;
		% Otherwise we assume the opponent will make the worst move for
		% us so we select the lowest scoring move (the head of the list)
		[move(TargetScore, _)|_] = SortedMoves), 
	
	% Get a list containing only optimal moves
	filter(move_with_score(TargetScore), SortedMoves, OptimalMoves),
	
	% Select a random element from the optimal moves and unify it with 
	% Result
	random_element(OptimalMoves, Result), !.

minimax(GameState, FavoredPlayer, 0, move(Score, _)) :-
	% The first recursion base case. When depth is 0 we're not allowed to
	% expand any further child states (e.g. try out moves) so we just have
	% to evaluate the board's utility for our favored player.
	evaluate_board(GameState, FavoredPlayer, Score), !.

minimax(GameState, FavoredPlayer, _, move(Score, _)) :-
	% The second recursion base case. When no more moves can be made in the
	% board we evaluate the board and stop recursing.
	no_moves_left(GameState),
	evaluate_board(GameState, FavoredPlayer, Score), !.

%% apply_edge(+GameState, +FavoredPlayer, +CallerDepth, +Player, +Edge, -Result)
% 
% Constructs a new state by adding the provided Edge to the GameState and 
% evaluates the resulting gamestate using minimax, giving the score and edge in 
% Result.
%
% This predicate is used by maplist in minimax to recursivley call minimax. In
% that instance, the last two parameters are not specified as they are added by
% maplist when it calls this predicate.
%
apply_edge(GameState, FavoredPlayer, CallerDepth, Player, Edge, Result):-
	Depth is CallerDepth - 1,
	
	% Construct a new gamestate with the provided edge added by the player
	state_put_edge(GameState, Edge, Player, NewState),
	
	% Call minimax on the new state to find its score
	minimax(NewState, FavoredPlayer,  Depth, move(Score, _)),
	
	% unify result with a move representing the edge applied and the score 
	% it resulted in.
	Result = move(Score, Edge).

move_with_score(Score, move(Score, _)).

%% evaluate_board(+GameState, +FavoredPlayer, -Score)
%
% Gives the board a value representing the utillity to the favored player.
% Positive values are better for the favored player.
%
evaluate_board(GameState, FavoredPlayer, Score) :-
	GameState = gamestate(Width, Height, _, [P1|[P2|_]]),
	
	MaxScore is (Width - 1) * (Height - 1),
	
	cell_count(GameState, P1, P1Score),
	cell_count(GameState, P2, P2Score),
	
	(FavoredPlayer = P1 ->
		Score is (P1Score - P2Score) / (MaxScore / 2.0) ;
		Score is (P2Score - P1Score) / (MaxScore / 2.0)).


%% no_moves_left(+GameState)
% 
% True if the gamestate has no more moves which could be made.
% 
no_moves_left(GameState):-
	GameState = gamestate(Width, Height, _, _),
	% Find the maximum number of edges a board of provided size can hold
	max_edge_count(Width, Height, MaxEdges),
	
	% Find the number of edges in the the state
	edge_count(GameState, EdgeCount),
	
	% The board is full if EdgeCount is equal to MaxEdges.
	EdgeCount >= MaxEdges.


%% next_player(+GameState, -Player)
%
% Gives the player who is to make the next move given the gamestate.
%
next_player(GameState, Player):-
	edge_count(GameState, 0),
	GameState = gamestate(_, _, _, [Player|_]).
next_player(GameState, Player):-
	previous_player(GameState, PreviousPlayer),
	(newest_edge_completed_cell(GameState, _, _) ->
		% The previous player finished a cell so they get to play again
		Player = PreviousPlayer ;
		
		% unify player with the player who isn't the previous player
		GameState = gamestate(_, _, _, [P1|[P2|_]]),
		(PreviousPlayer = P1 -> Player = P2 ; Player = P1)).
		

%% previous_player(+GameState, -Player)
%
% The previous player to play is the player who added the most recent edge.
%
previous_player(GameState, Player):-
	newest_edge(GameState, _, edge_data(player(Player), _)).

cell_uses_edge(GameState, edge(X, Y, down), Cell):-
	XMinus1 is X - 1,
	Cell = cell(XMinus1, Y),
	cell_completed_in_state(GameState, Cell, _, _).
cell_uses_edge(GameState, edge(X, Y, right), Cell):-
	YMinus1 is Y - 1,
	Cell = cell(X, YMinus1),
	cell_completed_in_state(GameState, Cell, _, _).
cell_uses_edge(GameState, edge(X, Y, _), Cell):-
	Cell = cell(X, Y),
	cell_completed_in_state(GameState, Cell, _, _).

%% newest_edge_completed_cell(+GameState, -Edge, -Cell)
%
% The newest edge completed a cell if either of the cells adjacent to the edge
% are completed.
%
newest_edge_completed_cell(GameState, Edge, Cell):-
	newest_edge(GameState, Edge, _),
	cell_uses_edge(GameState, Edge, Cell).

newest_edge(GameState, Edge, EdgeData):-
	GameState = gamestate(_, _, Edges, _),
	assoc_to_list(Edges, EdgeList),
	EdgeList = [First|_],
	newest_edge_(EdgeList, First, Edge-EdgeData).

newest_edge_([], CurrentLargest, Largest):- CurrentLargest = Largest.
newest_edge_([Head|Rest], CurrentLargest, Largest):-
	Head = _-edge_data(_, age(HeadAge)),
	CurrentLargest = _-edge_data(_, age(Age)),
	(HeadAge > Age ->
		newest_edge_(Rest, Head, Largest) ;
		newest_edge_(Rest, CurrentLargest, Largest)).
		


%% max_edge_count(+Width, +Height, -Count)
%
% Count is the number of edges in a dots and boxes grid of the specified width
% and height.
%
max_edge_count(Width, Height, Count):-
	Count is 2 * (Width - 1) * (Height - 1) + (Width - 1) + (Height - 1).


%%
%
% 
%
edge_count(GameState, Count):-
	GameState = gamestate(_, _, Edges, _),
	assoc_to_list(Edges, EdgeList), length(EdgeList, Count).


%% cell_in_grid(?Width, ?Height, ?Cell)
%
% Succeeds if the cell is a valid location in a grid of the given width and 
% height.
%
cell_in_grid(Width, Height, Cell):-
	in_range(0, Width, X),
	in_range(0, Height, Y),
	Cell = cell(X, Y).


%% cell_completed_in_state(+GameState, ?Cell, ?CompletingEdge, 
%                          ?CompletingEdgeData)
%
% Succeeds if the specified cell has been completed in the gamestate. i.e. if
% the cell has all 4 of its edges existing in the gamestate.
%
cell_completed_in_state(GameState, Cell, CompletingEdge, CompletingEdgeData):-
	GameState = gamestate(Width, Height, _, _),
	cell_in_grid(Width, Height, Cell),
	Cell = cell(X, Y),
	XPlus1 is X + 1, YPlus1 is Y + 1,
	
	Edge1 = edge(X, Y, right), 
	edge_in_gamestate(GameState, Edge1, Data1),
	
	Edge2 = edge(X, Y, down), 
	edge_in_gamestate(GameState, Edge2, Data2),
	
	Edge3 = edge(X, YPlus1, right), 
	edge_in_gamestate(GameState, Edge3, Data3),
	
	Edge4 = edge(XPlus1, Y, down), 
	edge_in_gamestate(GameState, Edge4, Data4),
	
	oldest_edge(Edge1-Data1, Edge2-Data2, Edge3-Data3, Edge4-Data4, 
		CompletingEdge-CompletingEdgeData).
	
oldest_edge(Edge1, Edge2, Edge3, Edge4, OldestEdge):-
	oldest_edge(Edge1, Edge2, Res1),
	oldest_edge(Edge3, Edge4, Res2),
	oldest_edge(Res1, Res2, OldestEdge).

%% oldest_edge(+Edge1, +Edge2, ?OldestEdge)
%
% OldestEdge is the Edge-EdgeData pair with the largest age.
%
oldest_edge(Edge1, Edge2, OldestEdge):-
	Edge1 = edge(_, _, _)-edge_data(_, age(Age1)),
	Edge2 = edge(_, _, _)-edge_data(_, age(Age2)),
	(Age1 > Age2 -> OldestEdge = Edge1 ; OldestEdge = Edge2).
	

%% edge_in_gamestate(+GameState, -Edge)
%
% Succeeds if the edge exists in the gamestate.
% 
edge_in_gamestate(GameState, Edge, EdgeData):-
	GameState = gamestate(_, _, Edges, _),
	get_assoc(Edge, Edges, EdgeData).


cell_count(GameState, Player, Count):-
	findall(X, cell_completed_in_state(
		GameState, X, _, edge_data(player(Player), _)), Cells),
	length(Cells, Count).

%% random_element(+List, -Element).
%
% Element is a randomly chosen element from the List.
%
random_element([], _):- fail.
random_element(List, Element):-
	length(List, Len),
	random(0, Len, RandomIndex),
	nth0(RandomIndex, List, Element), !.

/*
 * I implemented the following predicates myself to replace the apply library I
 * had been using in SWI/Sicstus4 when I found that Sicstus 3 doesn't come with
 * it.
 *
 * I implemented left and right folds, map as well as a tail recursive version 
 * of right fold and map (as I suspected the non tail recursive versions were 
 * the cause of running out of my program running out of stack space, but this 
 * turned out not to be the case).
 */

%% fold_left(+Predicate, +List, -Result)
%
% Performs a left fold on the List by calling the predicate on initially the
% first and second members of the list, then calling predicate on the result
% and the 3rd element and so on untill all elements of the list have been 
% combined with the results of combining the previous elements.  
%
fold_left(Predicate, [Initial|List], Result):-
	foldl_(List, Predicate, Initial, Result).

%% foldl(+Predicate, +Initial, +List, -Result)
%
% The same as foldl/3 except the initial value is given explicitly instead of
% being the first element of the list.
%
fold_left(Predicate, Initial, List, Result):-
	foldl_(List, Predicate, Initial, Result).

% The result of folding an empty list is the starting value
foldl_([], _, Value, Result):-
	Value = Result.
foldl_([Head|Tail], Predicate, Value, Result):-
	call_n(Predicate, [Value, Head, NewValue]),
	foldl_(Tail, Predicate, NewValue, Result).

%% foldr(+Predicate, +Initial, +List, -Result)
%
% Performs a right fold on the list, reducing it to Result.
%
fold_right(Predicate, Initial, List, Result):-
	foldr_(List, Predicate, Initial, Result).
foldr_([], _, Value, Result):- Value = Result.
foldr_([Head|Tail], Predicate, Value, Result):-
	foldr_(Tail, Predicate, Value, TailResult),
	call_n(Predicate, [Head, TailResult, Result]).

%% map(+Predicate, +List, -Result)
%
% Each element of Result is the element of List at the same position after 
% having applying predicate applied to it.
%
% The Predicate will be called with a list element and should unify the 
% subsiquent parameter with the result to store in the result list.  
%
map(Predicate, List, Result):-
	fold_right(map_apply(Predicate), [], List, Result).

map_apply(Predicate, Value, Rest, Result):-
	call_n(Predicate, [Value, OutValue]),
	cons_(OutValue, Rest, Result).

cons_(First, Rest, [First|Rest]).

plus_(A, B, Out):- Out is A + B.

% Work around sicstus 3 not supporting call w/ variable arity
call_n(Pred, ArgList):-
	Pred =.. Term,
	append(Term, ArgList, TermN),
	ToCall =.. TermN,
	call(ToCall).

% Implementation of reverse tail-recursively using foldl
reverse_(List, Reversed):-
	fold_left(flip_cons_, [], List, Reversed).

flip_cons_(A, B, Result):-
	cons_(B, A, Result).

% An implementation of fold_right (not normally tail recursive) which IS tail 
% recursive thanks to fold_left (which is tail recursive) being used to 
% implement it.
fold_right_tr(Predicate, Initial, List, Result):-
	reverse_(List, Reversed),
	fold_left(call_flip_args_(Predicate), Initial, Reversed, Result).

call_flip_args_(Predicate, First, Second, Result):-
	call_n(Predicate, [Second, First, Result]).

% A tail recursive implementation of the map higher order function.
map_tr(Predicate, List, Result):-
	fold_right_tr(map_apply(Predicate), [], List, Result).

%% filter(+Predicate, +List, -Result)
%
% Result is a list containing every element of List for which Predicate(Element)
% succeeds.
%
filter(Predicate, List, Result):-
	fold_right_tr(filter_pred_(Predicate), [], List, Result).

filter_pred_(Predicate, Element, Rest, Out):-
	(call_n(Predicate, [Element])
		-> cons_(Element, Rest, Out)
		;  Rest = Out).