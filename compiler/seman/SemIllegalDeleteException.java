package compiler.seman;

/** 
 * Ob poskusu brisanja imena, ki ni deklarirano na trenutnem nivoju.
 * 
 * @author sliva
 */
public class SemIllegalDeleteException extends Exception {

	static final long serialVersionUID = 0l;

	public SemIllegalDeleteException() {
	}
}
