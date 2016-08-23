package compiler.abstr.tree;

import java.util.*;

import compiler.*;
import compiler.abstr.*;

/**
 * Definicija funckije.
 * 
 * @author sliva
 */
public class AbsFunDef extends AbsDef {

	/** Ime funkcije. */
	public final String name;
	
	/** Seznam parametrov. */
	private final AbsPar pars[];

	/** Opis tipa rezultata funkcije. */
	public final AbsType type;
	
	/** Jedro funkcije. */
	public final AbsExpr expr;

	/**
	 * Ustvari novo definicijo funckije.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime funkcije.
	 * @param pars
	 *            Seznam parametrov.
	 * @param type
	 *            Opis tipa rezultata funkcije.
	 * @param expr
	 *            Jedro funkcije.
	 */
	public AbsFunDef(Position pos, String name, Vector<AbsPar> pars, AbsType type, AbsExpr expr) {
		super(pos);
		this.name = name;
		this.pars = new AbsPar[pars.size()];
		for (int par = 0; par < pars.size(); par++)
			this.pars[par] = pars.elementAt(par);
		this.type = type;
		this.expr = expr;
	}

	/**
	 * Vrne izbrani parameter.
	 * 
	 * @param index
	 *            Indeks parametra.
	 * @return Parameter na izbranem mestu.
	 */
	public AbsPar par(int index) {
		return pars[index];
	}

	/**
	 * Vrne stevilo parametrov funkcije.
	 * 
	 * @return Stevilo parametrov funkcije.
	 */
	public int numPars() {
		return pars.length;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}
