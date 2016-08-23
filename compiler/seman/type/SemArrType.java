package compiler.seman.type;

/**
 * Opis tabelaricnega tipa.
 * 
 * @author sliva
 */
public class SemArrType extends SemType {

	/** Tip elementa. */
	public final SemType type;

	/** Velikost tabele. */
	public final int size;

	/**
	 * Ustvari nov opis tabelaricnega tipa.
	 * 
	 * @param type
	 *            Tip elementa tabele.
	 * @param size
	 *            Velikost tabele.
	 */
	public SemArrType(int size, SemType type) {
		this.type = type;
		this.size = size;
	}

	@Override
	public boolean sameStructureAs(SemType type) {
		if (type.actualType() instanceof SemArrType) {
			SemArrType arrayType = (SemArrType) (type.actualType());
			return (arrayType.size == size)
					&& (arrayType.type.sameStructureAs(this.type));
		} else
			return false;
	}

	@Override
	public String toString() {
		return "ARR(" + size + "," + type.toString() + ")";
	}

    @Override
    public int size() {
        return 4*size;
    }
}
