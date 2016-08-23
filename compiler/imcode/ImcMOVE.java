package compiler.imcode;

import compiler.*;

/**
 * Prenos.
 * 
 * @author sliva
 */
public class ImcMOVE extends ImcStmt {

	/** Ponor.  */
	public ImcExpr dst;

	/** Izvor.  */
	public ImcExpr src;

	/** Ustvari nov prenos.
	 * 
	 * @param dst Ponor.
	 * @param src Izvor.
	 */
	public ImcMOVE(ImcExpr dst, ImcExpr src) {
		this.dst = dst;
		this.src = src;
	}

	@Override
	public void dump(int indent) {
		Report.dump(indent, "MOVE");
		dst.dump(indent + 2);
		src.dump(indent + 2);
	}

	@Override
	public ImcSEQ linear() {
		ImcSEQ lin = new ImcSEQ();
		ImcESEQ dst = this.dst.linear();
		ImcESEQ src = this.src.linear();
		lin.stmts.addAll(((ImcSEQ)dst.stmt).stmts);
		lin.stmts.addAll(((ImcSEQ)src.stmt).stmts);
		lin.stmts.add(new ImcMOVE(dst.expr, src.expr));
		return lin;
	}

}
