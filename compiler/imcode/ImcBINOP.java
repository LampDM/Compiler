package compiler.imcode;

import compiler.*;

/**
 * Binarna operacija.
 * 
 * @author sliva
 */
public class ImcBINOP extends ImcExpr {

    
    
	public static final int IOR = 0;
	public static final int AND = 1;//Logicals
        
	public static final int EQU = 2;//Integer
	public static final int NEQ = 3;
	public static final int LEQ = 4;
	public static final int GEQ = 5;
	public static final int LTH = 6;
	public static final int GTH = 7;
        
	public static final int ADD = 8;
	public static final int SUB = 9;
	public static final int MUL = 10;
	public static final int DIV = 11;
	public static final int MOD = 12;
        


	/** Operator.  */
	public int op;

	/** Koda levega podizraza.  */
	public ImcExpr limc;

	/** Koda desnega podizraza.  */
	public ImcExpr rimc;

	/**
	 * Ustvari novo binarno operacijo.
	 * 
	 * @param op Operator.
	 * @param limc Levi podizraz.
	 * @param rimc Desni podizraz.
	 */
	public ImcBINOP(int op, ImcExpr limc, ImcExpr rimc) {
		this.op = op;
		this.limc = limc;
		this.rimc = rimc;
	}

	@Override
	public void dump(int indent) {
		String op = null;
		switch (this.op) {
		case ADD: op = "+" ; break;
		case SUB: op = "-" ; break;
		case MUL: op = "*" ; break;
		case DIV: op = "/" ; break;
		case EQU: op = "=="; break;
		case NEQ: op = "!="; break;
		case LTH: op = "<" ; break;
		case GTH: op = ">" ; break;
		case LEQ: op = "<="; break;
		case GEQ: op = ">="; break;
		case AND: op = "&" ; break;
		case IOR : op = "|" ; break;
		}
		Report.dump(indent, "BINOP op=" + op);
		limc.dump(indent + 2);
		rimc.dump(indent + 2);
	}

	@Override
	public ImcESEQ linear() {
		ImcESEQ limc = this.limc.linear();
		ImcESEQ rimc = this.rimc.linear();
		ImcSEQ stmt = new ImcSEQ();
		stmt.stmts.addAll(((ImcSEQ)limc.stmt).stmts);
		stmt.stmts.addAll(((ImcSEQ)rimc.stmt).stmts);
		ImcESEQ lin = new ImcESEQ(stmt, new ImcBINOP(op, limc.expr, rimc.expr));
		return lin;
	}

}
