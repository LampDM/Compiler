package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Zanka z eksplicitnim stevcem.
 * 
 * @author sliva
 */
public class AbsFor extends AbsExpr {
	
	/** Stevec. */
	public final AbsVarName count;
	
	/** Spodnja meja. */
	public final AbsExpr lo;
	
	/** Zgornja meja. */
	public final AbsExpr hi;
	
	/** Korak. */
	public final AbsExpr step;
	
	/** Jedro stavka. */
	public final AbsExpr body;
	
	/**
	 * Ustvari zanko z eksplicitnim stevcem.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param count
	 *            Stevec.
	 * @param lo
	 *            Spodnja meja.
	 * @param hi
	 *            Zgornja meja.
	 * @param step
	 *            Korak.
	 * @param body
	 *            Jedro zanke.
	 */
	public AbsFor(Position pos, AbsVarName count, AbsExpr lo, AbsExpr hi, AbsExpr step, AbsExpr body) {
		super(pos);
		this.count = count;
		this.lo = lo;
		this.hi = hi;
		this.step = step;
		this.body = body;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
