package compiler.frames;

import compiler.abstr.tree.*;
import compiler.seman.*;
import compiler.seman.type.*;

/**
 * Dostop do argumenta funkcije.
 * 
 * @author sliva
 */
public class FrmParAccess extends FrmAccess {

	/** Opis argumenta.  */
	public AbsPar par;

	/** Klicni zapis funkcije, v kateri je parameter deklariran.  */
	public FrmFrame frame;

	/** Odmik od FPja.  */
	public int offset;

	/** Ustvari nov dostop do parametra.
	 * 
	 * @param par Parameter.
	 * @param frame Klicni zapis.
	 */
        
	public FrmParAccess(AbsPar par, FrmFrame frame) {
		this.par = par;
		this.frame = frame;
		
		SemType type = SymbDesc.getType(this.par).actualType();
		this.offset = 0 + frame.sizePars;
		frame.sizePars = frame.sizePars + type.size();
		frame.numPars++;
	}

	@Override
	public String toString() {
		return "PAR(" + par.name + ": offset=" + offset + ")";
	}
	
}
