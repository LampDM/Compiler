package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Definicija spremenljivke.
 * 
 * @author sliva
 */
public class AbsVarDef extends AbsDef {

	/** Ime spremenljivke. */
	public final String name;

	/** Opis tipa spremenljivke. */
	public final AbsType type;

	/**
	 * Ustvari novo definicijo spremenljivke.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime spremenljivke.
	 * @param type
	 *            Opis tipa spremenljivke.
	 */
	public AbsVarDef(Position pos, String name, AbsType type) {
		super(pos);
		this.name = name;
		this.type = type;
	}


	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
