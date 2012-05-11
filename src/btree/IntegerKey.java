// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:50:46 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IntegerKey.java

package btree;


// Referenced classes of package btree:
//            KeyClass

public class IntegerKey extends KeyClass
{

    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return key.toString();
    }
    public IntegerKey(Integer integer)
    {
        key = new Integer(integer.intValue());
    }

    public IntegerKey(int i)
    {
        key = new Integer(i);
    }

    public Integer getKey()
    {
        return new Integer(key.intValue());
    }

    public void setKey(Integer integer)
    {
        key = new Integer(integer.intValue());
    }

    private Integer key;
}