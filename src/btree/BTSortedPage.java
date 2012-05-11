// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 4/12/2012 2:49:21 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BTSortedPage.java

package btree;

import global.PageId;
import global.RID;
import global.SystemDefs;
import heap.HFPage;
import heap.InvalidSlotNumberException;

import java.io.IOException;

import diskmgr.Page;

// Referenced classes of package btree:
//            BT, ConstructPageException, DeleteRecException, InsertRecException, 
//            KeyDataEntry, LeafData, NodeType

public class BTSortedPage extends HFPage {

	public BTSortedPage(PageId pageid, int i) throws ConstructPageException {
		try {
			SystemDefs.JavabaseBM.pinPage(pageid, this, false);
			keyType = i;
			return;
		} catch (Exception exception) {
			throw new ConstructPageException(exception,
					"construct sorted page failed");
		}
	}

	public BTSortedPage(Page page, int i) {
		super(page);
		keyType = i;
	}

	public BTSortedPage(int i) throws ConstructPageException {
		try {
			Page page = new Page();
			PageId pageid = SystemDefs.JavabaseBM.newPage(page, 1);
			if (pageid == null) {
				throw new ConstructPageException(null,
						"construct new page failed");
			} else {
				init(pageid, page);
				keyType = i;
				return;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new ConstructPageException(exception,
					"construct sorted page failed");
		}
	}

	public RID insertRecord(KeyDataEntry keydataentry)

			throws InsertRecException {
		try {
			byte abyte0[] = BT.getBytesFromEntry(keydataentry);
			RID rid = super.insertRecord(abyte0);
			if (rid == null)
				return null;
			short word0;
			if (keydataentry.data instanceof LeafData)
				word0 = 12;
			else
				word0 = 11;
			int i;
			for (i = getSlotCnt() - 1; i > 0; i--) {
				KeyClass keyclass = BT.getEntryFromBytes(getpage(),
						getSlotOffset(i), getSlotLength(i), keyType, word0).key;
				KeyClass keyclass1 = BT.getEntryFromBytes(getpage(),
						getSlotOffset(i - 1), getSlotLength(i - 1), keyType,
						word0).key;
				if (BT.keyCompare(keyclass, keyclass1) >= 0)
					break;
				short word1 = getSlotLength(i);
				short word2 = getSlotOffset(i);
				setSlot(i, getSlotLength(i - 1), getSlotOffset(i - 1));
				setSlot(i - 1, word1, word2);
			}

			rid.slotNo = i;
			return rid;
		} catch (Exception exception) {
			throw new InsertRecException(exception, "insert record failed");
		}
	}

	public boolean deleteSortedRecord(RID rid) throws DeleteRecException {
		try {
			deleteRecord(rid);
			compact_slot_dir();
			return true;
		} catch (Exception exception) {
			if (exception instanceof InvalidSlotNumberException)
				return false;
			else
				throw new DeleteRecException(exception, "delete record failed");
		}
	}

	protected int numberOfRecords() throws IOException {
		return getSlotCnt();
	}

	int keyType;
}