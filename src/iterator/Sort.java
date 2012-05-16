package iterator;

import global.AttrType;
import global.Convert;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.FileAlreadyDeletedException;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.HFException;
import heap.HFPage;
import heap.Heapfile;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Scan;
import heap.SpaceNotAvailableException;
import heap.Tuple;

import java.io.IOException;
import java.util.Vector;

import diskmgr.Page;

/**
 * The Sort class sorts a file. All necessary information are passed as
 * arguments to the constructor. After the constructor call, the user can
 * repeatly call <code>get_next()</code> to get tuples in sorted order. After
 * the sorting is done, the user should call <code>close()</code> to clean up.
 */
public class Sort extends Iterator implements GlobalConst {

	private int keyType;
	private int counter = 0;
	protected Heapfile gFile; // user given file
	private static String data1[] = { "raghu", "xbao", "cychan", "leela",
			"ketola", "soma", "ulloa", "dhanoa", "dsilva", "kurniawa",
			"dissoswa", "waic", "susanc", "kinc", "marc", "scottc", "yuc",
			"ireland", "rathgebe", "joyce", "daode", "yuvadee", "he",
			"huxtable", "muerle", "flechtne", "thiodore", "jhowe", "frankief",
			"yiching", "xiaoming", "jsong", "yung", "muthiah", "bloch", "binh",
			"dai", "hai", "handi", "shi", "sonthi", "evgueni", "chung-pi",
			"chui", "siddiqui", "mak", "tak", "sungk", "randal", "barthel",
			"newell", "schiesl", "neuman", "heitzman", "wan", "gunawan",
			"djensen", "juei-wen", "josephin", "harimin", "xin", "zmudzin",
			"feldmann", "joon", "wawrzon", "yi-chun", "wenchao", "seo",
			"karsono", "dwiyono", "ginther", "keeler", "peter", "lukas",
			"edwards", "mirwais", "schleis", "haris", "meyers", "azat",
			"shun-kit", "robert", "markert", "wlau", "honghu", "guangshu",
			"chingju", "bradw", "andyw", "gray", "vharvey", "awny", "savoy",
			"meltz" };

	// ------------------------------------------------------------------------
	private static final int ARBIT_RUNS = 10;
	private AttrType[] _in;// size will always be 1
	private short n_cols; // equal 1
	private short[] str_lens;// size is one and assigned from the constructor
	private Iterator _am;// given at the constructor
	private int _sort_fld;// unsued in our case
	private TupleOrder order;
	private int _n_pages;
	private byte[][] bufs;// array of the bytes that is read and to be sorted
	private boolean first_time;// first time to call pass 0
	private int Nruns;
	private int max_elems_in_heap;
	private int sortFldLen; // length of the field you are sorting on
	private int tuple_size;
	// private pnodeSplayPQ Q;
	private Heapfile[] temp_files; // replaced by one heapfile
	private int n_tempfiles;
	private Tuple output_tuple;
	private int[] n_tuples;
	private int n_runs;// number of runs
	// private Tuple op_buf;
	// private OBuf o_buf;
	// private SpoofIbuf[] i_buf;
	private PageId[] bufs_pids; // ids of pages in the buffer manager
	private boolean useBM = true; // flag for whether to use buffer manager

	/**
	 * Class constructor, take information about the tuples, and set up the
	 * sorting
	 * 
	 * @param in
	 *            array containing attribute types of the relation
	 * @param len_in
	 *            number of columns in the relation
	 * @param str_sizes
	 *            array of sizes of string attributes
	 * @param am
	 *            an iterator for accessing the tuples
	 * @param sort_fld
	 *            the field number of the field to sort on
	 * @param sort_order
	 *            the sorting order (ASCENDING, DESCENDING)
	 * @param sort_field_len
	 *            the length of the sort field
	 * @param n_pages
	 *            amount of memory (in pages) available for sorting
	 * @exception IOException
	 *                from lower layers
	 * @exception SortException
	 *                something went wrong in the lower layer.
	 * @throws InvalidTupleSizeException
	 * @throws InvalidTypeException
	 * @throws IteratorBMException
	 */
	public Sort(AttrType[] in, short len_in, short[] str_sizes, Iterator am,
			int sort_fld, TupleOrder sort_order, int sort_fld_len, int n_pages)
			throws IOException, SortException, InvalidTypeException,
			InvalidTupleSizeException, IteratorBMException {
		keyType = in[0].attrType;
		_am = am;
		_sort_fld = sort_fld;
		order = sort_order;
		_n_pages = n_pages;
		_in = in;
		n_cols = len_in;
		str_lens = str_sizes;
		sortFldLen = sort_fld_len;
	}

