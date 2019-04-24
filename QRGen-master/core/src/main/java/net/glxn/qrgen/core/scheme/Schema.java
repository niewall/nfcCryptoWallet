
package net.glxn.qrgen.core.scheme;

/**
 * Abstract schema class
 *
 */
public abstract class Schema {

	/**
	 * Parse qr code schema for given code string.
	 * 
	 * @param code
	 * @return schema
	 */
	public abstract Schema parseSchema(String code);

	/**
	 * Generates code string.
	 * 
	 * @return code
	 */
	public abstract String generateString();
}
