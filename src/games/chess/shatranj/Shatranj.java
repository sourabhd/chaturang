//package games.chess.shatranj;

import java.lang.Math;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;




/**
 * 
 * @author Sourabh Daptardar
 *
 */

/**
 * ShatranjType defines Chess Variant
 */
enum ShatranjType {

	NORMAL(8, 8, "rnbqkbnr" + "pppppppp" + "........" + "........" + "........"
			+ "........" + "PPPPPPPP" + "RNBQKBNR"

	);

	int width;
	int height;
	StringBuilder boardPosition;

	ShatranjType(int width, int height, String boardPosition) {
		this.width = width;
		this.height = height;
		this.boardPosition = new StringBuilder(boardPosition);
	}

};

class Pos {
	int x;
	int y;

	Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

/**
 * Players : teams - BLACK and WHITE
 */

enum Player {
	BLACK, WHITE
}

/**
 * 
 * Interface to define valid moves for a piece
 * 
 */

interface ValidMove {
	public Vector<Pos> getValidMoves(Shatranj S, Piece P);
}

/**
 * 
 * PieceType: defines behaviour (valid moves) of a piece
 * 
 */

enum PieceType {
	KING(0, 'K', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			Vector<Pos> V = new Vector<Pos>();
			V.clear();

			/* All 8 neighbours */
			for (int i = Math.max(0, P.xPos - 1); i <= Math.min(S.height - 1,
					P.xPos + 1); i++) {
				for (int j = Math.max(0, P.yPos - 1); j <= Math.min(
						S.width - 1, P.yPos + 1); j++) {
					if ((i == P.xPos) && (j == P.yPos))
						continue;
					if (S.isVacantSquare(i, j)
							|| S.isOpponentPiece(i, j, P.player))
						V.add(new Pos(i, j));
				}
			}
			return V;
		}
	}), QUEEN(1, 'Q', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			/* QUEEN == ROOK + BISHOP */
			Piece P1 = new Piece(-1, PieceType.ROOK, P.player, P.xPos, P.yPos);
			Piece P2 = new Piece(-2, PieceType.BISHOP, P.player, P.xPos, P.yPos);
			Vector<Pos> V1 = new Vector<Pos>(P1.type.vmove.getValidMoves(S, P1));
			Vector<Pos> V2 = new Vector<Pos>(P1.type.vmove.getValidMoves(S, P2));
			Vector<Pos> V = new Vector<Pos>();
			V.addAll(V1);
			V.addAll(V2);
			return V;
		}
	}), ROOK(2, 'R', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			Vector<Pos> V = new Vector<Pos>();
			V.clear();

			int i, j;
			/* North */
			for (i = P.xPos - 1, j = P.yPos; S.isVacantSquare(i, j); i--) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* East */
			for (i = P.xPos, j = P.yPos + 1; S.isVacantSquare(i, j); j++) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* West */
			for (i = P.xPos, j = P.yPos - 1; S.isVacantSquare(i, j); j--) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* South */
			for (i = P.xPos + 1, j = P.yPos; S.isVacantSquare(i, j); i++) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			return V;
		}
	}), KNIGHT(3, 'N', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			Vector<Pos> V = new Vector<Pos>();
			V.clear();
			if (S.isVacantOrOpponent(P.xPos + 1, P.yPos + 2, P.player))
				V.add(new Pos(P.xPos + 1, P.yPos + 2));
			if (S.isVacantOrOpponent(P.xPos + 1, P.yPos - 2, P.player))
				V.add(new Pos(P.xPos + 1, P.yPos - 2));
			if (S.isVacantOrOpponent(P.xPos - 1, P.yPos + 2, P.player))
				V.add(new Pos(P.xPos - 1, P.yPos + 2));
			if (S.isVacantOrOpponent(P.xPos - 1, P.yPos - 2, P.player))
				V.add(new Pos(P.xPos - 1, P.yPos - 2));
			if (S.isVacantOrOpponent(P.xPos + 2, P.yPos + 1, P.player))
				V.add(new Pos(P.xPos + 2, P.yPos + 1));
			if (S.isVacantOrOpponent(P.xPos + 2, P.yPos - 1, P.player))
				V.add(new Pos(P.xPos + 2, P.yPos - 1));
			if (S.isVacantOrOpponent(P.xPos - 2, P.yPos + 1, P.player))
				V.add(new Pos(P.xPos - 2, P.yPos + 1));
			if (S.isVacantOrOpponent(P.xPos - 2, P.yPos - 1, P.player))
				V.add(new Pos(P.xPos - 2, P.yPos - 1));
			return V;
		}
	}), BISHOP(4, 'B', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			Vector<Pos> V = new Vector<Pos>();
			V.clear();

			int i, j;
			/* North-West */
			for (i = P.xPos - 1, j = P.yPos - 1; S.isVacantSquare(i, j); i--, j--) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* North-East */
			for (i = P.xPos - 1, j = P.yPos + 1; S.isVacantSquare(i, j); i--, j++) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* South-West */
			for (i = P.xPos + 1, j = P.yPos - 1; S.isVacantSquare(i, j); i++, j--) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			/* South-East */
			for (i = P.xPos + 1, j = P.yPos + 1; S.isVacantSquare(i, j); i++, j++) {
				V.add(new Pos(i, j));
			}
			if (S.isOpponentPiece(i, j, P.player))
				V.add(new Pos(i, j));

			return V;

		}
	}), PAWN(5, 'P', new ValidMove() {
		public Vector<Pos> getValidMoves(Shatranj S, Piece P) {
			Vector<Pos> V = new Vector<Pos>();
			V.clear();

			if (P.player == Player.WHITE) {
				if (S.isVacantSquare(P.xPos - 1, P.yPos))
					V.add(new Pos(P.xPos - 1, P.yPos));
				if (P.xPos == S.height - 2)
					V.add(new Pos(P.xPos - 2, P.yPos)); /*
														 * TODO: revisit opening
														 * move
														 */
				if (S.isOpponentPiece(P.xPos - 1, P.yPos - 1, P.player))
					V.add(new Pos(P.xPos - 1, P.yPos - 1));
				if (S.isOpponentPiece(P.xPos - 1, P.yPos + 1, P.player))
					V.add(new Pos(P.xPos - 1, P.yPos + 1));
				/* En passant ?? */
			} else {
				if (S.isVacantSquare(P.xPos + 1, P.yPos))
					V.add(new Pos(P.xPos+1, P.yPos));
				if (P.xPos == 1)
					V.add(new Pos(P.xPos + 2, P.yPos)); /*
														 * TODO: revisit opening
														 * move
														 */
				if (S.isOpponentPiece(P.xPos + 1, P.yPos - 1, P.player))
					V.add(new Pos(P.xPos + 1, P.yPos - 1));
				if (S.isOpponentPiece(P.xPos + 1, P.yPos + 1, P.player))
					V.add(new Pos(P.xPos + 1, P.yPos + 1));
			}

			return V;
		}
	});

	final int pieceTypeID;
	final char pieceChar;
	private ValidMove vmove;

	private static final Map<Character, PieceType> lookup = new HashMap<Character, PieceType>();

	static {
		System.out.println("static block called");
		for (PieceType p : PieceType.values()) {
			lookup.put((new Character(p.pieceChar)), p);
			System.err.println("map:" +  p.name() + " " + p.pieceChar);
		}
	}

	public static PieceType get(char pChar) {
		return lookup.get((new Character(Character.toUpperCase(pChar))));
	}

	public static boolean containsKey(char pChar) {
		return lookup
				.containsKey((new Character(Character.toUpperCase(pChar))));
	}

	public ValidMove getVMove() {
		return vmove;
	}

	PieceType(int pieceTypeID, char pieceChar, ValidMove vmove) {
		this.pieceTypeID = pieceTypeID;
		this.pieceChar = pieceChar;
		this.vmove = vmove;
	}

}

