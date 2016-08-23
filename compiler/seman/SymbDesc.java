package compiler.seman;

import java.util.*;

import compiler.abstr.tree.*;
import compiler.seman.type.*;

/**
 * Opisi posameznih definicij.
 * 
 * @author sliva
 */
public class SymbDesc {

	/** Nivo vidnosti. */
	private static HashMap<AbsTree, Integer> scope = new HashMap<AbsTree, Integer>();

	/**
	 * Doloci globino nivoja vidnosti za dano definicijo imena.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @param nodeScope
	 *            Globina nivoja vidnosti.
	 */
	public static void setScope(AbsTree node, int nodeScope) {
		scope.put(node, new Integer(nodeScope));
	}

	/**
	 * Vrne globino nivoja vidnosti za dano definicijo imena.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @return Globina nivoja vidnosti.
	 */
	public static Integer getScope(AbsTree node) {
		Integer nodeScope = scope.get(node);
		return nodeScope;
	}

	/** Definicija imena. */
	private static HashMap<AbsTree, AbsDef> nameDef = new HashMap<AbsTree, AbsDef>();

	/**
	 * Poveze vozlisce drevesa z definicijo imena.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @param def
	 *            Definicija imena.
	 */
	public static void setNameDef(AbsTree node, AbsDef def) {
		nameDef.put(node, def);
	}

	/**
	 * Vrne definicijo imena, ki je povezano z vozliscem.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @return Definicija imena.
	 */
	public static AbsDef getNameDef(AbsTree node) {
		AbsDef def = nameDef.get(node);
		return def;
	}

	/** Tipizacija vozlisc drevesa. */
	private static HashMap<AbsTree, SemType> type = new HashMap<AbsTree, SemType>();

	/**
	 * Poveze vozlisce drevesa z opisom tipa.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @param typ
	 *            Opis tipa.
	 */
	public static void setType(AbsTree node, SemType typ) {
		type.put(node, typ);
	}

	/**
	 * Vrne opis tipa, ki je povezano z vozliscem.
	 * 
	 * @param node
	 *            Vozlisce drevesa.
	 * @return Opis tipa.
	 */
	public static SemType getType(AbsTree node) {
		SemType typ = type.get(node);
		return typ;
	}

}
