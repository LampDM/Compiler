package compiler.seman.type;

import compiler.*;

/**
 * Opis atomarnih podatkovnih tipov.
 * 
 * @author sliva
 */
public class SemAtomType extends SemType {

	public static final int LOG = 0;
	public static final int INT = 1;
	public static final int STR = 2;
	public static final int VOID = 3;

	/* Tip. */
	public final int type;

	/**
	 * Ustvari nov opis atomarnega tipa.
	 * 
	 * @param type
	 *            Atomarni tip.
	 */
	public SemAtomType(int type) {
		this.type = type;
	}

	@Override
	public boolean sameStructureAs(SemType type) {
		if (type.actualType() instanceof SemAtomType) {
			SemAtomType atomType = (SemAtomType) (type.actualType());
			return this.type == atomType.type;
		} else
			return false;
	}

	@Override
	public String toString() {
		switch (type) {
		case LOG: return "LOGICAL";
		case INT: return "INTEGER";
		case STR: return "STRING";
		case VOID: return "VOID";
		}
		Report.error("Internal error :: compiler.seman.type.SemAtomType.toString()");
		return "";
	}

    @Override
    public int size() {
        return 4;
    }
}
