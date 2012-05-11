// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:54:29 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ConvertException.java

package btree;

import chainexception.ChainException;

public class ConvertException extends ChainException {

	public ConvertException() {
	}

	public ConvertException(String s) {
		super(null, s);
	}

	public ConvertException(Exception exception, String s) {
		super(exception, s);
	}
}