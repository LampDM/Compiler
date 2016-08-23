package compiler.abstr.tree;

import compiler.*;
import compiler.abstr.*;

/**
 * Opis tabela.
 * 
 * @author sliva
 */
public class AbsArrType extends AbsType {

	/** Dolzina tabele. */
	public final int length;

	/** Tip elementa tabele. */
	public final AbsType type;

	/**
	 * Ustvari nov opis tabele.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param length
	 *            Dolzina tabele.
	 * @param type
	 *            Tip elementa tabele.
	 */
	public AbsArrType(Position pos, int length, AbsType type) {
		super(pos);
		this.length = length;
		this.type = type;
	}
	
	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