/**
 * 
 * Piece repesents a chess piece
 * 
 */

class Piece {
	int pieceID;
	PieceType type;
	Player player;
	int xPos;
	int yPos;

	Piece(int pieceID, PieceType type, Player player, int xPos, int yPos) {
		this.pieceID  = pieceID;
		this.type     = type;
		this.player   = player;
		this.xPos     = xPos;
		this.yPos     = yPos;
	}
}

/**
 * Notation Fields
 */

enum FEN {
	PIECE_PLACEMENT_DATA(0), ACTIVE_COLOR(1), CASTLING_AVAILABILITY(2), EN_PASSANT_TARGET_SQUARE(
			3), HALFMOVE_CLOCK(4), FULLMOVE_CLOCK(5);

	int fen_field;

	FEN(int fen_field) {
		this.fen_field = fen_field;
	}
}

/* Wanted a typedef :) */

class ChessMen extends HashMap<Pos, Piece> {
	private static final long serialVersionUID = 1000L;
}

class Move {
	Piece piece;
	Pos pos;

	Move(Piece piece, Pos pos) {
		this.piece = piece;
		this.pos = pos;
	}
}

class ChessMoves extends HashMap<Player, Vector<Move>> {
	private static final long serialVersionUID = 1001L;
}

/**
 * 
 * Shatranj : Chess (validation) engine
 * 
 */

