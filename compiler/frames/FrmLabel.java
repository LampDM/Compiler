package compiler.frames;

/**
 * Opis labele v programu.
 * 
 * @author sliva
 */
public class FrmLabel implements java.io.Serializable {

	/** Ime labele.  */
	private String name;

	/**
	 * Ustvari novo labelo.
	 * 
	 * @param name Ime labele.
	 */
	private FrmLabel(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object l) {
		return name == ((FrmLabel)l).name;
	}

	/**
	 * Vrne ime labele.
	 *
	 * @return Ime labele.
	 */
	public String name() {
		return name;
	}

	/** Stevec anonimnih label.  */
	private static int label_count = 0;

	/** 
	 * Vrne novo anonimno labelo.
	 *
	 * @return Nova anonimna labela.
	 */
	public static FrmLabel newLabel() {
		return new FrmLabel("L" + (label_count++));
	}

	/**
	 * Vrne novo poimenovano labelo.
	 * 
	 * @param name Ime nove poimenovane labele.
	 * @return Nova poimenovana labela.
	 */
	public static FrmLabel newLabel(String name) {
		return new FrmLabel("_" + name);
	}

}
