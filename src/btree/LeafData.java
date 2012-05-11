// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:51:56 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LeafData.java

package btree;

import global.RID;

// Referenced classes of package btree:
//            DataClass

public class LeafData extends DataClass {

	public String toString() {
		String s = "[ " + (new Integer(myRid.pageNo.pid)).toString() + " "
				+ (new Integer(myRid.slotNo)).toString() + " ]";
		return s;
	}

	LeafData(RID rid) {
		myRid = new RID(rid.pageNo, rid.slotNo);
	}

	public RID getData() {
		return new RID(myRid.pageNo, myRid.slotNo);
	}

	public void setData(RID rid) {
		myRid = new RID(rid.pageNo, rid.slotNo);
	}

	private RID myRid;
}