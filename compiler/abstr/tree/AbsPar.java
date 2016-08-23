package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Opis parametra funckije.
 * 
 * @author sliva
 *
 */
public class AbsPar extends AbsDef {

	/** Ime parametra. */
	public final String name;

	/** Tip parametra. */
	public final AbsType type;

	/**
	 * Ustvari nov opis parametra.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime parametra.
	 * @param type
	 *            Tip parametra.
	 */
	public AbsPar(Position pos, String name, AbsType type) {
		super(pos);
		this.name = name;
		this.type = type;
	}
	
	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
