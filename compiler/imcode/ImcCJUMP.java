package compiler.imcode;

import compiler.*;
import compiler.frames.*;

/**
 * Pogojni skok.
 * 
 * @author sliva
 */
public class ImcCJUMP extends ImcStmt {

	/** Koda pogoja.  */
	public ImcExpr cond;

	/** Labela skoka, ce je pogoj izpolnjen.  */
	public FrmLabel trueLabel;

	/** Lanbela skoka, ce pogoj ni izpolnjen. */
	public FrmLabel falseLabel;

	/**
	 * Ustvari nov pogojni skok.
	 * 
	 * @param cond Pogoj.
	 * @param trueLabel Labela skoka, ce je pogoj izpolnjen.
	 * @param falseLabel Labela skoka, ce pogoj ni izpolnjen.
	 */
	public ImcCJUMP(ImcExpr cond, FrmLabel trueLabel, FrmLabel falseLabel) {
		this.cond = cond;
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
	}

	@Override
	public void dump(int indent) {
		Report.dump(indent, "CJUMP labels=" + trueLabel.name() + "," + falseLabel.name());
		cond.dump(indent + 2);
	}

	@Override
	public ImcSEQ linear() {
		ImcSEQ lin = new ImcSEQ();
		ImcESEQ linCond = cond.linear();
		FrmLabel newFalseLabel = FrmLabel.newLabel();
		lin.stmts.addAll(((ImcSEQ)linCond.stmt).stmts);
		lin.stmts.add(new ImcCJUMP(linCond.expr, trueLabel, newFalseLabel));
		lin.stmts.add(new ImcLABEL(newFalseLabel));
		lin.stmts.add(new ImcJUMP(falseLabel));
		return lin;
	}

}