class Shatranj {
	int width;
	int height;
	public StringBuilder boardPosition;
	ChessMen chessMen;
	ChessMoves chessMoves;

	static final int NumFENFields = 6;

	void updateChessMenFromBoardPos() {
		int pieceCounter = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				char piece = boardPosition.charAt(i * width + j);
				System.err.println("updateChessMenFromBoardPos:" +
						(new Character(piece)).toString());

				if (PieceType.containsKey(piece)) {
					pieceCounter++;
					System.err.println("updateChessMenFromBoardPos" + (new Integer(
							pieceCounter)).toString());

					Player pl = ((Character.isLowerCase(piece) == true) ? Player.BLACK
							: Player.WHITE);
					if (PieceType.get(piece) == null) {
						System.err.println("piecetype:" + "Is null");
					} else {
						System.err.println("piecetype:" + PieceType.get(piece).name());
					}

					Pos ppos = new Pos(i, j);
					PieceType pt = PieceType.get(piece);
					System.err.println("Piece" + (new Character(piece)).toString());
					if (pt == null) {
						System.err.println("PieceType:" + "Is null");
					} else {
						System.err.println("PieceType" + PieceType.get(piece).name());
					}

					try {
						chessMen.put(new Pos(i, j), new Piece(pieceCounter,
								PieceType.get(piece), pl, i, j));
					} catch (Exception E) {
						E.printStackTrace();
					}
				}

			}
		}
	}

	public void updateboardPositionFromChessMen() {
		StringBuilder boardPos = new StringBuilder(width * height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				boardPos.setCharAt(i * width + j, '.');
			}
		}

		int sz = chessMen.size();
		for (int i = 0; i < sz; i++) {
			Piece P = chessMen.get(i);
			int xpos = P.xPos;
			int ypos = P.yPos;

			char pieceCharRep = P.type.pieceChar;
			if (P.player == Player.BLACK)
				pieceCharRep = Character.toLowerCase(P.type.pieceChar);
			else
				pieceCharRep = Character.toUpperCase(P.type.pieceChar);
			boardPos.setCharAt(xpos * width + ypos, pieceCharRep);
			this.boardPosition = boardPos;
		}

	}

	/**
	 * boardPosition as a string
	 */
	public String toString() {
		return boardPosition.toString();
	}

	/**
	 * Load board position from String
	 */

	void loadBoardPosition(int width, int height, String boardPosition) {
		this.width = width;
		this.height = height;
		this.boardPosition = new StringBuilder(boardPosition);
	}

	/**
	 * 
	 * Format board position to get text based board
	 */

	public String formatBoardPosition() {
		StringBuilder S = new StringBuilder("");
		int sz = width * height;
		for (int i = 0; i < sz; i++) {
			char sep = (((i % width) == 0) ? '\n' : ' ');
			S.append(sep);
			S.append(boardPosition.charAt(i));
		}
		S.append('\n');
		return S.toString();
	}

	/**
	 * Print formatted boardPosition to console
	 */
	void print() {
		System.out.print(this.formatBoardPosition());
	}

	/**
	 * Constructor
	 * 
	 * @param st
	 *            type of chess
	 */
	Shatranj(ShatranjType st) {
		this.width = st.width;
		this.height = st.height;
		this.boardPosition = st.boardPosition;

		this.chessMen = new ChessMen();
		this.chessMoves = new ChessMoves();

		updateChessMenFromBoardPos();
	}

	/**
	 * 
	 * @param x
	 *            x-coordinate of position
	 * @param y
	 *            y-coordinate of position
	 * @return Color of Piece
	 */

	Player getPlayer(int x, int y) {
		return ((Character.isUpperCase(boardPosition.charAt(x * width + y)) == true) ? Player.WHITE
				: Player.BLACK);
	}

	/**
	 * 
	 * @param x
	 *            x-coordinate of position
	 * @param y
	 *            y-coordinate of position
	 * @return true is (x,y) lies on chess board
	 */

	boolean isValidSquare(int x, int y) {
		return (((x >= 0) && (y >= 0) && (x < height) && (y < width)) ? true
				: false);
	}

	boolean isVacantSquare(int x, int y) {
		return (((isValidSquare(x, y)) && (boardPosition.charAt(x * width + y) == '.')) ? true
				: false);
	}

	boolean isOpponentPiece(int x, int y, Player self) {
		return (((isValidSquare(x, y))
				&& (boardPosition.charAt(x * width + y) != '.') && (self != getPlayer(
				x, y))) ? true : false);
	}

	boolean isOwnPiece(int x, int y, Player self) {
		return (((isValidSquare(x, y))
				&& (boardPosition.charAt(x * width + y) != '.') && (self == getPlayer(
				x, y))) ? true : false);
	}

	boolean isVacantOrOpponent(int x, int y, Player self) {
		return ((isVacantSquare(x, y) || isOpponentPiece(x, y, self)) ? true
				: false);
	}

	Pos squareToPos(String M) {
		return new Pos((height - M.charAt(1) + '0'), (Character.toLowerCase(M
				.charAt(0)) - 'a'));
	}

	String posToSquare(int x, int y) {
		return (new StringBuffer((char) ('a' + y))
				.append((char) ('0' + height - x))).toString();
	}

	Move readMove(String M, Player pl) {

		try {

			System.err.println("readMove : " + M + " [ " + pl.name() + " ]");
			Pos P;
			String sq = new String();
			PieceType pt = PieceType.PAWN;

			/* Decipher the notation */
			switch (M.length()) {
				case 2:
					/* Move like e4, e5 ; pawn moves */
					sq = M;
					pt = PieceType.PAWN;
					break;
				case 3:
					if(M.equals("O-O")) { /* King side castling */
						return null;
					} else {
						sq = M.substring(1, 3); 
						pt = PieceType.get(M.charAt(0));
					}
					break;
				default:

			}
				P = squareToPos(sq);

				System.err.println("P: " + (new Integer(P.x)).toString() + " , "
						+ (new Integer(P.y)).toString());

				if (isValidSquare(P.x, P.y)) {
					
					for( Map.Entry<Pos, Piece> entry : chessMen.entrySet() ) {
						Pos pos = entry.getKey();
						Piece piece = entry.getValue();

						System.out.print("Piece : " + (new Integer(pos.x).toString() + " " + (new Integer(pos.y)).toString() ) + " : " );
						System.out.println(piece.type.name() + " " + piece.player.name());
						
						Vector<Pos> VP = piece.type.getVMove().getValidMoves(this, piece);
						
						if(VP == null) continue;
							
						if ( 
							 (piece.player.equals(pl))
							&& /* Is of the same colour ? */
							 (piece.type.equals(pt))
							  /* Is a pawn ? */ 
						) {
							
							for( Pos itr : VP ) {
								System.out.println( (new Integer(itr.x).toString() + " " + (new Integer(itr.y)).toString()) );
								if( (itr.x == P.x) && (itr.y == P.y) ) {
									System.out.println("VALID : => " + (new Integer(itr.x).toString() + " " + (new Integer(itr.y)).toString() ) + " : " );
									return new Move(piece, P);
								}
							}
						}
					} 
					
						
					System.err.println("Could not make a valid move to sqaure "
							+ M + "for player" + pl.name());

				} else {
					System.err.println("Invalid square " + M + "for player "
							+ pl.name());
				}

		} catch (Exception E) {
			E.printStackTrace();
		}

		return null;
	}
	
	void makeMove(Move M) {
		
		if(M == null) return;
		
		System.err.println("src_x :" + (new Integer(M.piece.xPos)).toString());
		System.err.println("src_y :" + (new Integer(M.piece.yPos)).toString());
		System.err.println("dest_x:" + (new Integer(M.pos.x)).toString());
		System.err.println("dest_y:" + (new Integer(M.pos.y)).toString());
		
		if(M.piece != null) {
			
			/* If it is a capture, remove the captured piece */
			Piece Q = chessMen.get(M.pos);
			if(Q != null) { /* capture */
				chessMen.remove(Q);		
			}
			
			boardPosition.setCharAt(M.pos.x * this.width + M.pos.y,  boardPosition.charAt(M.piece.xPos * this.width + M.piece.yPos));
			
			/* Change the position of the piece to be moved */
			
			Pos src = null;
			
			for( Map.Entry<Pos, Piece> entry : chessMen.entrySet()) {
				src = entry.getKey();
				int x = src.x;
				int y = src.y;
				if( (x == M.piece.xPos) && (y == M.piece.yPos)) {
					Piece srcPiece = entry.getValue();
					chessMen.put(new Pos(M.pos.x,M.pos.y), new Piece(srcPiece.pieceID, srcPiece.type, srcPiece.player, M.pos.x, M.pos.y) );
					break;
				}
			}
			
			if(src != null) {
				chessMen.remove(src);
			}
			
			boardPosition.setCharAt(M.piece.xPos * this.width + M.piece.yPos, '.');
				
		}
		
	}

	StringBuilder fenToBoardPosition(String fen) {

		StringBuilder boardPos = new StringBuilder("");
		StringTokenizer st = new StringTokenizer(fen);

		/* Loop through tokens */
		if (st.hasMoreTokens() == false)
			return boardPos;
		int count = 0;
		for (String tok = st.nextToken(); st.hasMoreTokens(); tok = st
				.nextToken(), count++) {
			System.out.print(tok);
		}

		return boardPos;
	}
	
	void handleCastle( Player pl, String side) {
		
		// TODO: handle check and leading to check conditions
		// TODO: handle the condition where the king has already moved 
		
		int rpos, kpos = width / 2, castleWidth;
		int row  = ( ( pl.equals(Player.BLACK) ) ? 0 : (height-1));
		char rook = ( ( pl.equals(Player.BLACK) ) ? 'r' : 'R');
		char king = ( ( pl.equals(Player.BLACK) ) ? 'k' : 'K');
		if( 
				(   (pl.equals(Player.BLACK)) && (side.equalsIgnoreCase("O-O"))    ) 
				 ||
				(   (pl.equals(Player.WHITE)) && (side.equalsIgnoreCase("O-O-O"))  )
		   ) {
			 rpos = 0;
		   } else {
			  rpos = width-1;
		   }
		 castleWidth = ( (side.equalsIgnoreCase("O-O"))? kpos : kpos+1 );
		
		 int ridx = row*width+rpos;
		 int kidx = row*width+kpos;
		 
		 boolean castlePermitted = true;
		 if( boardPosition.charAt(ridx)  != rook ) castlePermitted = false;
		 if( boardPosition.charAt(kidx)  != king ) castlePermitted = false;
		 
		 for( int i = Math.min(ridx,kidx) + 1 ; i < Math.max(ridx,kidx) ; i++ ) {
			 if( boardPosition.charAt(i) != '.' ) {
				 castlePermitted = false;
				 break;
			 }
		 }
		 
		if (castlePermitted) {
			/* Interchange King and the rook */

			/* In boardPosition representation */

			boardPosition.setCharAt(ridx, king);
			boardPosition.setCharAt(kidx, rook);

			/* In chess men representation */

			Pos rookPos = new Pos(row, rpos);
			Pos kingPos = new Pos(row, kpos);
			
			Pos pos = null; Piece piece = null;
			Piece kingPiece = null, rookPiece = null;
			for( Map.Entry<Pos, Piece> entry : chessMen.entrySet() ) {
				pos = entry.getKey();
				piece = entry.getValue();
				if((pos.x == rookPos.x) && (pos.y == rookPos.y)) rookPiece = piece;
				if((pos.x == kingPos.x) && (pos.y == kingPos.y)) kingPiece = piece;
			}

			if ((kingPiece != null) && (rookPiece != null)) {

				Piece kingPieceShifted = new Piece(kingPiece.pieceID,
						PieceType.KING, pl, rookPiece.xPos, rookPiece.yPos);
				Piece rookPieceShifted = new Piece(rookPiece.pieceID,
						PieceType.ROOK, pl, kingPiece.xPos, kingPiece.yPos);

				chessMen.remove(rookPos);
				chessMen.remove(kingPos);

				chessMen.put(rookPos, kingPieceShifted);
				chessMen.put(kingPos, rookPieceShifted);

			} else {
				System.err.println("Could not get pieces for castling");
			}

		}
	}

	StringBuilder parsePGN(Reader in) {
		StringBuilder boardPos = new StringBuilder("");
		StringBuilder moves = new StringBuilder("");
		StringBuilder movesCommentsRemoved = new StringBuilder("");

		BufferedReader br = new BufferedReader(in);
		try {
			String l = new String("");
			for (l = br.readLine(); (l.equals("") == false) && (l != null); l = br
					.readLine()) {
				System.err.print(l.trim() + "\n");
			}

			for (; l.equals("") && (l != null); l = br.readLine())
				; // skip the blank lines

			for (; l != null; l = br.readLine())
				moves.append(l.trim() + " "); // TODO: handle single line
												// comments

			boolean commentFlag = false;
			int L = moves.length();
			for (int i = 0; i < L; i++) {

				if (moves.charAt(i) == '{')
					commentFlag = true;
				else if (moves.charAt(i) == '}')
					commentFlag = false;
				else {
					if (commentFlag == false)
						movesCommentsRemoved.append(moves.charAt(i));
				}

			}

			Player pl = Player.WHITE;
			Vector<Move> whiteMoves = new Vector<Move>();
			Vector<Move> blackMoves = new Vector<Move>();

			StringTokenizer st = new StringTokenizer(
					movesCommentsRemoved.toString());

			for (String tok = st.nextToken(); st.hasMoreTokens(); tok = st
					.nextToken()) {
			
				System.err.println("token: " + tok);
				if (tok.endsWith("...")) {
					// skip
				} else if (tok.endsWith(".")) {
					pl = Player.WHITE;
				} else {

					Move M = readMove(tok, pl);
					
					if (M == null) {
						/* Check for special cases */
						/* Castling */
						if( tok.equalsIgnoreCase("O-O") || tok.equalsIgnoreCase("O-O-O")) {
							handleCastle(pl, tok);
						} else {
							System.err.println("Could not read a valid move !");
						}
						
					} else {
						
						System.err.println("Move Read " +
								(new Integer(M.pos.x)).toString()
										+ (new Integer(M.pos.y)).toString());
						
						makeMove(M);
					}
					
					System.err.println(formatBoardPosition());
					
					if (pl == Player.WHITE) {
							whiteMoves.add(M);
							pl = Player.BLACK;
					} else {
							blackMoves.add(M);
							pl = Player.WHITE;
					}
					 
				}

			}

			this.chessMoves.put(Player.WHITE, whiteMoves);
			this.chessMoves.put(Player.BLACK, blackMoves);

			int sz = whiteMoves.size();

			System.err.print("NumMoves: " + (new Integer(sz)).toString() + ":\n");

			for (int i = 0; i < sz; i++) {
				System.err.print((new Integer(i)).toString() + ":\n");

				System.err.print("Player: "
						+ chessMoves.get(Player.WHITE).get(i).piece.type.name());
				System.err.print("Position: ( "
						+ chessMoves.get(Player.WHITE).get(i).piece.xPos
						+ " , ");
				System.err.print(chessMoves.get(Player.WHITE).get(i).piece.yPos
						+ " )\n");
				System.err.print("PieceID: "
						+ (new Integer(
								chessMoves.get(Player.WHITE).get(i).piece.pieceID))
								.toString() + "\n");
				System.err.print("Move To: ( "
						+ (new Integer(
								chessMoves.get(Player.WHITE).get(i).pos.x))
								.toString() + " , ");
				System.err.print((new Integer(
						chessMoves.get(Player.WHITE).get(i).pos.y)).toString()
						+ " )\n");
				System.err.print("\n");

				System.err.print((new Integer(i)).toString() + ":\n");
				System.err.print("Player: "
						+ chessMoves.get(Player.BLACK).get(i).piece.type.name());
				System.err.print("Position: ( "
						+ chessMoves.get(Player.BLACK).get(i).piece.xPos
						+ " , ");
				System.err.print(chessMoves.get(Player.BLACK).get(i).piece.yPos
						+ " )\n");
				System.err.print("PieceID: "
						+ (new Integer(
								chessMoves.get(Player.BLACK).get(i).piece.pieceID))
								.toString() + "\n");
				System.err.print("Move To: ( "
						+ (new Integer(
								chessMoves.get(Player.BLACK).get(i).pos.x))
								.toString() + " , ");
				System.err.print((new Integer(
						chessMoves.get(Player.BLACK).get(i).pos.y)).toString()
						+ " )\n");
				System.err.print("\n");

				System.err.print("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception Encountered");
		}

		return boardPos;
	}
	
	/* TODO: Remove ; for testing only */
	public static void main(String args[])
	{
		Shatranj S = new Shatranj(ShatranjType.NORMAL);
				
		
		//S.fenToBoardPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		
		S.parsePGN( new StringReader( new String(
"[Event \"F/S Return Match\"]" + "\n" +
"[Site \"Belgrade, Serbia Yugoslavia|JUG\"]" + "\n" +
"[Date \"1992.11.04\"]" + "\n" +
"[Round \"29\"]" + "\n" +
"[White \"Fischer, Robert J.\"]" + "\n" +
"[Black \"Spassky, Boris V.\"]" + "\n" +
"[Result \"1/2-1/2\"]" + "\n" +
"" + "\n" +
"1. e4 e5 2. Nf3 Nc6 3. Bb5 {This opening is called the Ruy Lopez.} 3... a6" + "\n" + 
"4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8  10. d4 Nbd7" + "\n" + 
"11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15. Nb1 h6 16. Bh4 c5 17. dxe5" + "\n" + 
"Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21. Nc4 Nxc4 22. Bxc4 Nb6" + "\n" + 
"23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7 27. Qe3 Qg5 28. Qxg5"+ "\n" + 
"hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33. f3 Bc8 34. Kf2 Bf5" + "\n" +
"35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5 40. Rd6 Kc5 41. Ra6" + "\n" +
"Nf2 42. g4 Bd3 43. Re6 1/2-1/2" 
)) );
		
		
	
	
	}

}
