package iterator;

import global.AttrType;
import global.GlobalConst;
import global.TupleOrder;
import heap.Heapfile;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;

import java.io.IOException;

/**
 * The Sort class sorts a file. All necessary information are passed as
 * arguments to the constructor. After the constructor call, the user can
 * repeatly call <code>get_next()</code> to get tuples in sorted order. After
 * the sorting is done, the user should call <code>close()</code> to clean up.
 */
public class Sort extends Iterator implements GlobalConst {

	protected int keyType;
	protected int counter = 0;
	protected Heapfile gFile; // user given file
	protected static String data1[] = { "raghu", "xbao", "cychan", "leela",
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
	protected static final int ARBIT_RUNS = 10;
	protected AttrType[] _in;// size will always be 1
	protected short n_cols; // equal 1
	protected short[] str_lens;// size is one and assigned from the constructor
	protected Iterator _am;// given at the constructor
	protected int _sort_fld;// unsued in our case
	protected TupleOrder order;
	protected int _n_pages;
	protected byte[][] bufs;// array of the bytes that is read and to be sorted
	protected boolean first_time;// first time to call pass 0
	protected int Nruns;
	protected int max_elems_in_heap;
	protected int sortFldLen; // length of the field you are sorting on
	protected int tuple_size;
	// protected pnodeSplayPQ Q;
	protected Heapfile[] temp_files; // replaced by one heapfile
	protected int n_tempfiles;
	protected Tuple output_tuple;
	protected int[] n_tuples;
	protected int n_runs;// number of runs
	// private Tuple op_buf;
	// private OBuf o_buf;
	// private SpoofIbuf[] i_buf;
//	private PageId[] bufs_pids; // ids of pages in the buffer manager
//	private boolean useBM = true; // flag for whether to use buffer manager

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