package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Opis izraza z definicijami.
 * 
 * @author sliva
 */
public class AbsWhere extends AbsExpr {

	/** Izraz. */
	public final AbsExpr expr;
	
	/** Definicije v izrazu. */
	public final AbsDefs defs;
	
	/**
	 * Ustvari nov izraz z definicijami.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param expr
	 *            Izraz.
	 * @param defs
	 *            Definicije v izrazu.
	 */
	public AbsWhere(Position pos, AbsExpr expr, AbsDefs defs) {
		super(pos);
		this.expr = expr;
		this.defs = defs;
	}
	
	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
