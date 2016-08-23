package compiler.abstr.tree;

import java.util.*;

import compiler.*;
import compiler.abstr.*;

/**
 * Klic funkcije.
 * 
 * @author sliva
 */
public class AbsFunCall extends AbsExpr {
	
	/** Ime funkcije. */
	public final String name;
	
	/** Argumenti funkcije. */
	private final AbsExpr args[];

	/**
	 * Ustvari nov opis klica funkcije.
	 * 
	 * @param pos
	 *            Polozaj stavcne oblike tega drevesa.
	 * @param name
	 *            Ime funkcije.
	 * @param args
	 *            Argumenti funkcije.
	 */
	public AbsFunCall(Position pos, String name, Vector<AbsExpr> args) {
		super(pos);
		this.name = name;
		this.args = new AbsExpr[args.size()];
		for (int arg = 0; arg < args.size(); arg++)
			this.args[arg] = args.elementAt(arg);
	}

	/**
	 * Vrne izbrani argument.
	 * 
	 * @param index
	 *            Indeks argumenta.
	 * @return Argument na izbranem mestu.
	 */
	public AbsExpr arg(int index) {
		return args[index];
	}

	/**
	 * Vrne stevilo argumentov.
	 * 
	 * @return Stevilo argumentov.
	 */
	public int numArgs() {
		return args.length;
	}

	@Override public void accept(Visitor visitor) { visitor.visit(this); }

}