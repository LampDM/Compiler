package compiler.imcode;

import compiler.*;
import compiler.frames.*;

/**
 * Ime.
 * 
 * @author sliva
 */
public class ImcNAME extends ImcExpr {

	/** Labela imenovane lokacije.  */
	public FrmLabel label;

	/**
	 * Ustvari novo ime.
	 * 
	 * @param label Labela.
	 */
	public ImcNAME(FrmLabel label) {
		this.label = label;
	}

	@Override
	public void dump(int indent) {
		Report.dump(indent, "NAME label=" + label.name());
	}

	@Override
	public ImcESEQ linear() {
		return new ImcESEQ(new ImcSEQ(), this);
	}

}
