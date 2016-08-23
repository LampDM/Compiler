package compiler.frames;

import compiler.abstr.tree.*;

/**
 * Dostop do globalne spremenljivke.
 * 
 * @author sliva
 */

public class FrmVarAccess extends FrmAccess {

	/** Opis spremenljivke.  */
	public final AbsVarDef var;

	/** Labela spremenljivke.  */
	public final FrmLabel label;

	/**
	 * Ustvari nov dostop do globalne spremenljivke.
	 * 
	 * @param var Globalna spremenljivka.
	 */
	public FrmVarAccess(AbsVarDef var) {
		this.var = var;
		label = FrmLabel.newLabel(var.name);
	}

	@Override
	public String toString() {
		return "VAR(" + var.name + ": label=" + label.name() + ")";
	}
	
}
