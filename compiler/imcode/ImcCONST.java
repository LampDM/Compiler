package compiler.imcode;

import compiler.*;

/**
 * Konstanta.
 * 
 * @author sliva
 */
public class ImcCONST extends ImcExpr {

	/** Vrednost.  */
	public Integer value;

	/**
	 * Ustvari novo konstanto.
	 * 
	 * @param value Vrednost konstante.
	 */
	public ImcCONST(Integer value) {
		this.value = value;
	}

	@Override
	public void dump(int indent) {
		Report.dump(indent, "CONST value=" + value.toString());
	}

	@Override
	public ImcESEQ linear() {
		return new ImcESEQ(new ImcSEQ(), this);
	}

}
