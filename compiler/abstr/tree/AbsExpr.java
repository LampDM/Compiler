package compiler.abstr.tree;

import compiler.*;

/**
 * Izraz.
 * 
 * @author sliva
 */
public abstract class AbsExpr extends AbsTree {

	/**
	 * Ustvari nov izraz.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 */
	public AbsExpr(Position pos) {
		super(pos);
	}

}
