package iterator;

import global.AttrType;
import global.Convert;
import global.RID;
import global.SystemDefs;
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import diskmgr.Page;

public class externalSort extends Sort {
	private boolean first = true;
	private Scan s = null;

	// constructor
	public externalSort(AttrType[] in, short len_in, short[] str_sizes,
			Iterator am, int sort_fld, TupleOrder sort_order, int sort_fld_len,
			int n_pages, Heapfile hf) throws IOException, SortException,
			InvalidTypeException, InvalidTupleSizeException,
			IteratorBMException {
		super(in, len_in, str_sizes, am, sort_fld, sort_order, sort_fld_len,
				n_pages);
		gFile = hf;
		// TODO Auto-generated constructor stub
	}

	// pass 0 to sort each page of the heap file and make each page in a heap
	// file alone
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
		Vector<Heapfile> v = new Vector<Heapfile>(); // vector of the result
														// heap file
		Heapfile hf = new Heapfile("Sorted");
		int t1 = gFile.getRecCnt();
		System.out.println(t1);

		Scan s = gFile.openScan(); // open scan on the heap file
		Tuple t = s.getNext(new RID());
		Page p = new Page();
		HFPage page = new HFPage(p);
		page.init(page.getCurPage(), page);
		int counter = 0;
		boolean entered = false;
		int numOfPages = 0;
		while (t != null) {// loop until the input file is finished
			RID r = page.insertRecord(t.returnTupleByteArray());// fill the page
			entered = false;
			if (r == null) {// if the page iss full
				numOfPages++;
				page = sortPage(page);
				RID dummyRID = page.firstRecord();
				// insert all the record in the heap file
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
				System.out.println(numOfPages
						+ " ------------------------------------------ "
						+ SystemDefs.JavabaseBM.getNumUnpinnedBuffers());
			}
			// SystemDefs.JavabaseBM.unpinPage(page.getCurPage(), true);
			t = s.getNext(new RID());
		}
		// last page if not reformated
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

	// sort each page on its own
	private HFPage sortPage(HFPage p) throws Exception {
		Tuple[] tupleArray = new Tuple[p.getSlotCnt()];// make array of tuples
		RID dummyrid = p.firstRecord();
		for (int i = 0; i < p.getSlotCnt(); i++) {
			tupleArray[i] = p.getRecord(dummyrid);
			p.deleteRecord(dummyrid);
			dummyrid = p.nextRecord(dummyrid);
		}
		tupleArray = mergeSortPage(tupleArray); // apply merge sort on the array
												// of tuples
		for (int i = 0; i < tupleArray.length; i++) {
			p.insertRecord(tupleArray[i].getTupleByteArray()); // reinsert the
																// array of
																// tuple in the
																// page

		}
		s.closescan();
		return p;
	}

	// merge sort used to sort page
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

	// this method is main class method that call to sort the file
	public Heapfile organizer() throws InvalidTupleSizeException,
			FieldNumberOutOfBoundException, HFException, HFBufMgrException,
			HFDiskMgrException, InvalidSlotNumberException,
			SpaceNotAvailableException, IOException, Exception {
		Vector<Heapfile> temp = sort_Runs(sortHeapfile());// take the vector of
															// sorted pages in
															// the form of heap
															// file each
		Heapfile t = temp.elementAt(0);
		System.out.println("ENDDDDDD");
		return t; // return the sorted heap file

	}

	// runs to be created and sorted
	public Vector<Heapfile> sort_Runs(Vector<Heapfile> files)
			throws InvalidTupleSizeException, FieldNumberOutOfBoundException,
			HFException, HFBufMgrException, HFDiskMgrException,
			InvalidSlotNumberException, SpaceNotAvailableException,
			IOException, FileAlreadyDeletedException {
		// two vectors to switch between
		Vector<Heapfile> temp = files;
		Vector<Heapfile> temp1;

		while (temp.size() != 1) {
			temp1 = run(temp);
			delete_Files(temp);// delete files from the last run call
			temp = temp1;
		}
		return temp;
	}

