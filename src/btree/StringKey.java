// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:53:02 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StringKey.java

package btree;


// Referenced classes of package btree:
//            KeyClass

public class StringKey extends KeyClass
{

@Override
public String toString() {
	// TODO Auto-generated method stub
	return key.toString();
}
    public StringKey(String s)
    {
        key = new String(s);
    }

    public String getKey()
    {
        return new String(key);
    }

    public void setKey(String s)
    {
        key = new String(s);
    }

    private String key;
}