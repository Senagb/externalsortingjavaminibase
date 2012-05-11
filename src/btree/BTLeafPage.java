package btree;

import global.PageId;
import global.RID;
import heap.InvalidSlotNumberException;
import heap.Tuple;
import java.io.IOException;
import java.security.KeyRep;
import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import diskmgr.Page;

public class BTLeafPage extends BTSortedPage {

	/* * * * * * * * C O N S T R U C T O R S * * * * * * * */

	/**
	 * pin the page with pageno, and get the corresponding BTLeafPage, also it
	 * sets the type to be NodeType.LEAF. Parameters: pageno - Input parameter.
	 * To specify which page number the BTLeafPage will correspond to. keyType -
	 * either AttrType.attrInteger or AttrType.attrString. Input parameter.
	 **/
	public BTLeafPage(PageId pageid, int i) throws ConstructPageException,
			ReplacerException, HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			IOException {
		super(pageid, i);
		// TODO Auto-generated constructor stub
	}

	/**
	 * new a page, associate the BTLeafPage instance with the Page instance,
	 * also it sets the type to be NodeType.LEAF. Parameters: keyType - either
	 * AttrType.attrInteger or AttrType.attrString. Input parameter.
	 **/
	public BTLeafPage(int i) throws ConstructPageException {
		super(i);
		try {
			setPrevPage(new PageId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * associate the BTLeafPage instance with the Page instance, also it sets
	 * the type to be NodeType.LEAF. Parameters: page - input parameter. To
	 * specify which page the BTLeafPage will correspond to. keyType - either
	 * AttrType.attrInteger or AttrType.attrString. Input parameter.
	 **/

	public BTLeafPage(Page page, int i) {

		super(page, i);
	}

	/** M E T H O D S */

	/**
	 * Iterators. One of the two functions: getFirst and getNext which provide
	 * an iterator interface to the records on a BTLeafPage.
	 * 
	 * Parameters: rid - It will be modified and the first rid in the leaf page
	 * will be passed out by itself. Input and Output parameter. Returns: return
	 * the first KeyDataEntry in the leaf page. null if no more record
	 */
	public KeyDataEntry getFirst(RID rid) throws IOException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {

		RID rid1 = super.firstRecord();
		if(rid1==null){
			rid.pageNo=null;
			rid=null;
			return null;}
		rid.pageNo = rid1.pageNo;
		rid.slotNo = rid1.slotNo;
		Tuple e = getRecord(rid);

		KeyDataEntry kde = BT.getEntryFromBytes(e.getTupleByteArray(),
				e.getOffset(), e.getLength(), keyType, 12);
		return kde;
	}

	/**
	 * Iterators. One of the two functions: getFirst and getNext which provide
	 * an iterator interface to the records on a BTLeafPage.
	 * 
	 * Parameters: rid - It will be modified and the next rid will be passed out
	 * by itself. Input and Output parameter. Returns: return the next
	 * KeyDataEntry in the leaf page. null if no more record.
	 */
	public KeyDataEntry getNext(RID rid) throws IOException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {

		RID rid1 = super.nextRecord(rid);
		if (rid1 == null) {
			rid.pageNo = null;
			return null;
		}
		rid.pageNo = rid1.pageNo;
		rid.slotNo = rid1.slotNo;

		Tuple e = getRecord(rid);

		KeyDataEntry kde = BT.getEntryFromBytes(e.getTupleByteArray(),
				e.getOffset(), e.getLength(), keyType, 12);
		return kde;
	}

	/**
	 * getCurrent returns the current record in the iteration; it is like
	 * getNext except it does not advance the iterator.
	 * 
	 * Parameters: rid - the current rid. Input and Output parameter. But
	 * Output=Input. Returns: return the current KeyDataEntry
	 */
	public KeyDataEntry getCurrent(RID rid) throws InvalidSlotNumberException,
			IOException, KeyNotMatchException, NodeNotMatchException,
			ConvertException {
		Tuple e = getRecord(rid);
		KeyDataEntry kde = BT.getEntryFromBytes(e.getTupleByteArray(),
				e.getOffset(), e.getLength(), keyType, 12);
		return kde;
	}

	/**
	 * delete a data entry in the leaf page. Parameters: dEntry - the entry will
	 * be deleted in the leaf page. Input parameter. Returns: true if deleted;
	 * false if no dEntry in the page
	 */
	public boolean delEntry(KeyDataEntry dEntry) throws IOException,

			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException, DeleteRecException {
		RID rid = super.firstRecord();
		if(rid == null )
			return false;
		KeyDataEntry entry = getFirst(rid);
		if (BT.keyCompare(entry.key, dEntry.key) == 0) {
			super.deleteSortedRecord(rid);
			setPrevPage(new PageId(getPrevPage().pid - 1));
			return true;
		}
		while (true) {
			entry = getNext(rid);
			if (entry == null)
				return false;
			if (BT.keyCompare(entry.key, dEntry.key) == 0) {
				super.deleteSortedRecord(rid);
				setPrevPage(new PageId(getPrevPage().pid - 1));
				return true;
			}
		}
	}

	/**
	 * insertRecord. READ THIS DESCRIPTION CAREFULLY. THERE ARE TWO RIDs WHICH
	 * MEAN TWO DIFFERENT THINGS. Inserts a key, rid value into the leaf node.
	 * This is accomplished by a call to SortedPage::insertRecord() Parameters:
	 * 
	 * Parameters: key - - the key value of the data record. Input parameter.
	 * dataRid - - the rid of the data record. This is stored on the leaf page
	 * along with the corresponding key value. Input parameter. Returns: - the
	 * rid of the inserted leaf record data entry, i.e., the pair.
	 * */
	public RID insertRecord(KeyClass key, RID dataRid)

	throws InsertRecException, IOException {
		int pid = getPrevPage().pid;
		if (getPrevPage().pid > 1)
			return null;
		KeyDataEntry dataEntry = new KeyDataEntry(key, dataRid);
		RID rid = super.insertRecord(dataEntry);
		if (rid != null)
			setPrevPage(new PageId(getPrevPage().pid + 1));
		pid = getPrevPage().pid;
		return rid;
	}

	public void print() {
		

		RID pos = new RID();
		try {
			System.out.print("Leaf Page , id = "+getCurPage().pid+" : ");
			KeyDataEntry data = getFirst(pos);
			
			System.out.print("("
					+ (data.key).toString() + ",("
					+ ((LeafData) data.data).getData().pageNo + ")),");
			while (pos.pageNo != null) {
				data = getNext(pos);
				if (data != null)
					System.out.print("("+(data.key).toString() + ",("
							+ ((LeafData) data.data).getData().pageNo + ")) ,");
			}
			System.out.println();
		} catch (Exception e) {
			System.out.println("Empty Page");
		}
	}
}
