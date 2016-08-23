package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Definicija tipa.
 * 
 * @author sliva
 */
public class AbsTypeDef extends AbsDef {

	/** Ime tipa. */
	public final String name;

	/** Opis tipa. */
	public final AbsType type;

	/**
	 * Ustvari novo definicijo tipa.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime tipa.
	 * @param type
	 *            Opis tipa.
	 */
	public AbsTypeDef(Position pos, String name, AbsType type) {
		super(pos);
		this.name = name;
		this.type = type;
	}
	
	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
