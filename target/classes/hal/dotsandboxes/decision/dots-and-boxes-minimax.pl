% Representa o estado do tabuleiro com as association lists
:- use_module(library(assoc)).

% Para aplicar predicados nas listas, podendo filtrar listas por exemplo
:- use_module(library(apply)).

% Importa a biblioteca list para realizar operacoes comuns em listas(ex:tamanho).
:- use_module(library(lists)).

% Gera numeros aleatorios para nao selecionar sempre o mesmo movimento otimizado
:- use_module(library(random)).

% State eh o estado do jogo com as caracteristicas definidas.
% EdgePlayerPairs eh uma lista das jogadas dos jogadores no formato [edge(0,1,right)-p1|_]
state_with(Width, Height, EdgePlayerPairs, Players, State):-
	empty_game_state(Width, Height, Players, EmptyState),
	state_with_edges(EmptyState, EdgePlayerPairs, State).

% StateWithEdges eh o State com a lista de arestas(Edges) adicionadas.
state_with_edges(State, [], State).
state_with_edges(State, [Edge-Player|Rest], StateWithEdges):-
	state_put_edge(State, Edge, Player, NewState),
	state_with_edges(NewState, Rest, StateWithEdges).

%retorna o movimento otimizado, usando o algoritmo minimax
main(Width, Height, EdgePlayerPairs, SearchDepth, Players):-
	state_with(Width, Height, EdgePlayerPairs, Players, State),
	next_player(State, FavoredPlayer),
	minimax(State, FavoredPlayer, SearchDepth, move(Score, Edge)),
	write(move(Score, Edge)).

% As direcoes possiveis
direction(right).
direction(down).

% Os jogadores possiveis
player(p1).
player(p2).


%Verdade quanto State for um jogo vazio e a lista players contiver dois jogadores distintos.
empty_game_state(Width, Height, Players, State):-
	State = gamestate(Width, Height, Edges, Players),
        %Unifica Edges com uma lista de associacoes vazia
	empty_assoc(Edges),
	Players = [P1|[P2]], player(P1), player(P2), P1 \= P2.

% Enumera as arestas disponiveis em um jogo, uma aresta eh da forma edge(X, Y, right|down). 
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


% Associa a aresta de jogada de um jogador ao GameState gerando o novo NewState
state_put_edge(GameState, Edge, Player, NewState) :-
	GameState = gamestate(Width, Height, Edges, Players),
	
	% member verifica se um processo pertence a lista
	player(Player), member(Player, Players),
	
	% Verifica que a aresta esta nos limnites do jogo
	edge_in_game(Width, Height, Edge),
	
	(newest_edge(GameState, _, edge_data(_, age(NewestAge))) ->
		Age is NewestAge + 1 ;
		Age is 0),
	
	put_assoc(Edge, Edges, edge_data(player(Player), age(Age)), NewEdges),
	
	% unify newstate with the updated data
	NewState = gamestate(Width, Height, NewEdges, Players), !.

% Enumera os inteiros em N, tais que N >= Min e N < Max
in_range(Min, Max, N) :- Min < Max, N = Min.
in_range(Min, Max, N) :- 
	NextMin is Min + 1, NextMin < Max, in_range(NextMin, Max, N).

% Atribue uma pontuacao para um gamestate recursivamente, aplicando minimax nos
% estados filhos.
% Result esta no formato result(score, edge(x, y, direction))
minimax(GameState, FavoredPlayer, Depth, Result) :-
        % Sao gerados todos os estados para cada movimento possivel nessa
        % profundidade e feita avaliacao recursiva com o minimax

	% Profundidade precisa ser maior que 0, cc nao pode ocorrer recursao
	Depth > 0,
	
	% Encontra qual sera o jogador do turno atual
	next_player(GameState, Player),
	
	% Collect all possible edges we can add in a list
	findall(Edge, state_open_edge(GameState, Edge), Edges),
	
	% Evaluate all possible moves from this point by mapping apply_edge onto
	% the list of edges and collecting the resulting move(Edge, Score) pairs
	% in a list.
	maplist(apply_edge(GameState, FavoredPlayer, Depth, Player),
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
	include(move_with_score(TargetScore), SortedMoves, OptimalMoves),
	
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

% Atribue em Player quem sera o proximo jogador, dado o estado do jogo
next_player(GameState, Player):-
	edge_count(GameState, 0),
	GameState = gamestate(_, _, _, [Player|_]).
next_player(GameState, Player):-
	previous_player(GameState, PreviousPlayer),
	(newest_edge_completed_cell(GameState, _, _) ->
		% Se o jogador fechou um quadrado ele joga de novo
		Player = PreviousPlayer ;
		
		% unifica o jogador com o player que nao eh o jogador anterior
		GameState = gamestate(_, _, _, [P1|[P2|_]]),
		(PreviousPlayer = P1 -> Player = P2 ; Player = P1)).

% O previous_player eh o autor do newest_edge
previous_player(GameState, Player):-
	newest_edge(GameState, _, edge_data(player(Player), _)).

% Verdade se um quadrado fechado usa uma aresta no jogo atual
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

% A ultima aresta fechou um quadrado se os quadrados adjacentes a aresta 
% estiverem fechados.
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

% Atribue o em Count o numero de arestas no jogo
edge_count(GameState, Count):-
	GameState = gamestate(_, _, Edges, _),
	assoc_to_list(Edges, EdgeList), length(EdgeList, Count).

% Verdade para os quadrados validos dado o tamanho do tabuleiro
cell_in_grid(Width, Height, Cell):-
	in_range(0, Width, X),
	in_range(0, Height, Y),
	Cell = cell(X, Y).

% Verdade se as quatro arestas do quadro existem no jogo
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
	
% OldestEdge e o par Edge-EdgeData com a maior idade (mais antigo)
oldest_edge(Edge1, Edge2, Edge3, Edge4, OldestEdge):-
	oldest_edge(Edge1, Edge2, Res1),
	oldest_edge(Edge3, Edge4, Res2),
	oldest_edge(Res1, Res2, OldestEdge).

% OldestEdge e o par Edge-EdgeData com a maior idade (mais antigo)
oldest_edge(Edge1, Edge2, OldestEdge):-
	Edge1 = edge(_, _, _)-edge_data(_, age(Age1)),
	Edge2 = edge(_, _, _)-edge_data(_, age(Age2)),
	(Age1 > Age2 -> OldestEdge = Edge1 ; OldestEdge = Edge2).
	
% Verdade para as arestas existentes no jogo
edge_in_gamestate(GameState, Edge, EdgeData):-
	GameState = gamestate(_, _, Edges, _),
        % Verdade quando Edge e EdgeData sao uma entrada de Edges
	get_assoc(Edge, Edges, EdgeData).


cell_count(GameState, Player, Count):-
	findall(X, cell_completed_in_state(
		GameState, X, _, edge_data(player(Player), _)), Cells),
	length(Cells, Count).

% O Element eh um item de List escolhido aleatoriamente
random_element([], _):- fail.
random_element(List, Element):-
	length(List, Len),
	random(0, Len, RandomIndex),
	nth0(RandomIndex, List, Element), !.