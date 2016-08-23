package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Ime spremenljivke v izrazu.
 * 
 * @author sliva
 */
public class AbsVarName extends AbsExpr {
	
	/** Ime spremenljivke. */
	public final String name;

	/**
	 * Ustvari nov opis imena spremenljivke v izrazu.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime spremenljivke.
	 */
	public AbsVarName(Position pos, String name) {
		super(pos);
		this.name = name;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}