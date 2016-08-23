package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Kratki pogojni stavek.
 * 
 * @author sliva
 */
public class AbsIfThen extends AbsExpr {
	
	/** Pogoj. */
	public final AbsExpr cond;
	
	/** Pozitivna veja. */
	public final AbsExpr thenBody;
		
	/**
	 * Ustvari nov kratki pogojni stavek.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param cond
	 *            Pogoj.
	 * @param thenBody
	 *            Pozitivna veja.
	 */
	public AbsIfThen(Position pos, AbsExpr cond, AbsExpr thenBody) {
		super(pos);
		this.cond = cond;
		this.thenBody = thenBody;		
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
