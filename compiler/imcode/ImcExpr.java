package compiler.imcode;

/**
 * Izrazi vmesne kode.
 * 
 * @author sliva
 */
public abstract class ImcExpr extends ImcCode {

	/**
	 * Vrne linearizirano vmesno kodo izraza.
	 * 
	 * @return Linearizirana vmesna koda izraza.
	 */
	public abstract ImcESEQ linear();

}
