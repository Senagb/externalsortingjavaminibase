// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:53:41 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IndexData.java

package btree;

import global.PageId;

// Referenced classes of package btree:
//            DataClass

public class IndexData extends DataClass {

	public String toString() {
		return (new Integer(pageId.pid)).toString();
	}

	IndexData(PageId pageid) {
		pageId = new PageId(pageid.pid);
	}

	IndexData(int i) {
		pageId = new PageId(i);
	}

	protected PageId getData() {
		return new PageId(pageId.pid);
	}

	protected void setData(PageId pageid) {
		pageId = new PageId(pageid.pid);
	}

	private PageId pageId;
}