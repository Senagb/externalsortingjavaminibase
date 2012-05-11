// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:53:22 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   InsertRecException.java

package btree;

import chainexception.ChainException;

public class InsertRecException extends ChainException
{

    public InsertRecException()
    {
    }

    public InsertRecException(String s)
    {
        super(null, s);
    }

    public InsertRecException(Exception exception, String s)
    {
        super(exception, s);
    }
}