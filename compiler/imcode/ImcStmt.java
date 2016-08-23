package compiler.imcode;

/**
 * Stavki vmesne kode.
 * 
 * @author sliva
 */
public abstract class ImcStmt extends ImcCode {

	/**
	 * Vrne linearizirano vmesno kodo stavka.
	 * 
	 * @return linearizirana vmesna koda stavka.
	 */
	public abstract ImcSEQ linear();

}
