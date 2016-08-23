package compiler;

/**
 * Doloca polozaj dela izvornega besedila v izvorni datoteki.
 * 
 * @author sliva
 */
public class Position implements java.io.Serializable {

	/** Vrstica zacetka dela besedila. */
	private final int begLine;
	/** Stolpec zacetka dela besedila. */
	private final int begColumn;

	/** Vrstica konca dela besedila. */
	private final int endLine;
	/** Stolpec konca dela besedila. */
	private final int endColumn;

	/**
	 * Ustvari nov polozaj dela izvornega besedila.
	 * 
	 * @param begLine
	 *            Vrstica zacetka dela besedila.
	 * @param begColumn
	 *            Stolpec zacetka dela besedila.
	 * @param endLine
	 *            Vrstica konca dela besedila.
	 * @param endColumn
	 *            Stolpec konca dela besedila.
	 */
	public Position(int begLine, int begColumn, int endLine, int endColumn) {
		this.begLine = begLine;
		this.begColumn = begColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	/**
	 * Ustvari not polozaj znaka izvornega besedila.
	 * 
	 * @param line
	 *            Vrstica znaka v izvornem besedilu.
	 * @param column
	 *            Stolpec znaka v izvornem besedilu.
	 */
	public Position(int line, int column) {
		this(line, column, line, column);
	}

	/**
	 * Ustvari nov polozaj dela izvornega besedila na osnovi polozaja prvega in
	 * zadnjega dela izvornega besedila.
	 * 
	 * @param begPos
	 *            Polozaj prvega dela izvornega besedila.
	 * @param endPos
	 *            Polozaj zadnjega dela izvornega besedila.
	 */
	public Position(Position begPos, Position endPos) {
		this.begLine = begPos.begLine;
		this.begColumn = begPos.begColumn;
		this.endLine = endPos.endLine;
		this.endColumn = endPos.endColumn;
	}

	@Override
	public String toString() {
		return (begLine + ":" + begColumn + "-" + endLine + ":" + endColumn);
	}

}
