// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:49:54 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BT.java

package btree;

import global.*;

import java.io.*;

// Referenced classes of package btree:
//            ConvertException, IndexData, IntegerKey, KeyDataEntry, 
//            KeyNotMatchException, LeafData, NodeNotMatchException, NodeType, 
//            StringKey, KeyClass

public class BT implements GlobalConst {

	public static final int keyCompare(KeyClass keyclass, KeyClass keyclass1)
			throws KeyNotMatchException {
		if ((keyclass instanceof IntegerKey)
				&& (keyclass1 instanceof IntegerKey))
			return ((IntegerKey) keyclass).getKey().intValue()
					- ((IntegerKey) keyclass1).getKey().intValue();
		if ((keyclass instanceof StringKey) && (keyclass1 instanceof StringKey))
			return ((StringKey) keyclass).getKey().compareTo(
					((StringKey) keyclass1).getKey());
		else
			throw new KeyNotMatchException(null, "key types do not match");
	}

	public static final int getKeyLength(KeyClass keyclass)
			throws KeyNotMatchException, IOException {
		if (keyclass instanceof StringKey) {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			DataOutputStream dataoutputstream = new DataOutputStream(
					bytearrayoutputstream);
			dataoutputstream.writeUTF(((StringKey) keyclass).getKey());
			return dataoutputstream.size();
		}
		if (keyclass instanceof IntegerKey)
			return 4;
		else
			throw new KeyNotMatchException(null, "key types do not match");
	}

	public static final int getDataLength(short word0)
			throws NodeNotMatchException {
		if (word0 == 12)
			return 8;
		if (word0 == 11)
			return 4;
		else
			throw new NodeNotMatchException(null, "key types do not match");
	}

	public static final int getKeyDataLength(KeyClass keyclass, short word0)
			throws KeyNotMatchException, NodeNotMatchException, IOException {
		return getKeyLength(keyclass) + getDataLength(word0);
	}

	public static final KeyDataEntry getEntryFromBytes(byte abyte0[], int i,
			int j, int k, int l) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		try {
			Object obj1;
			byte byte0;
			if (l == 11) {
				byte0 = 4;
				obj1 = new IndexData(Convert.getIntValue((i + j) - 4, abyte0));
			} else if (l == 12) {
				byte0 = 8;
				RID rid = new RID();
				rid.slotNo = Convert.getIntValue((i + j) - 8, abyte0);
				rid.pageNo = new PageId();
				rid.pageNo.pid = Convert.getIntValue((i + j) - 4, abyte0);
				obj1 = new LeafData(rid);
			} else {
				throw new NodeNotMatchException(null, "node types do not match");
			}
			Object obj;
			if (k == 1)
				obj = new IntegerKey(
						new Integer(Convert.getIntValue(i, abyte0)));
			else if (k == 0)
				obj = new StringKey(Convert.getStrValue(i, abyte0, j - byte0));
			else
				throw new KeyNotMatchException(null, "key types do not match");
			return new KeyDataEntry(((KeyClass) (obj)), ((DataClass) (obj1)));
		} catch (IOException ioexception) {
			throw new ConvertException(ioexception, "convert faile");
		}
	}

	public static final byte[] getBytesFromEntry(KeyDataEntry keydataentry)
			throws KeyNotMatchException, NodeNotMatchException,
			ConvertException {
		try {
			int i = getKeyLength(keydataentry.key);
			int j = i;
			if (keydataentry.data instanceof IndexData)
				i += 4;
			else if (keydataentry.data instanceof LeafData)
				i += 8;
			byte abyte0[] = new byte[i];
			if (keydataentry.key instanceof IntegerKey)
				Convert.setIntValue(((IntegerKey) keydataentry.key).getKey()
						.intValue(), 0, abyte0);
			else if (keydataentry.key instanceof StringKey)
				Convert.setStrValue(((StringKey) keydataentry.key).getKey(), 0,
						abyte0);
			else
				throw new KeyNotMatchException(null, "key types do not match");
			if (keydataentry.data instanceof IndexData)
				Convert.setIntValue(
						((IndexData) keydataentry.data).getData().pid, j,
						abyte0);
			else if (keydataentry.data instanceof LeafData) {
				Convert.setIntValue(
						((LeafData) keydataentry.data).getData().slotNo, j,
						abyte0);
				Convert.setIntValue(
						((LeafData) keydataentry.data).getData().pageNo.pid,
						j + 4, abyte0);
			} else {
				throw new NodeNotMatchException(null, "node types do not match");
			}
			return abyte0;
		} catch (IOException ioexception) {
			throw new ConvertException(ioexception, "convert failed");
		}
	}

	public BT() {
	}

	public static void printAllLeafPages(Object headerPage) {
		// TODO Auto-generated method stub
		
	}

	public static void printBTree(Object headerPage) {
		// TODO Auto-generated method stub
		
	}

	public static void printPage(PageId pageId, int keyType) {
		// TODO Auto-generated method stub
		
	}
}