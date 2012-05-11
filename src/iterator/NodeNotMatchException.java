// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:52:26 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NodeNotMatchException.java

package iterator;

import chainexception.ChainException;

public class NodeNotMatchException extends ChainException
{

    public NodeNotMatchException()
    {
    }

    public NodeNotMatchException(String s)
    {
        super(null, s);
    }

    public NodeNotMatchException(Exception exception, String s)
    {
        super(exception, s);
    }
}