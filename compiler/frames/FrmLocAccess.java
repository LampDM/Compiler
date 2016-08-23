
package compiler.frames;

import compiler.abstr.tree.*;
import compiler.seman.*;
import compiler.seman.type.*;

/**
 * Dostop do lokalne spremenljivke.
 * 
 * @author sliva
 */
public class FrmLocAccess extends FrmAccess {

	/** Opis spremenljivke.  */
	public final AbsVarDef var;

	/** Klicni zapis funkcije, v kateri je spremenljivka deklarirana.  */
	public final FrmFrame frame;

	/** Odmik od FPja.  */
	public final int offset;

	/** Ustvari nov dostop do lokalne spremenljivke.
	 * 
	 * @param var Lokalna spremenljivka.
	 * @param frame Klicni zapis.
	 */
	public FrmLocAccess(AbsVarDef var, FrmFrame frame) {
		this.var = var;
		this.frame = frame;
		
		SemType type = SymbDesc.getType(this.var).actualType();
		this.offset = 0 - frame.sizeLocs - type.size();
		frame.sizeLocs = frame.sizeLocs + type.size();
	}

	@Override
	public String toString() {
		return "LOC(" + var.name + ": offset=" + offset + ")";
	}
	
}
