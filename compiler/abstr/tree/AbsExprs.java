package compiler.abstr.tree;

import java.util.*;

import compiler.*;
import compiler.abstr.*;

/**
 * Opis izrazov.
 * 
 * @author sliva
 */
public class AbsExprs extends AbsExpr {

	/** Seznam izrazov. */
	private final AbsExpr exprs[];

	/**
	 * Ustvari nov opis izrazov.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param exprs
	 *            Seznam izrazov.
	 */
	public AbsExprs(Position pos, Vector<AbsExpr> exprs) {
		super(pos);
		this.exprs = new AbsExpr[exprs.size()];
		for (int expr = 0; expr < exprs.size(); expr++)
			this.exprs[expr] = exprs.elementAt(expr);
	}

	/**
	 * Vrne izbrani izraz.
	 * 
	 * @param index
	 *            Indeks izraza.
	 * @return Izraz na izbranem mestu.
	 */
	public AbsExpr expr(int index) {
		return exprs[index];
	}

	/**
	 * Vrne stevilo izrazov.
	 * 
	 * @return Stevilo izrazov.
	 */
	public int numExprs() {
		return exprs.length;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
