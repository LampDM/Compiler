package compiler.lexan;

import compiler.*;

/**
 * Leksikalni simbol.
 * 
 * @author sliva
 */
public class Symbol {
	
	/** Vrsta simbola. */
	public final int token;

	/** Znakovna predstavitev simbola. */
	public final String lexeme;
	
	/** Polozaj simbola v izvorni datoteki. */
	public final Position position;

	/**
	 * Ustvari nov leksikalni simbol.
	 * 
	 * @param token
	 *            Vrsta simbola.
	 * @param lexeme
	 *            Znakovna predstavitev simbola.
	 * @param begLine
	 *            Vrstca zacetka simbola v izvorni datoteki.
	 * @param begColumn
	 *            Stolpec zacetka simbola v izvorni datoteki.
	 * @param endLine
	 *            Vrstica konca simbola v izvorni datoteki.
	 * @param endColumn
	 *            Stolpec konca simbola v izvorni datoteki.
	 */
	public Symbol(int token, String lexeme, int begLine, int begColumn, int endLine, int endColumn) {
		this.token = token;
		this.lexeme = lexeme;
		this.position = new Position(begLine, begColumn, endLine, endColumn);
	}

	/**
	 * Ustvari nov leksikalni simbol.
	 * 
	 * @param token
	 *            Vrsta simbola.
	 * @param lexeme
	 *            Znakovna predstavitev simbola.
	 * @param position
	 *            Polozaj simbola v izvorni datoteki.
	 */
	public Symbol(int token, String lexeme, Position position) {
		this.token = token;
		this.lexeme = lexeme;
		this.position = position;
	}

	@Override
	public String toString() {
		String tokenName = "";
		switch (token) {

		case Token.EOF       : tokenName = "EOF"       ; break;
		
		case Token.IDENTIFIER: tokenName = "IDENTIFIER"; break;

		case Token.LOG_CONST : tokenName = "LOG_CONST" ; break;
		case Token.INT_CONST : tokenName = "INT_CONST" ; break;
		case Token.STR_CONST : tokenName = "STR_CONST" ; break;

		case Token.AND       : tokenName = "AND"       ; break;
		case Token.IOR       : tokenName = "IOR"       ; break;
		case Token.NOT       : tokenName = "NOT"       ; break;
		
		case Token.EQU       : tokenName = "EQU"       ; break;
		case Token.NEQ       : tokenName = "NEQ"       ; break;
		case Token.LTH       : tokenName = "LTH"       ; break;
		case Token.GTH       : tokenName = "GTH"       ; break;
		case Token.LEQ       : tokenName = "LEQ"       ; break;
		case Token.GEQ       : tokenName = "GEQ"       ; break;
		
		case Token.MUL       : tokenName = "MUL"       ; break;
		case Token.DIV       : tokenName = "DIV"       ; break;
		case Token.MOD       : tokenName = "MOD"       ; break;
		case Token.ADD       : tokenName = "ADD"       ; break;
		case Token.SUB       : tokenName = "SUB"       ; break;
		
		
		case Token.LPARENT   : tokenName = "LPARENT"   ; break;
		case Token.RPARENT   : tokenName = "RPARENT"   ; break;
		case Token.LBRACKET  : tokenName = "LBRACKET"  ; break;
		case Token.RBRACKET  : tokenName = "RBRACKET"  ; break;
		case Token.LBRACE    : tokenName = "LBRACE"    ; break;
		case Token.RBRACE    : tokenName = "RBRACE"    ; break;
		
		case Token.DOT	     : tokenName = "DOT"       ; break;
		case Token.COLON     : tokenName = "COLON"     ; break;
		case Token.SEMIC     : tokenName = "SEMIC"     ; break;
		case Token.COMMA     : tokenName = "COMMA"     ; break;
		
		case Token.ASSIGN    : tokenName = "ASSIGN"    ; break;
		
		case Token.LOGICAL   : tokenName = "LOGICAL"   ; break;
		case Token.INTEGER   : tokenName = "INTEGER"   ; break;
		case Token.STRING    : tokenName = "STRING"    ; break;
		
		case Token.KW_ARR    : tokenName = "ARR"       ; break;
		case Token.KW_ELSE   : tokenName = "ELSE"      ; break;
		case Token.KW_FOR    : tokenName = "FOR"       ; break;
		case Token.KW_FUN    : tokenName = "FUN"       ; break;
		case Token.KW_IF     : tokenName = "IF"        ; break;
		case Token.KW_THEN   : tokenName = "THEN"      ; break;
		case Token.KW_TYP    : tokenName = "TYP"       ; break;
		case Token.KW_VAR    : tokenName = "VAR"       ; break;
		case Token.KW_WHERE  : tokenName = "WHERE"     ; break;
		case Token.KW_WHILE  : tokenName = "WHILE"     ; break;

		default:
			Report.error("Internal error: token=" + token + " in compiler.lexan.Symbol.toString().");
		}
		return tokenName + ":" + lexeme;
	}

}