	public void setHeapFile(Heapfile hf) {
		gFile = hf;
	}

	// pass 0
	public Vector<Heapfile> sortHeapfile() throws Exception {
		/*
		 * WE MAY NEED TO MODIFY THIS THROUGH CATCHING THIS EXCEPTION SORTING
		 * WHAT WAS INSERTED SO FAR THEN CONTINUE !Exception in thread "main"
		 * heap.HFDiskMgrException: Heapfile.java: get_file_entry() failed at
		 * heap.Heapfile.get_file_entry(Heapfile.java:1015) at
		 * heap.Heapfile.<init>(Heapfile.java:261) at
		 * iterator.Sort.sortHeapfile(Sort.java:132) at
		 * iterator.Sort.organizer(Sort.java:347) at
		 * iterator.MyTest.<init>(MyTest.java:117) at
		 * iterator.MyTest.main(MyTest.java:121)
		 */
		Vector<Heapfile> v = new Vector<Heapfile>();
		Heapfile hf = new Heapfile("Sorted");
		int t1 = gFile.getRecCnt();
		System.out.println(t1);
		Scan s = gFile.openScan();
		Tuple t = s.getNext(new RID());
		Page p = new Page();
		HFPage page = new HFPage(p);
		page.init(page.getCurPage(), page);
		int counter = 0;
		boolean entered = false;
		int numOfPages = 0;
		while (t != null) {
			RID r = page.insertRecord(t.returnTupleByteArray());
			entered = false;
			if (r == null) {
				numOfPages++;
				page = sortPage(page);
				RID dummyRID = page.firstRecord();
				for (int i = 0; i < page.getSlotCnt(); i++) {
					Tuple temp = page.getRecord(dummyRID);
					hf.insertRecord(temp.getTupleByteArray());
					dummyRID = page.nextRecord(dummyRID);
				}
				// System.out.println(hf.getRecCnt());
				v.add(hf);
				System.out.println(hf.getRecCnt());
				// String name = "Sorted" + counter;
				// System.out.println(name);
				hf = new Heapfile("Sorted" + counter);
				counter++;
				page = new HFPage();
				page.init(page.getCurPage(), page);
				r = page.insertRecord(t.getTupleByteArray());
				entered = true;
				// System.out.println(numOfPages
				// + " ------------------------------------------");
			}
			t = s.getNext(new RID());
		}
		if (!entered) {
			page = sortPage(page);
			RID dummyRID = page.firstRecord();
			for (int i = 0; i < page.getSlotCnt(); i++) {
				Tuple temp = page.getRecord(dummyRID);
				hf.insertRecord(temp.getTupleByteArray());
				dummyRID = page.nextRecord(dummyRID);
			}
			v.add(hf);
		}
		for (int i = 0; i < v.size(); i++)
			System.out.println(i + " / " + v.get(i).getRecCnt());
		System.out.println("size " + v.size());
		System.out.println("Number of Pages: " + numOfPages);
		return v;
	}

	private HFPage sortPage(HFPage p) throws Exception {
		Tuple[] tupleArray = new Tuple[p.getSlotCnt()];
		RID dummyrid = p.firstRecord();
		for (int i = 0; i < p.getSlotCnt(); i++) {
			tupleArray[i] = p.getRecord(dummyrid);
			p.deleteRecord(dummyrid);
			dummyrid = p.nextRecord(dummyrid);
		}
		tupleArray = mergeSortPage(tupleArray);
		for (int i = 0; i < tupleArray.length; i++) {
			p.insertRecord(tupleArray[i].getTupleByteArray());
			// System.out.println(Convert.getIntValue(0,
			// tupleArray[i].getTupleByteArray()));
		}
		return p;
	}

