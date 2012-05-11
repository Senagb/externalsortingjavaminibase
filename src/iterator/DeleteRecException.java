// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:53:57 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DeleteRecException.java

package iterator;

import chainexception.ChainException;

public class DeleteRecException extends ChainException
{

    public DeleteRecException()
    {
    }

    public DeleteRecException(String s)
    {
        super(null, s);
    }

    public DeleteRecException(Exception exception, String s)
    {
        super(exception, s);
    }
}