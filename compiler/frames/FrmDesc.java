package compiler.frames;

import java.util.*;

import compiler.abstr.tree.*;

/**
 * Shranjevanje klicnih zapisov.
 * 
 * @author sliva
 */
public class FrmDesc {

	/** Klicni zapisi. */
	private static HashMap<AbsFunDef, FrmFrame> frames = new HashMap<AbsFunDef, FrmFrame>();

	/**
	 * Poveze funkcijo s klicnim zapisom.
	 * 
	 * @param fun Funkcija.
	 * @param frame Klicni zapis.
	 */
	public static void setFrame(AbsFunDef fun, FrmFrame frame) {
		FrmDesc.frames.put(fun, frame);
	}
	
	/**
	 * Vrne klicni zapis funkcije.
	 * 
	 * @param fun Funkcija.
	 * @return Klicni zapis.
	 */
	public static FrmFrame getFrame(AbsTree fun) {
		return FrmDesc.frames.get(fun);
	}

	/** Opisi dostopa. */
	private static HashMap<AbsDef, FrmAccess> acceses = new HashMap<AbsDef, FrmAccess>();

	/**
	 * Poveze spremenljivko, parameter ali komponento z opisom dostopa.
	 * 
	 * @param var Spremenljivka, parameter ali komponenta.
	 * @param access Opis dostopa.
	 */
	public static void setAccess(AbsDef var, FrmAccess access) {
		FrmDesc.acceses.put(var, access);
	}
	
	/**
	 * Vrne opis dostopa do spremenljivke, parametra ali komponente.
	 * 
	 * @param var Spremenljivka, parameter ali komponenta.
	 * @return Opis dostopa.
	 */
	public static FrmAccess getAccess(AbsDef var) {
		return FrmDesc.acceses.get(var);
	}

}