	private Tuple[] mergeSortPage(Tuple[] tupleArray) throws Exception {
		if (tupleArray.length == 1)
			return tupleArray;
		Tuple[] firstMid = new Tuple[tupleArray.length / 2];
		Tuple[] secondMid = null;
		if ((tupleArray.length & 1) == 1)
			secondMid = new Tuple[tupleArray.length / 2 + 1];
		else
			secondMid = new Tuple[tupleArray.length / 2];
		System.arraycopy(tupleArray, 0, firstMid, 0, firstMid.length);
		System.arraycopy(tupleArray, tupleArray.length / 2, secondMid, 0,
				secondMid.length);

		Tuple[] firstSortedMid = mergeSortPage(firstMid);
		Tuple[] secondSortedMid = mergeSortPage(secondMid);
		Tuple[] mergeSorted = new Tuple[firstSortedMid.length
				+ secondSortedMid.length];
		int first = 0;
		int second = 0;
		int merge = 0;
		if (order.tupleOrder == TupleOrder.Ascending) {
			if (keyType == global.AttrType.attrInteger) {
				while (first < firstSortedMid.length
						&& second < secondSortedMid.length) {
					if (Convert.getIntValue(0,
							firstSortedMid[first].getTupleByteArray()) < Convert
							.getIntValue(0,
									secondSortedMid[second].getTupleByteArray())) {
						mergeSorted[merge] = firstSortedMid[first];
						first++;
						merge++;
					} else {
						mergeSorted[merge] = secondSortedMid[second];
						second++;
						merge++;
					}
				}
				while (first < firstSortedMid.length) {
					mergeSorted[merge] = firstSortedMid[first];
					first++;
					merge++;
				}
				while (second < secondSortedMid.length) {
					mergeSorted[merge] = secondSortedMid[second];
					second++;
					merge++;

				}
				return mergeSorted;
			} else {
				while (first < firstSortedMid.length
						&& second < secondSortedMid.length) {
					if ((Convert.getStrValue(0,
							firstSortedMid[first].getTupleByteArray(),
							sortFldLen).compareTo(Convert.getStrValue(0,
							secondSortedMid[second].getTupleByteArray(),
							sortFldLen))) < 1) {
						mergeSorted[merge] = firstSortedMid[first];
						first++;
						merge++;
					} else {
						mergeSorted[merge] = secondSortedMid[second];
						second++;
						merge++;
					}
				}
				while (first < firstSortedMid.length) {
					mergeSorted[merge] = firstSortedMid[first];
					first++;
					merge++;
				}
				while (second < secondSortedMid.length) {
					mergeSorted[merge] = secondSortedMid[second];
					second++;
					merge++;

				}
				return mergeSorted;
			}
		} else {
			if (keyType == global.AttrType.attrInteger) {
				while (first < firstSortedMid.length
						&& second < secondSortedMid.length) {
					if (Convert.getIntValue(0,
							firstSortedMid[first].getTupleByteArray()) > Convert
							.getIntValue(0,
									secondSortedMid[second].getTupleByteArray())) {
						mergeSorted[merge] = firstSortedMid[first];
						first++;
						merge++;
					} else {
						mergeSorted[merge] = secondSortedMid[second];
						second++;
						merge++;
					}
				}
				while (first < firstSortedMid.length) {
					mergeSorted[merge] = firstSortedMid[first];
					first++;
					merge++;
				}
				while (second < secondSortedMid.length) {
					mergeSorted[merge] = secondSortedMid[second];
					second++;
					merge++;

				}
				return mergeSorted;
			} else {
				while (first < firstSortedMid.length
						&& second < secondSortedMid.length) {
					if ((Convert.getStrValue(0,
							firstSortedMid[first].getTupleByteArray(),
							sortFldLen).compareTo(Convert.getStrValue(0,
							secondSortedMid[second].getTupleByteArray(),
							sortFldLen))) > 1) {
						mergeSorted[merge] = firstSortedMid[first];
						first++;
						merge++;
					} else {
						mergeSorted[merge] = secondSortedMid[second];
						second++;
						merge++;
					}
				}
				while (first < firstSortedMid.length) {
					mergeSorted[merge] = firstSortedMid[first];
					first++;
					merge++;
				}
				while (second < secondSortedMid.length) {
					mergeSorted[merge] = secondSortedMid[second];
					second++;
					merge++;

				}
				return mergeSorted;
			}
		}
	}

