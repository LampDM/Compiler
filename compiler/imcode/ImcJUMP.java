package compiler.imcode;

import compiler.*;
import compiler.frames.*;

/**
 * Brezpogojni skok.
 * 
 * @author sliva
 */
public class ImcJUMP extends ImcStmt {

	/** Labela skoka.  */
	public FrmLabel label;

	/** Ustvari brezpogojni skok. */
	public ImcJUMP(FrmLabel label) {
		this.label = label;
	}

	@Override
	public void dump(int indent) {
		Report.dump(indent, "JUMP label=" + label.name());
	}

	@Override
	public ImcSEQ linear() {
		ImcSEQ lin = new ImcSEQ();
		lin.stmts.add(this);
		return lin;
	}

}
