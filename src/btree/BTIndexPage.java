package btree;

import global.PageId;
import global.RID;
import heap.InvalidSlotNumberException;
import heap.Tuple;
import java.io.IOException;

public class BTIndexPage extends BTSortedPage {
	/**
	 * pins the page with PageId id , and set the keyType to i
	 * 
	 * @param i
	 *            : Key Type
	 * @throws ConstructPageException
	 */
	public BTIndexPage(PageId id, int i) throws ConstructPageException {
		super(id, i);
		
	}

	/**
	 * Creates a new Page , set the KeyType to i , the PageId can be obtained by
	 * getCur
	 * 
	 * @param i
	 * @throws ConstructPageException
	 */
	public BTIndexPage(int i) throws ConstructPageException {
		super(i);
		try
		{
			setNextPage(new PageId(-2));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
		}

	}

	/**
	 * This method modifies the rid (i/p) to point to the RID of the first
	 * Record ( not the left link) and return the first Record in the form of a
	 * KeyDataEntry
	 * 
	 * @param rid
	 *            , will be modified to point to the first RID
	 * @return first KeyDataEntry
	 * @throws IOException
	 * @throws InvalidSlotNumberException
	 * @throws ConvertException
	 * @throws NodeNotMatchException
	 * @throws KeyNotMatchException
	 */
	public KeyDataEntry getFirst(RID rid) throws IOException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		RID first = firstRecord();
		if (first == null)
			return null;
		rid.pageNo = first.pageNo;
		rid.slotNo = first.slotNo;
		Tuple e = getRecord(rid);
		return BT.getEntryFromBytes(e.getTupleByteArray(), e.getOffset(),
				e.getLength(), keyType, (short) 11);
	}

	/**
	 * Return the KeyDataEntry in the next Record , and moves the RID to points
	 * to the next RID
	 * 
	 * @param rid
	 *            : RID of the current Record
	 * @return
	 * @throws IOException
	 * @throws InvalidSlotNumberException
	 * @throws ConvertException
	 * @throws NodeNotMatchException
	 * @throws KeyNotMatchException
	 */
	public KeyDataEntry getNext(RID rid) throws IOException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		// make rid points to the next RID
		RID rid1 = nextRecord(rid);
		if (rid1 == null)
		{
			rid .pageNo= null;
			return null;
		}
		rid.pageNo = rid1.pageNo;
		rid.slotNo = rid1.slotNo;
		Tuple e = getRecord(rid);
		return getKeyDataEntry(e);
	}

	/**
	 * get Left Link
	 * 
	 * @return
	 * @throws IOException
	 */
	public PageId getLeftLink() throws IOException {
		return getPrevPage();
	}

	/**
	 * set left link
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void setLeftLink(PageId id) throws Exception {
		setPrevPage(id);
	}

	/**
	 * Insert the KeyDataEntry with key (key) and data (IndexData (id)) if there
	 * is no space , RID will be null
	 * 
	 * @param key
	 * @param id
	 * @return
	 * @throws InsertRecException
	 * @throws IOException
	 */
	public RID insertKey(KeyClass key, PageId id) throws InsertRecException,
			IOException {
		PageId pid = getNextPage();
		if (getNextPage().pid <-3)
		{
			return null;
		}
		KeyDataEntry data = new KeyDataEntry(key, new IndexData(id));
		RID rid = insertRecord(data);
		if (rid != null)
			setNextPage(new PageId(getNextPage().pid - 1));
		return rid;
	}

	/**
	 * remove Record with Key key if no such record , null will be returned
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public KeyDataEntry remove(KeyClass key) throws Exception {


		RID rid = firstRecord();
		while (rid != null)
		{
			Tuple e = getRecord(rid);
			KeyDataEntry data = getKeyDataEntry(e);
			if (Compare(data.key, key) == 0)
			{
				deleteSortedRecord(rid);
				setNextPage(new PageId(getNextPage().pid + 1));
				return data;
			}
			rid = nextRecord(rid);
		}
		return null;
	}

	/**
	 * Compare key1 to key 2 , 1 if (key1>key2) , 0 if equal ,-1 if(key2>key1)
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 * @throws Exception
	 */
	private int Compare(KeyClass key1, KeyClass key2) throws Exception {
		if (key1 instanceof IntegerKey && key2 instanceof IntegerKey)
		{
			return ((IntegerKey) key1).getKey().compareTo(
					((IntegerKey) key2).getKey());
		}
		if (key1 instanceof StringKey && key2 instanceof StringKey)
		{
			return ((StringKey) key1).getKey().compareTo(
					((StringKey) key2).getKey());
		}
		throw new Exception("Key mismatch Exception");
	}

	/**
	 * Convert the Tuple e to the KeyDataEntry corresponding to it
	 * 
	 * @param e
	 * @return
	 * @throws KeyNotMatchException
	 * @throws NodeNotMatchException
	 * @throws ConvertException
	 */
	private KeyDataEntry getKeyDataEntry(Tuple e) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		return BT.getEntryFromBytes(e.getTupleByteArray(), e.getOffset(),
				e.getLength(), keyType, (short) 11);
	}

	/**
	 * return the PageId that the pointer in the KeyDataEntry with KeyClass key
	 * points at.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public PageId getPageNoByKey(KeyClass key) throws Exception {
		RID rid = new RID();
		KeyDataEntry data = getFirst(rid);
		while (rid != null)
		{
			if (Compare(data.key, key) == 0)
			{
				return ((IndexData) data.data).getData();
			}
			data = getNext(rid);
			if (data == null)
				break;
		}
		return null;
	}

	/**
	 * Print all the values in the IndexPage leftLink key and Pointer are
	 * surrounded with |&Key&Pointer| All in the same line
	 * 
	 * @throws InvalidSlotNumberException
	 * @throws KeyNotMatchException
	 * @throws NodeNotMatchException
	 * @throws ConvertException
	 * @throws IOException
	 */
	public void print() throws InvalidSlotNumberException,

			KeyNotMatchException, NodeNotMatchException, ConvertException,
			IOException {
		RID rid = new RID();
		System.out.print("Index page , id = "+getCurPage().pid+" ,");
		System.out.print("Left Link :("+getLeftLink().pid + "), Data :");
		KeyDataEntry data = getFirst(rid);
		System.out.print("(" + data.key.toString() + "," + data.data.toString()
				+ ")");
		while (rid != null)
		{
			data = getNext(rid);
			if (data == null)
				break;
			System.out.print(",(" + data.key.toString() + ","
					+ data.data.toString() + ")");
		}
		System.out.println();
	}
}