	public void organizer() throws InvalidTupleSizeException,
			FieldNumberOutOfBoundException, HFException, HFBufMgrException,
			HFDiskMgrException, InvalidSlotNumberException,
			SpaceNotAvailableException, IOException, Exception {
		Vector<Heapfile> temp = sort_Runs(sortHeapfile());
		Heapfile t = temp.elementAt(0);
		System.out.println("ENDDDDDD");

	}

	public Vector<Heapfile> sort_Runs(Vector<Heapfile> files)
			throws InvalidTupleSizeException, FieldNumberOutOfBoundException,
			HFException, HFBufMgrException, HFDiskMgrException,
			InvalidSlotNumberException, SpaceNotAvailableException,
			IOException, FileAlreadyDeletedException {

		Vector<Heapfile> temp = files;
		Vector<Heapfile> temp1;

		while (temp.size() != 1) {
			temp1 = run(temp);
			//delete_Files(temp);
			temp = temp1;
		}
		return temp;
	}

	private void delete_Files(Vector<Heapfile> hf)
			throws InvalidSlotNumberException, FileAlreadyDeletedException,
			InvalidTupleSizeException, HFBufMgrException, HFDiskMgrException,
			IOException {
		for (int i = 0; i < hf.size(); i++) {
			hf.get(i).deleteFile();
		}
	}

