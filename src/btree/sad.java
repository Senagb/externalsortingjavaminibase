//package btree;
//
//import global.PageId;
//import global.RID;
//import global.SystemDefs;
//import heap.HFPage;
//import heap.InvalidSlotNumberException;
//import heap.Tuple;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Stack;
//
//import bufmgr.BufMgrException;
//import bufmgr.BufferPoolExceededException;
//import bufmgr.HashEntryNotFoundException;
//import bufmgr.HashOperationException;
//import bufmgr.InvalidFrameNumberException;
//import bufmgr.PageNotReadException;
//import bufmgr.PagePinnedException;
//import bufmgr.PageUnpinnedException;
//import bufmgr.ReplacerException;
//
//public class BTreeFile extends IndexFile {
//	private BTHeaderPage rootPointer = new BTHeaderPage();
//	private PageId root = new PageId();
//	private FileWriter fstream;
//	private BufferedWriter out;
//
//	public BTreeFile(String string, int keyType, int keySize, int delete) {
//		try {
//			root = SystemDefs.JavabaseBM.newPage(rootPointer, 1);
//			rootPointer.setName(string);
//			rootPointer.setKeytype(keyType);
//			rootPointer.setKeySize(keySize);
//			SystemDefs.JavabaseDB.add_file_entry(string, root);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//		}
//	}
//
//	public BTreeFile(String filename) {
//		try {
//			root = SystemDefs.JavabaseDB.get_file_entry(filename);
//			SystemDefs.JavabaseBM.pinPage(root, rootPointer, false);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//		}
//	}
//
//	/**
//	 * insert the KeyDataEntry in the BTreeFile
//	 */
//	public void insert(KeyClass key, RID rid) {
//		try {
//			System.out.println("Insert Key" + key.toString());
//			// if there is no root
//			if (rootPointer.getRoot() == null) {
//				initialize(key, rid);
//				return;
//			}
//			// Path
//			Stack<RID> result = search(key);
//			RID pos = result.peek();
//			// BTLeafPage insertion
//			BTIndexPage index = new BTIndexPage(pos.pageNo,
//					rootPointer.getKeytype());
//			BTLeafPage leaf;
//			// if the page is pointed by the left link
//			if (pos.slotNo != -1) {
//				Tuple e = index.getRecord(pos);
//				KeyDataEntry data = BT.getEntryFromBytes(e.getTupleByteArray(),
//						e.getOffset(), e.getLength(), rootPointer.getKeytype(),
//						11);
//				leaf = new BTLeafPage(((IndexData) data.data).getData(), 1);
//			} else {
//				leaf = new BTLeafPage(index.getLeftLink(), index.keyType);
//			}
//			SystemDefs.JavabaseBM.unpinPage(index.getCurPage(), false);
//			if (leaf.insertRecord(key, rid) != null)
//			// if insertion is done
//			{
//				SystemDefs.JavabaseBM.unpinPage(leaf.getCurPage(), true);
//				// leaf.print();
//				return;
//			}
//			// if insertion failed
//			BTLeafPage page2 = new BTLeafPage(leaf.keyType);
//			KeyDataEntry temp = SplitLeaf(leaf, page2, key, rid);
//			SystemDefs.JavabaseBM.unpinPage(page2.getCurPage(), true);
//			SystemDefs.JavabaseBM.unpinPage(leaf.getCurPage(), true);
//			insertInIndexedPage(result, temp, leaf.keyType);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void initialize(KeyClass key, RID rid) throws Exception {
//		BTLeafPage leaf = new BTLeafPage(rootPointer.getKeytype());
//		leaf.insertRecord(key, rid);
//		BTIndexPage root = new BTIndexPage(rootPointer.getKeytype());
//		root.insertKey(key, leaf.getCurPage());
//		BTLeafPage leaf2 = new BTLeafPage(rootPointer.getKeytype());
//		// leaf.setPrevPage(leaf2.getCurPage());
//		leaf2.setNextPage(leaf.getCurPage());
//		root.setLeftLink(leaf2.getCurPage());
//		rootPointer.setRoot(root.getCurPage());
//		// root.print();
//		// leaf.print();
//		SystemDefs.JavabaseBM.unpinPage(root.getCurPage(), true);
//		SystemDefs.JavabaseBM.unpinPage(leaf.getCurPage(), true);
//		SystemDefs.JavabaseBM.unpinPage(leaf2.getCurPage(), true);
//	}
//
//	private ArrayList<KeyDataEntry> getSize(BTIndexPage page, KeyDataEntry input)
//			throws Exception {
//		ArrayList<KeyDataEntry> values = new ArrayList();
//		short size = 0;
//		// page.print();
//		RID pos = new RID();
//		KeyDataEntry data = page.getFirst(pos);
//		boolean added = false;
//		short addingIndex = -1;
//		if (pos == null || pos.pageNo == null)
//			return new ArrayList<KeyDataEntry>();
//		if (BT.keyCompare(data.key, input.key) > 0) {
//			values.add(input);
//			added = true;
//			addingIndex = 0;
//		}
//		values.add(data);
//		size++;
//		while ((data = page.getNext(pos)) != null) {
//			if (BT.keyCompare(data.key, input.key) > 0 && !added) {
//				addingIndex = size;
//				values.add(input);
//				added = true;
//			}
//			values.add(data);
//			size++;
//		}
//		if (!added) {
//			values.add(input);
//			addingIndex = size;
//		}
//		int i = 0;
//		for (i = 0; i < (size + 1) / 2; i++)
//			values.remove(0);
//		int temp = i;
//		addingIndex -= i;
//		for (; i < (size + 1); i++)
//			page.remove(values.get(i - temp).key);
//		if (addingIndex < 0) {
//			page.insertKey(input.key, ((IndexData) input.data).getData());
//		}
//		return values;
//	}
//
//	/**
//	 * divide the records in page including input to 2 groups , remove the
//	 * second group from page
//	 * 
//	 * @param page
//	 * @param input
//	 * @return
//	 * @throws IOException
//	 * @throws InvalidSlotNumberException
//	 * @throws KeyNotMatchException
//	 * @throws NodeNotMatchException
//	 * @throws ConvertException
//	 * @throws InsertRecException
//	 * @throws DeleteRecException
//	 */
//	private ArrayList<KeyDataEntry> getSize(BTLeafPage page, KeyDataEntry input)
//			throws IOException, InvalidSlotNumberException,
//			KeyNotMatchException, NodeNotMatchException, ConvertException,
//			InsertRecException, DeleteRecException {
//		ArrayList<KeyDataEntry> values = new ArrayList<KeyDataEntry>();
//		short size = 0;
//		// page.print();
//		RID pos = new RID();
//		// first record in page
//		KeyDataEntry data = page.getFirst(pos);
//		boolean added = false; // indicates whether input is added or not yet(to
//								// keep them sorted)
//		short addingIndex = -1;
//		// if the page is empty ( impossible to happen)
//		if (pos == null || pos.pageNo == null)
//			return new ArrayList<KeyDataEntry>();
//		// if first record key> input key
//		if (BT.keyCompare(data.key, input.key) > 0) {
//			addingIndex = 0;
//			values.add(input);
//			added = true;
//		}
//		values.add(data);
//		size++;
//		while ((data = page.getNext(pos)) != null) {
//			if (BT.keyCompare(data.key, input.key) > 0 && !added) {
//				addingIndex = size;
//				values.add(input);
//				added = true;
//			}
//			values.add(data);
//			size++;
//		}
//		if (!added) {
//			addingIndex = size;
//			values.add(input);
//		}
//		int i = 0;
//		for (i = 0; i < (size + 1) / 2; i++)
//			values.remove(0);
//		int temp = i;
//		addingIndex -= i;
//		for (; i < (size + 1); i++)
//			// delete the second half form page
//			page.delEntry(values.get(i - temp));
//		if (addingIndex < 0) {
//			page.insertRecord(input.key, ((LeafData) input.data).getData());
//		}
//		return values;
//	}
//
//	/**
//	 * insert the KeyDataEntry entry into the BTIndexPage pointed by the first
//	 * RID in stack if the stack is empty , add it in the root
//	 * 
//	 * @param stack
//	 * @param entry
//	 * @param keyType
//	 * @throws Exception
//	 */
//	private void insertInIndexedPage(Stack<RID> stack, KeyDataEntry entry,
//			int keyType) throws Exception {
//		BTIndexPage index = null;
//		if (!stack.isEmpty())
//			index = new BTIndexPage(stack.pop().pageNo, keyType);
//		else
//			index = new BTIndexPage(rootPointer.getRoot(), keyType);
//		RID rid = index
//				.insertKey(entry.key, ((IndexData) entry.data).getData());
//		if (rid != null) {
//			SystemDefs.JavabaseBM.unpinPage(index.getCurPage(), true);
//			// if insertion is done
//			// index.print();
//			return;
//		} else {
//			BTIndexPage sipiling = new BTIndexPage(index.keyType);
//			splitIndexPage(index, sipiling, stack, entry);
//			SystemDefs.JavabaseBM.unpinPage(index.getCurPage(), true);
//			SystemDefs.JavabaseBM.unpinPage(sipiling.getCurPage(), true);
//		}
//	}
//
//	/**
//	 * split 2 BTIndexPages to two pages
//	 * 
//	 * @param index
//	 * @param sipiling
//	 * @param stack
//	 * @param entry
//	 * @throws Exception
//	 */
//	private void splitIndexPage(BTIndexPage index, BTIndexPage sipiling,
//			Stack<RID> stack, KeyDataEntry entry) throws Exception {
//		ArrayList<KeyDataEntry> data = getSize(index, entry);
//		KeyDataEntry value = data.remove(0);
//		sipiling.setLeftLink(((IndexData) value.data).getData());
//		value.data = new IndexData(sipiling.getCurPage());
//		KeyDataEntry copy = null;
//		while (!data.isEmpty()) {
//			copy = data.remove(0);
//			sipiling.insertKey(copy.key, ((IndexData) copy.data).getData());
//		}
//		if (stack.isEmpty()) {
//			BTIndexPage root = new BTIndexPage(rootPointer.getKeytype());
//			root.setLeftLink(index.getCurPage());
//			root.insertKey(value.key, ((IndexData) value.data).getData());
//			rootPointer.setRoot(root.getCurPage());
//			SystemDefs.JavabaseBM.unpinPage(root.getCurPage(), true);
//			return;
//		}
//		// index.print();
//		// sipiling.print();
//		stack.pop();
//		insertInIndexedPage(stack, value, index.keyType);
//	}
//
//	/**
//	 * split the page leaf into 2 pages (leaf and spiling)
//	 * 
//	 * @param leaf
//	 * @param spiling
//	 * @param key
//	 * @param rid
//	 * @return
//	 * @throws IOException
//	 * @throws InsertRecException
//	 * @throws KeyNotMatchException
//	 * @throws DeleteRecException
//	 * @throws InvalidSlotNumberException
//	 * @throws NodeNotMatchException
//	 * @throws ConvertException
//	 */
//	private KeyDataEntry SplitLeaf(BTLeafPage leaf, BTLeafPage spiling,
//			KeyClass key, RID rid) throws IOException, InsertRecException,
//			KeyNotMatchException, DeleteRecException,
//			InvalidSlotNumberException, NodeNotMatchException, ConvertException {
//		// divide the leaf records (and the inserted one) to 2 groups
//		ArrayList<KeyDataEntry> data = getSize(leaf, new KeyDataEntry(key, rid));
//		// the first one ( smallest) in the second page(new) is the Key inserted
//		// in the parent IndexPage
//		KeyDataEntry value = data.get(0);
//		while (!data.isEmpty()) { // add the second half to the new page
//			spiling.insertRecord((data.get(0).key),
//					((LeafData) data.get(0).data).getData());
//			data.remove(0);
//		}
//		spiling.setNextPage(leaf.getNextPage());
//		leaf.setNextPage(spiling.getCurPage());
//		// leaf.print();
//		// spiling.print();
//		// change the value in data
//		return new KeyDataEntry(value.key, new IndexData(spiling.getCurPage()));
//	}
//
//	@Override
//	public boolean Delete(KeyClass data, RID rid) {
//		try {
//			append_in_file("Deleting Key : " + data + "RID : " + rid);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Stack<RID> s = search(data);
//		RID pointer = s.pop();
//		try {
//			BTIndexPage page = new BTIndexPage(rootPointer.getKeytype());
//			PageId id = new PageId();
//			SystemDefs.JavabaseBM.pinPage(pointer.pageNo, page, false);
//			page.setCurPage(pointer.pageNo);
//			if (pointer.slotNo == -1) {
//				id = page.getLeftLink();
//			} else {
//				Tuple t = page.getRecord(pointer);
//				KeyDataEntry dataEntery = BT.getEntryFromBytes(
//						t.getTupleByteArray(), t.getOffset(), t.getLength(),
//						rootPointer.getKeytype(), ((short) 11));
//				id = ((IndexData) dataEntery.data).getData();
//			}
//			SystemDefs.JavabaseBM.unpinPage(pointer.pageNo, false);
//			BTLeafPage page1 = new BTLeafPage(rootPointer.getKeytype());
//			SystemDefs.JavabaseBM.pinPage(id, page1, false);
//			boolean t = page1.delEntry(new KeyDataEntry(data, rid));
//			page1.print();
//			SystemDefs.JavabaseBM.unpinPage(id, true);
//			return t;
//		} catch (Exception e) {
//		}
//		return false;
//	}
//
//	public Object getHeaderPage() {
//		return rootPointer;
//	}
//
//	public void close() {
//		try {
//			SystemDefs.JavabaseBM.unpinPage(root, true);
//			if (out != null) {
//				System.out
//						.println("closed-------------------------------------->");
//				out.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public BTFileScan new_scan(KeyClass lowkey, KeyClass hikey)
//			throws IOException {
//		append_in_file("New Scan lowKey = " + lowkey + " highKey = " + hikey);
//		Stack<RID> s = search(lowkey);
//		if (s.isEmpty()) {
//			lowkey = null;
//			hikey = null;
//		}
//		if (lowkey == null)
//			if (lowkey instanceof IntegerKey)
//				lowkey = new IntegerKey(0);
//			else if (lowkey instanceof StringKey)
//				lowkey = new StringKey("a");
//		BTFileScan tt = new BTFileScan(lowkey, hikey);
//
//		RID pointer = s.pop();
//		try {
//			BTIndexPage page = new BTIndexPage(pointer.pageNo,
//					rootPointer.getKeytype());
//			PageId id = new PageId();
//			SystemDefs.JavabaseBM.pinPage(pointer.pageNo, page, false);
//			page.setCurPage(pointer.pageNo);
//			if (pointer.slotNo == -1) {
//				id = page.getLeftLink();
//			} else {
//				Tuple t = page.getRecord(pointer);
//				KeyDataEntry dataEntery = BT.getEntryFromBytes(
//						t.getTupleByteArray(), t.getOffset(), t.getLength(),
//						rootPointer.getKeytype(), ((short) 11));
//				id = ((IndexData) dataEntery.data).getData();
//			}
//			SystemDefs.JavabaseBM.unpinPage(pointer.pageNo, false);
//			BTLeafPage page1 = new BTLeafPage(rootPointer.getKeytype());
//			SystemDefs.JavabaseBM.pinPage(id, page1, false);
//			tt.setCurrentRID(page1.firstRecord());
//			tt.setKeySize(rootPointer.getKeySize());
//			tt.setKeyType(rootPointer.getKeytype());
//			SystemDefs.JavabaseBM.unpinPage(id, false);
//			return tt;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return tt;
//	}
//
//	public void destroyFile() {
//		try {
//			SystemDefs.JavabaseDB.delete_file_entry(rootPointer.getName());
//			if (out != null)
//				out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void traceFilename(String filename) throws IOException {
//		fstream = new FileWriter(filename, true);
//		out = new BufferedWriter(fstream);
//	}
//
//	private void append_in_file(String operation) throws IOException {
//		out.write(operation + "\n");
//
//	}
//
//	public Stack<RID> search(KeyClass data) {
//		Stack<RID> container = new Stack<RID>();
//		PageId temp = rootPointer.getRoot();
//		HFPage tempPage = new HFPage();
//		PageId temp1 = new PageId();
//		RID r = new RID();
//		try {
//			while (true) {
//				SystemDefs.JavabaseBM.pinPage(temp, tempPage, false);
//				if (tempPage.getNextPage().pid < -1) {
//					tempPage = new BTIndexPage(tempPage.getCurPage(),
//							rootPointer.getKeytype());
//					BTIndexPage e = (BTIndexPage) tempPage;
//					e.print();
//					temp1 = find((BTIndexPage) tempPage, data, temp, r);
//					if (temp1 == null) {
//						container = new Stack<RID>();
//						break;
//					} else {
//						container.push(r);
//					}
//				} else if (tempPage.getNextPage().pid >= -1) {
//					break;
//				}
//				SystemDefs.JavabaseBM.unpinPage(temp, true);
//				temp = temp1;
//			}
//			SystemDefs.JavabaseBM.unpinPage(temp, false);
//			// System.out.println("**********END SEARCH ***************");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return container;
//	}
//
//	private PageId find(BTIndexPage tempPage, KeyClass data, PageId id, RID r1) {
//		RID r = new RID();
//		PageId p1 = new PageId();
//		try {
//			KeyDataEntry k = tempPage.getFirst(r);
//			if (BT.keyCompare(data, k.key) < 0) {
//				r1.pageNo = r.pageNo;
//				r1.slotNo = -1;
//				return tempPage.getLeftLink();
//			} else {
//				RID temprid = new RID();
//				while (BT.keyCompare(data, k.key) >= 0) {
//					p1 = ((IndexData) k.data).getData();
//					r1.copyRid(r);
//					k = tempPage.getNext(r);
//					if (k != null) {
//						temprid.copyRid(r);
//					} else {
//						break;
//					}
//				}
//				return p1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public void print() {
//		// TODO Auto-generated method stub
//		try {
//			BTIndexPage root = new BTIndexPage(rootPointer.getRoot(),
//					rootPointer.getKeytype());
//			ArrayList<BTSortedPage> current = new ArrayList();
//			current.add(root);
//			ArrayList<BTSortedPage> next = new ArrayList<BTSortedPage>();
//			int level = 0;
//			System.out.println("************Print****************");
//			while (!current.isEmpty()) {
//				System.out.println("************LEVEL " + level
//						+ "**********************");
//				while (!current.isEmpty()) {
//					if (current.get(0).getNextPage().pid < -1) {
//						validate(next, current.get(0));
//					} else {
//						new BTLeafPage(current.get(0).getCurPage(),
//								rootPointer.getKeytype()).print();
//					}
//					try {
//						SystemDefs.JavabaseBM.unpinPage(current.remove(0)
//								.getCurPage(), false);
//					} catch (Exception e) {
//					}
//				}
//				current = (ArrayList<BTSortedPage>) next.clone();
//				next = new ArrayList<BTSortedPage>();
//				level++;
//			}
//			System.out.println("*********END PRINT ****************");
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
//		} catch (Exception e) {
//		}
//	}
//
//	private void validate(ArrayList<BTSortedPage> next,
//			BTSortedPage btSortedPage) {
//		try {
//			BTIndexPage e = new BTIndexPage(btSortedPage.getCurPage(),
//					rootPointer.getKeytype());
//			RID pos = new RID();
//			KeyDataEntry data = e.getFirst(pos);
//			if (e.getLeftLink().pid != -1) {
//				next.add(new BTSortedPage(e.getLeftLink(), e.keyType));
//				SystemDefs.JavabaseBM.unpinPage(e.getLeftLink(), false);
//			}
//			if (data != null) {
//				next.add(new BTSortedPage(((IndexData) data.data).getData(),
//						rootPointer.getKeytype()));
//			}
//			while ((data = e.getNext(pos)) != null) {
//				next.add(new BTSortedPage(((IndexData) data.data).getData(),
//						rootPointer.getKeytype()));
//			}
//			e.print();
//			SystemDefs.JavabaseBM.unpinPage(e.getCurPage(), false);
//		} catch (Exception exc) {
//		}
//	}
//
//	//
//	// private KeyClass getLeftMostLeafKey() throws ReplacerException,
//	// HashOperationException, PageUnpinnedException,
//	// InvalidFrameNumberException, PageNotReadException,
//	// BufferPoolExceededException, PagePinnedException, BufMgrException,
//	// IOException, HashEntryNotFoundException, ConstructPageException,
//	// InvalidSlotNumberException, KeyNotMatchException,
//	// NodeNotMatchException, ConvertException {
//	// // TODO Auto-generated method stub
//	//
//	// if (rootPointer.getRoot() == null)
//	// return null;
//	//
//	// BTIndexPage hfPage = new BTIndexPage(rootPointer.getRoot(),
//	// rootPointer.getKeytype());
//	// PageId id = rootPointer.getRoot();
//	// PageId tempId = rootPointer.getRoot();
//	// id = hfPage.getLeftLink();
//	// while (true) { // if getNextPage() >= -1 yb2a leaf else Index
//	// BTSortedPage page = new BTSortedPage(id, rootPointer.getKeytype());
//	// tempId.pid = id.pid;
//	// id = hfPage.getNextPage();
//	// if (hfPage.getNextPage().pid < -1) {
//	// hfPage = new BTIndexPage(hfPage.getCurPage(),
//	// rootPointer.getKeytype());
//	// id = hfPage.getLeftLink();
//	// } else if (hfPage.getNextPage().pid >= -1) {
//	// SystemDefs.JavabaseBM.unpinPage(tempId, false);
//	// break;
//	// }
//	// SystemDefs.JavabaseBM.unpinPage(tempId, false);
//	// }
//	// BTLeafPage leafPage = new BTLeafPage(id, rootPointer.getKeytype());
//	// while (leafPage.getCurrent(leafPage.firstRecord()) == null) {
//	// SystemDefs.JavabaseBM.unpinPage(id, false);
//	// id = leafPage.getNextPage();
//	// leafPage = new BTLeafPage(id, rootPointer.getKeytype());
//	// }
//	// KeyClass keyClass = leafPage.getCurrent(hfPage.firstRecord()).key;
//	// SystemDefs.JavabaseBM.unpinPage(id, false);
//	// return keyClass;
//	// }
//
//	private int getLeftMostLeaf() throws ReplacerException,
//			HashOperationException, PageUnpinnedException,
//			InvalidFrameNumberException, PageNotReadException,
//			BufferPoolExceededException, PagePinnedException, BufMgrException,
//			IOException, HashEntryNotFoundException, ConstructPageException {
//		// TODO Auto-generated method stub
//		if (rootPointer.getRoot() == null)
//			return 0;
//
//		BTIndexPage hfPage = new BTIndexPage(rootPointer.getRoot(),
//				rootPointer.getKeytype());
//		PageId id = rootPointer.getRoot();
//		PageId tempId = rootPointer.getRoot();
//		id = hfPage.getLeftLink();
//		while (true) { // if getNextPage() >= -1 yb2a leaf else Index
//			BTSortedPage page = new BTSortedPage(id, rootPointer.getKeytype());
//			tempId.pid = id.pid;
//			id = page.getNextPage();
//			if (page.getNextPage().pid < -1) {
//				page = new BTIndexPage(page.getCurPage(),
//						rootPointer.getKeytype());
//				id = ((BTIndexPage) page).getLeftLink();
//			} else if (page.getNextPage().pid >= -1) {
//				SystemDefs.JavabaseBM.unpinPage(tempId, false);
//				break;
//			}
//			SystemDefs.JavabaseBM.unpinPage(tempId, false);
//		}
//		// BTLeafPage leafPage = new BTLeafPage(id, rootPointer.getKeytype());
//		SystemDefs.JavabaseBM.unpinPage(id, false);
//
//		return id.pid;
//	}
//
//	/**
//	 * @throws IOException
//	 * @throws ConstructPageException
//	 * @throws HashEntryNotFoundException
//	 * @throws BufMgrException
//	 * @throws PagePinnedException
//	 * @throws BufferPoolExceededException
//	 * @throws PageNotReadException
//	 * @throws InvalidFrameNumberException
//	 * @throws PageUnpinnedException
//	 * @throws HashOperationException
//	 * @throws ReplacerException
//	 * */
//
//	public void printLeafs() throws ReplacerException, HashOperationException,
//			PageUnpinnedException, InvalidFrameNumberException,
//			PageNotReadException, BufferPoolExceededException,
//			PagePinnedException, BufMgrException, HashEntryNotFoundException,
//			ConstructPageException, IOException {
//		// TODO Auto-generated method stub
//		PageId pageId = new PageId();
//		pageId.pid = getLeftMostLeaf();
//
//		while (pageId.pid != -1) {
//			BTLeafPage btLeafPage = new BTLeafPage(pageId,
//					rootPointer.getKeytype());
//			btLeafPage.print();
//			pageId = btLeafPage.getNextPage();
//			// SystemDefs.JavabaseBM.unpinPage(pageId, false);
//		}
//	}
//
//}
