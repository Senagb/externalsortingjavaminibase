// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:51:16 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   KeyDataEntry.java

package btree;

import global.PageId;
import global.RID;

// Referenced classes of package btree:
//            IndexData, IntegerKey, LeafData, StringKey, 
//            KeyClass, DataClass

public class KeyDataEntry {
	public KeyClass key;
	public DataClass data;

	public KeyDataEntry(Integer integer, PageId pageid) {
		key = new IntegerKey(integer);
		data = new IndexData(pageid);
	}

	public KeyDataEntry(KeyClass keyclass, PageId pageid) {
		data = new IndexData(pageid);
		if (keyclass instanceof IntegerKey) {
			key = new IntegerKey(((IntegerKey) keyclass).getKey());
			return;
		}
		if (keyclass instanceof StringKey)
			key = new StringKey(((StringKey) keyclass).getKey());
	}

	public KeyDataEntry(String s, PageId pageid) {
		key = new StringKey(s);
		data = new IndexData(pageid);
	}

	public KeyDataEntry(Integer integer, RID rid) {
		key = new IntegerKey(integer);
		data = new LeafData(rid);
	}

	public KeyDataEntry(KeyClass keyclass, RID rid) {
		data = new LeafData(rid);
		if (keyclass instanceof IntegerKey) {
			key = new IntegerKey(((IntegerKey) keyclass).getKey());
			return;
		}
		if (keyclass instanceof StringKey)
			key = new StringKey(((StringKey) keyclass).getKey());
	}

	public KeyDataEntry(String s, RID rid) {
		key = new StringKey(s);
		data = new LeafData(rid);
	}

	public KeyDataEntry(KeyClass keyclass, DataClass dataclass) {
		if (keyclass instanceof IntegerKey)
			key = new IntegerKey(((IntegerKey) keyclass).getKey());
		else if (keyclass instanceof StringKey)
			key = new StringKey(((StringKey) keyclass).getKey());
		if (dataclass instanceof IndexData) {
			data = new IndexData(((IndexData) dataclass).getData());
			return;
		}
		if (dataclass instanceof LeafData)
			data = new LeafData(((LeafData) dataclass).getData());
	}

	public boolean equals(KeyDataEntry keydataentry) {
		boolean flag;
		if (key instanceof IntegerKey)
			flag = ((IntegerKey) key).getKey().equals(
					((IntegerKey) keydataentry.key).getKey());
		else
			flag = ((StringKey) key).getKey().equals(
					((StringKey) keydataentry.key).getKey());
		boolean flag1;
		if (data instanceof IndexData)
			flag1 = ((IndexData) data).getData().pid == ((IndexData) keydataentry.data)
					.getData().pid;
		else
			flag1 = ((LeafData) data).getData().equals(
					((LeafData) keydataentry.data).getData());
		return flag && flag1;
	}

}