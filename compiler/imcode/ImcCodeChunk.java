package compiler.imcode;

import compiler.*;
import compiler.frames.*;

/**
 * Fragment kode.
 * 
 * @author sliva
 */
public class ImcCodeChunk extends ImcChunk {

	/** Klicni zapis funkcije.  */
	public FrmFrame frame;

	/** Vmesna koda funkcije.  */
	public ImcStmt imcode;

	/** Linearna vmesna koda.  */
	public ImcStmt lincode;

	/**
	 * Ustvari nov fragment kode.
	 * 
	 * @param frame Klicni zapis funkcije.
	 * @param imcode Vmesna koda funckije.
	 */
	public ImcCodeChunk(FrmFrame frame, ImcStmt imcode) {
		this.frame = frame;
		this.imcode = imcode;
		this.lincode = null;
	}

	@Override
	public void dump() {
		Report.dump(0, "CODE CHUNK: label=" + frame.label.name());
		Report.dump(2, frame.toString());
		if (lincode == null) imcode.dump(2); else lincode.dump(2);
	}

}