	// delete heap files to free the DBMS and BUFFER
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
		Tuple temp_T;
		Vector<Heapfile> h_Files = files;
		Vector<Heapfile> new_Files = new Vector<Heapfile>();
		while (size < h_Files.size()) {
			Heapfile file = null;
			try {
				file = new Heapfile("asser" + counter);// create file for the
														// output
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector<Scan> h_Scanners = set_Scanners(h_Files, size); // get the
																	// canner
																	// vector of
																	// the
																	// entered
																	// hap file
																	// vector
			System.out.println(h_Scanners.size());
			int[] pageId = new int[h_Scanners.size()];
			// get the ids of the pages to be sorted
			for (int i = 0; i < pageId.length; i++) {
				Scan temp;
				try {
					temp = h_Files.get(i).openScan();
					RID temp12 = new RID();
					Tuple t = temp.getNext(temp12);
					pageId[i] = temp12.pageNo.pid;
					temp.closescan();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			size += h_Scanners.size();

			Scan sc = files.get(1).openScan();
			RID k = new RID();
			Tuple t = sc.getNext(k);
			t = sc.getNext(k);
			int z = 0;
			FileWriter f = new FileWriter("fileTest2.txt");
			BufferedWriter br = new BufferedWriter(f);

			while (t != null) {
				br.write("rez2 " + z + "	"
						+ Convert.getIntValue(0, t.getTupleByteArray()) + '\n');
				System.out.println("final  " + c + "  :"
						+ Convert.getIntValue(0, t.getTupleByteArray()));
				t = sc.getNext(k);
				z++;
			}
			br.close();
			Vector<Tuple> tuples = set_Tuples(h_Scanners);
			int least = max_min(
					tuples, // get the mini or max according to the sorter
							// request
					order.tupleOrder == TupleOrder.Ascending,
					keyType == global.AttrType.attrInteger);
			// insert the record and loop until the end
			while (least != -1) {
				c++;
				RID temp = new RID();
				try {
					file.insertRecord(tuples.get(least).getTupleByteArray());
					System.out.println("final  "
							+ c
							+ "  :"
							+ Convert.getIntValue(0, tuples.get(least)
									.getTupleByteArray()) + "      " + least);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {

					temp_T = h_Scanners.get(least).getNext(temp);
					if (temp.pageNo.pid == pageId[least] && temp.slotNo == 0) {
						temp_T = null;
						h_Scanners.get(least).closescan();
					}

					tuples.setElementAt(temp_T, least);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (c == 245) {
					System.out.println("");
				}

				least = max_min(tuples,
						order.tupleOrder == TupleOrder.Ascending,
						keyType == global.AttrType.attrInteger);
			}

			new_Files.add(file);
			counter++;
		}
		Scan sc = new_Files.get(0).openScan();
		Tuple t = sc.getNext(new RID());
		int z = 0;
		FileWriter f = new FileWriter("fileTest3.txt");
		BufferedWriter br = new BufferedWriter(f);

		while (t != null) {
			br.write("rez2 " + z + "	"
					+ Convert.getIntValue(0, t.getTupleByteArray()) + '\n');
			System.out.println("final  " + c + "  :"
					+ Convert.getIntValue(0, t.getTupleByteArray()));
			t = sc.getNext(new RID());
			z++;
		}
		br.close();

		return new_Files;

	}

	// ge the scanner of the vector of heap file
	private Vector<Scan> set_Scanners(Vector<Heapfile> h_Files, int start)
			throws InvalidTupleSizeException, IOException {

		Vector<Scan> h_Scanners = new Vector<Scan>();
		int temp = start;
		for (int i = temp; SystemDefs.JavabaseBM.getNumUnpinnedBuffers() > 2; i++) {
			try {
				h_Scanners.add(h_Files.get(i).openScan());
				System.out.println(SystemDefs.JavabaseBM
						.getNumUnpinnedBuffers());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i >= h_Files.size() - 1)
				return h_Scanners;
		}
		return h_Scanners;
	}

	// get the tuples from the files in order and just return them in vector
	private Vector<Tuple> set_Tuples(Vector<Scan> h_Scanners)
			throws InvalidTupleSizeException, IOException {

		Vector<Tuple> tuples = new Vector<Tuple>();
		for (int i = 0; i < h_Scanners.size(); i++) {

			try {
				tuples.add(h_Scanners.get(i).getNext(new RID()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tuples;
	}

	// get the min or max of the given vector according to the user request
	private int max_min(Vector<Tuple> tuple, boolean isAscending,
			boolean isInteger) throws FieldNumberOutOfBoundException,
			IOException {
		Vector<Tuple> tuples = tuple;
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
				try {
					int x1 = Convert.getIntValue(0, tuples.get(1)
							.getTupleByteArray());
					int x2 = Convert.getIntValue(0, tuples.get(target)
							.getTupleByteArray());
					System.out.println();
				} catch (Exception e) {

				}
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
	@Override
	// get the nest tuple of the sorted file
	public Tuple get_next() throws IOException, SortException, UnknowAttrType,
			LowMemException, JoinsException, Exception {

		if (first) {
			Heapfile file = organizer();
			s = file.openScan();
			first = false;
			return s.getNext(new RID());
		} else {
			return s.getNext(new RID());
		}

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
	@Override
	// close the scan and end
	public void close() throws SortException, IOException {
		if (s != null)
			s.closescan();
	}
}