	private Vector<Heapfile> run(Vector<Heapfile> files)
			throws InvalidTupleSizeException, IOException,
			FieldNumberOutOfBoundException, HFException, HFBufMgrException,
			HFDiskMgrException, InvalidSlotNumberException,
			SpaceNotAvailableException {

		int size = 0;
		int c = 0;
		Vector<Heapfile> h_Files = files;
		Vector<Heapfile> new_Files = new Vector<Heapfile>();
		while (size < h_Files.size()) {
			Heapfile file = null;
			try {
				file = new Heapfile("Sorted1" + counter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector<Scan> h_Scanners = set_Scanners(h_Files, size);
			size += h_Scanners.size();
			Vector<Tuple> tuples = set_Tuples(h_Scanners);
			int least = max_min(tuples , order.tupleOrder == TupleOrder.Ascending , keyType == global.AttrType.attrInteger);

			
			int teto=0; 
			
			

			while (least != -1) {
				c++;
				try {
					file.insertRecord(tuples.get(least).getTupleByteArray());
					System.out.println("final  "+c+"  :" 
							+ Convert.getIntValue(0, tuples.get(least)
									.getTupleByteArray()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // anhe
					// byte
					// arraye
					// ele
					// haktebha
					// gowa
					// el
					// heapfile
				try {
					
					if(least==12)
					{
						teto++;
					}
					if(teto==125 && least==12)
					{
						tuples.setElementAt(null, least);
					}else
					{
						tuples.setElementAt(h_Scanners.get(least).getNext(new RID()), least);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (c == 1580) {
					System.out.println("");
				}

				least = max_min(tuples , order.tupleOrder == TupleOrder.Ascending , keyType == global.AttrType.attrInteger);
			}

			new_Files.add(file);
			counter++;
		}
		return new_Files;

	}

	private Vector<Scan> set_Scanners(Vector<Heapfile> h_Files, int start)
			throws InvalidTupleSizeException, IOException {

		Vector<Scan> h_Scanners = new Vector<Scan>();
		int temp=start;
		for (int i = 0; i < temp + 49; i++) {
			try {
				if(i==11)
				{
					h_Scanners.add(null);
					h_Scanners.add(h_Files.get(i).openScan());					
				}else
				{
					h_Scanners.add(h_Files.get(i).openScan());					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i >= h_Files.size() - 1)
				return h_Scanners;
		}
		return h_Scanners;
	}

	private Vector<Tuple> set_Tuples(Vector<Scan> h_Scanners)
			throws InvalidTupleSizeException, IOException {

		Vector<Tuple> tuples = new Vector<Tuple>();
		for (int i = 0; i < h_Scanners.size(); i++) {

			try {
				if(h_Scanners.get(i)!=null)
				{
					tuples.add(h_Scanners.get(i).getNext(new RID()));					
				}else
				{
					tuples.add(null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tuples;
	}

	private int max_min(Vector<Tuple> tuple, boolean isAscending,
			boolean isInteger) throws FieldNumberOutOfBoundException,
			IOException {
		Vector<Tuple> tuples=tuple;
		int target = 0;
		boolean empty = true;
		int j = 0;
		for (j = 0; j < tuples.size(); j++) {
			if (tuples.get(j) != null) {
				target = j;
				empty = false;
				break;
			}
		}
		if (isAscending) {
			if (isInteger) {
				for (int i = j; i < tuples.size(); i++) {
					if (tuples.get(i) != null
							&& Convert.getIntValue(0, tuples.get(i)
									.getTupleByteArray()) < Convert
									.getIntValue(0, tuples.get(target)
											.getTupleByteArray())) {
						target = i;
					} 
				}
			} else {
				for (int i = j; i < tuples.size(); i++) {
					if (tuples.get(i) != null
							&& Convert.getStrValue(0,
									tuples.get(i).getTupleByteArray(),
									sortFldLen).compareTo(
									Convert.getStrValue(0, tuples.get(target)
											.getTupleByteArray(), sortFldLen)) < 0) {
						target = i;
					} 
				}
			}

		} else {
			if (isInteger) {
				for (int i = j; i < tuples.size(); i++) {
					if (tuples.get(i) != null
							&& Convert.getIntValue(0, tuples.get(i)
									.getTupleByteArray()) > Convert
									.getIntValue(0, tuples.get(target)
											.getTupleByteArray())) {
						target = i;
					} 
				}
			} else {
				for (int i = j; i < tuples.size(); i++) {
					if (tuples.get(i) != null
							&& Convert.getStrValue(0,
									tuples.get(i).getTupleByteArray(),
									sortFldLen).compareTo(
									Convert.getStrValue(0, tuples.get(target)
											.getTupleByteArray(), sortFldLen)) > 0) {
						target = i;
					}
				}
			}
		}
		if (!empty) {
			return target;
		} else {
			return -1;
		}
	}

	/**
	 * Returns the next tuple in sorted order. Note: You need to copy out the
	 * content of the tuple, otherwise it will be overwritten by the next
	 * <code>get_next()</code> call.
	 * 
	 * @return the next tuple, null if all tuples exhausted
	 * @exception IOException
	 *                from lower layers
	 * @exception SortException
	 *                something went wrong in the lower layer.
	 * @exception JoinsException
	 *                from <code>generate_runs()</code>.
	 * @exception UnknowAttrType
	 *                attribute type unknown
	 * @exception LowMemException
	 *                memory low exception
	 * @exception Exception
	 *                other exceptions
	 */
	public Tuple get_next() throws IOException, SortException, UnknowAttrType,
			LowMemException, JoinsException, Exception {
		return null;
	}

	/**
	 * Cleaning up, including releasing buffer pages from the buffer pool and
	 * removing temporary files from the database.
	 * 
	 * @exception IOException
	 *                from lower layers
	 * @exception SortException
	 *                something went wrong in the lower layer.
	 */
	public void close() throws SortException, IOException {
	}
}