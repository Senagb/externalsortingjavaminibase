package iterator;

import global.AttrType;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.HFPage;
import heap.Heapfile;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Scan;
import heap.Tuple;

import java.io.IOException;
import java.util.Vector;

/**
 * The Sort class sorts a file. All necessary information are passed as
 * arguments to the constructor. After the constructor call, the user can
 * repeatly call <code>get_next()</code> to get tuples in sorted order. After
 * the sorting is done, the user should call <code>close()</code> to clean up.
 */
public class Sort extends Iterator implements GlobalConst {

	private int keyType;
	protected Heapfile gFile; // user given file
	protected boolean Ascending = true;

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
		keyType=in[0].attrType;
		_am = am;
		_sort_fld = sort_fld;
		order = sort_order;
		_n_pages = n_pages;
		_in = in;
		n_cols = len_in;
		str_lens = str_sizes;
		sortFldLen = sort_fld_len;
	}

	// pass 0
	public Vector<Heapfile> sortHeapfile() throws Exception {
		Vector<Heapfile> v = new Vector<Heapfile>();
		Heapfile hf = new Heapfile("Sorted");
		Scan s = gFile.openScan();
		Tuple t = s.getNext(new RID());
		HFPage page = new HFPage();
		int counter = 0;
		while (t != null) {
			RID r = page.insertRecord(t.getTupleByteArray());
			if (r == null) {
				page = sortPage(page);
				byte[] temp = page.getHFpageArray();
				hf.insertRecord(temp);
				v.add(hf);
				hf = new Heapfile("Sorted" + counter);
				counter++;
				s = hf.openScan();
				page = new HFPage();
				page.insertRecord(t.getTupleByteArray());

			}
			t = s.getNext(new RID());
		}
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
					if (firstSortedMid[first].getIntFld(1) < secondSortedMid[second]
							.getIntFld(1)) {
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
					if ((firstSortedMid[first].getStrFld(1)
							.compareTo(secondSortedMid[second].getStrFld(1))) < 1) {
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
					if (firstSortedMid[first].getIntFld(1) > secondSortedMid[second]
							.getIntFld(1)) {
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
					if ((firstSortedMid[first].getStrFld(1)
							.compareTo(secondSortedMid[second].getStrFld(1))) > 1) {
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

	public static void main(String[] args)
			throws FieldNumberOutOfBoundException, IOException {
	}
}
