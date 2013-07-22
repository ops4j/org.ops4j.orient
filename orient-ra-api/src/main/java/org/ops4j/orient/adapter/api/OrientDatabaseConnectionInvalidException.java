package org.ops4j.orient.adapter.api;

/**
 * @author Markus Menner
 */
public class OrientDatabaseConnectionInvalidException extends Exception {
	public OrientDatabaseConnectionInvalidException() {
		super("connection has already been closed");
	}
}
