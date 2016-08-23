package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Opis imena tipa.
 * 
 * @author sliva
 */
public class AbsTypeName extends AbsType {

	/** Ime tipa. */
	public final String name;

	/**
	 * Ustvari nov opis imena tipa.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime tipa.
	 */
	public AbsTypeName(Position pos, String name) {
		super(pos);
		this.name = name;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
