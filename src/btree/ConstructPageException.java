/*   */package btree;

/*   */
/*   */import chainexception.ChainException;

public class ConstructPageException extends ChainException {
	public ConstructPageException() {
		/*   */}

	/*   */
	/*   */public ConstructPageException(String paramString)
	/*   */{
		/* 7 */super(null, paramString);
	}

	/* 8 */public ConstructPageException(Exception paramException,
			String paramString) {
		super(paramException, paramString);
		/*   */}
	/*   */
}
