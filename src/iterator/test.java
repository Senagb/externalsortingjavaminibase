package iterator;

import java.io.IOException;

import chainexception.ChainException;
import global.PageId;
import global.RID;
import global.SystemDefs;
import heap.HFPage;
import heap.Heapfile;
import heap.Tuple;

public class test {
	HFPage header;
	Tuple tup;
	RID rid;
	boolean first = true;
	Heapfile hf;
	HFPage temp;
	int num;
	int lastPage;

	public test(Heapfile hf,String fileName) throws ChainException, IOException {
		this.hf = hf;
		PageId headerId;
			headerId = SystemDefs.JavabaseDB.get_file_entry(fileName);
			if (headerId == null) {
				header = new HFPage();
				headerId = SystemDefs.JavabaseBM.newPage(header, 1);
				SystemDefs.JavabaseDB.add_file_entry(fileName, headerId);
				header.init(headerId, header);
				SystemDefs.JavabaseBM.unpinPage(headerId, true);
			
			} else {
				header = new HFPage();
				SystemDefs.JavabaseBM.pinPage(headerId, header, false);
				SystemDefs.JavabaseBM.unpinPage(headerId, true);
			}
		SystemDefs.JavabaseBM.pinPage(header.getCurPage(), header, false);
		num = 3;
	}

	public Tuple getNext(RID rid) throws IOException, ChainException {
		if (first) {
			byte[]arr = header.getpage();
			lastPage=arr[50]+1;
			temp = new HFPage();
			PageId id = new PageId(num++);
			SystemDefs.JavabaseBM.pinPage(id, temp, false);
			SystemDefs.JavabaseBM.unpinPage(id, true);
			this.rid = temp.firstRecord();
			first = false;
			rid.copyRid(this.rid);
			return temp.returnRecord(this.rid);
		} else {
			this.rid = temp.nextRecord(this.rid);
			if (this.rid != null) {
				rid.copyRid(this.rid);
				return temp.returnRecord(this.rid);
			} else {
				if (num < lastPage+1) {
					temp = new HFPage();
					PageId id = new PageId(num++);
					SystemDefs.JavabaseBM.pinPage(id, temp, false);
					SystemDefs.JavabaseBM.unpinPage(id, true);
					this.rid = temp.firstRecord();
					tup = temp.returnRecord(this.rid);
					rid.copyRid(this.rid);
					return tup;
				} else {
					SystemDefs.JavabaseBM.unpinPage(header.getCurPage(), true);
					return null;

				}

			}
		}

	}

	public boolean position(RID rid) throws ChainException, IOException {
		HFPage temp = new HFPage();
		SystemDefs.JavabaseBM.pinPage(rid.pageNo, temp, false);
		SystemDefs.JavabaseBM.unpinPage(rid.pageNo, false);
		try {
			Tuple tuple = temp.returnRecord(rid);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public void closescan() {
		 header=null;
		 tup=null;
		 rid=null;
		 first = true;
		 hf=null;
		 temp=null;
		 num=1;
		 lastPage=-1;
	}

}
