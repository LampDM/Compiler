package compiler.frames;

/**
 * Opis zacasne spremenljivke v programu.
 * 
 * @author sliva
 */
public class FrmTemp implements java.io.Serializable {

	/** Stevec zacasnih spremenljivk.  */
	private static int count = 0;

	/** Ime te zacasne spremenljivke.  */
	private int num;

	/**
	 * Ustvari novo zacasno spremenljivko.
	 */
	public FrmTemp() {
		num = count++;
	}

	/**
	 * Vrne ime zacasne spremenljivke.
	 * 
	 * @return Ime zacasne spremenljivke.
	 */
	public String name() {
		return "T" + num;
	}

	@Override
	public boolean equals(Object t) {
		return num == ((FrmTemp)t).num;
	}

}
