 package compiler.abstr.tree;

import java.util.*;

import compiler.*;
import compiler.abstr.*;

/**
 * Seznam definicij.
 * 
 * @author sliva
 */
public class AbsDefs extends AbsTree {

	/** Elementi seznama definicij. */
	private AbsDef defs[];

	/**
	 * Ustvari nov seznam definicij.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param defs
	 *            Definicije.
	 */
	public AbsDefs(Position pos, Vector<AbsDef> defs) {
		super(pos);
		this.defs = new AbsDef[defs.size()];
		for (int def = 0; def < defs.size(); def++)
			this.defs[def] = defs.elementAt(def);
	}

	/**
	 * Vrne izbrano definicijo.
	 * 
	 * @param index
	 *            Indeks definicije.
	 * @return Definicija na izbranem mestu v seznamu.
	 */
	public AbsDef def(int index) {
		return defs[index];
	}

	/**
	 * Vrne stevilo definicij v seznamu.
	 * 
	 * @return Stevilo definicij v seznamu.
	 */
	public int numDefs() {
		return defs.length;
	}
	
	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
