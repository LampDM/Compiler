package compiler.abstr.tree;

import compiler.*;

/**
 * Opis podatkovnega tipa.
 * 
 * @author sliva
 */
public abstract class AbsType extends AbsTree {

	/**
	 * Ustvari nov opis podatkovnega tipa.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 */
	public AbsType(Position pos) {
		super(pos);
	}

}
