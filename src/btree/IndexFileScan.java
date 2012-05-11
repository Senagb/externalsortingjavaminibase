// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:50:24 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IndexFileScan.java

package btree;


// Referenced classes of package btree:
//            KeyDataEntry

public abstract class IndexFileScan
{

    public abstract KeyDataEntry get_next();

    public abstract void delete_current();

    public abstract int keysize();

    public IndexFileScan()
    {
    }
}