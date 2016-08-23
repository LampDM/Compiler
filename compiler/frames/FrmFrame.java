package compiler.frames;

import java.util.*;

import compiler.abstr.tree.*;

/**
 * Klicni zapis funkcije.
 * 
 * @author sliva
 */
public class FrmFrame implements java.io.Serializable {

	/** Opis funckije.  */
	public AbsFunDef fun;

	/** Staticni nivo funkcije.  */
	public int level;

	/** Vstopna labela.  */
	public FrmLabel label;

	/** Stevilo parametrov.  */
	public int numPars;
	
	/** Velikost bloka parametrov.  */
	public int sizePars;

	/** Lokalne spremenljivke podprograma.  */
	LinkedList<FrmLocAccess> locVars;

	/** Velikost bloka lokalnih spremenljivk.  */
	public int sizeLocs;

	/** Velikost bloka za oldFP in retAddr.  */
	public int sizeFPRA;

	/** Velikost bloka zacasnih spremenljivk.  */
	public int sizeTmps;

	/** Velikost bloka registrov.  */
	public int sizeRegs;

	/** Velikost izhodnih argumentov.  */
	public int sizeArgs;

	/** Kazalec FP.  */
	public FrmTemp FP;

	/** Spremenljivka z rezultatom funkcije.  */
	public FrmTemp RV;

	/**
	 * Ustvari nov klicni zapis funkcije.
	 * 
	 * @param fun Funkcija.
	 * @param level Staticni nivo funkcije.
	 */
	public FrmFrame(AbsFunDef fun, int level) {
		this.fun = fun;
		this.level = level;
		///this.label = (level == 1 ? FrmLabel.newLabel(fun.name) : FrmLabel.newLabel());
                this.label= FrmLabel.newLabel(fun.name);
		this.numPars = 0;
		this.sizePars = 4;
		this.locVars = new LinkedList<FrmLocAccess> ();
		this.sizeLocs = 0;
		this.sizeFPRA = 8;
		this.sizeTmps = 0;
		this.sizeRegs = 0;
		this.sizeArgs = 0;
		FP = new FrmTemp();
		RV = new FrmTemp();
	}

	/** Velikost klicnega zapisa.  */
	public int size() {
		return sizeLocs + sizeFPRA + sizeTmps + sizeRegs + sizeArgs;
	}

	@Override
	public String toString() {
		return ("FRAME(" + fun.name + ": " +
					"level=" + level + "," +
					"label=" + label.name() + "," +
					"sizeLocs=" + sizeLocs + "," +
					"sizeArgs=" + sizeArgs + "," +
					"size=" + size() + "," +
					"FP=" + FP.name() + "," +
					"RV=" + RV.name() + ")");
	}
	
}
