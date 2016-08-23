package compiler.seman.type;

import compiler.*;

/**
 * Opis imena tipa.
 * 
 * @author sliva
 */
public class SemTypeName extends SemType {

	/** Ime tipa. */
	public final String name;

	/** Opis tipa. */
	private SemType type;

	/**
	 * Ustvari nov opis imena tipa.
	 * 
	 * @param name
	 *            Ime tipa.
	 */
	public SemTypeName(String name) {
		this.name = name;
		this.type = null;
	}

	/**
	 * Doloci dejanski tipa.
	 * 
	 * @param type
	 *            Tip.
	 */
	public void setType(SemType type) {
		if (this.type == null)
			this.type = type;
		else
			Report.error("Internal error :: compiler.seman.type.SemTypeName.setType(SemType type)");
	}

	/**
	 * Vrne dejanski tip.
	 * 
	 * @return Dejanski tip.
	 */
	public SemType getType() {
		if (this.type != null)
			return this.type;
		else {
			Report.error("Internal error :: compiler.seman.type.SemTypeName.getType()");
			return null;
		}
	}

	@Override
	public SemType actualType() {
		return type.actualType();
	}

	@Override
	public boolean sameStructureAs(SemType type) {
		return this.actualType().sameStructureAs(type.actualType());
	}

	@Override
	public String toString() {
		return "TYP(" + name + ":" + (type == null ? "" : type.toString())
				+ ")";
	}

    @Override
    public int size() {
         return 4;
    }

}
