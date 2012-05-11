// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:51:33 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   KeyNotMatchException.java

package iterator;

import chainexception.ChainException;

public class KeyNotMatchException extends ChainException
{

    public KeyNotMatchException()
    {
    }

    public KeyNotMatchException(String s)
    {
        super(null, s);
    }

    public KeyNotMatchException(Exception exception, String s)
    {
        super(exception, s);
    }
}