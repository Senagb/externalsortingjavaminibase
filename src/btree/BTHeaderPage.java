package btree;

import java.io.IOException;

import diskmgr.Page;

import global.PageId;
import heap.HFPage;

public class BTHeaderPage extends HFPage {

	private int Keytype = Integer.MIN_VALUE;
	private int keySize = -1;
	private String Name = "";

	public BTHeaderPage(Page p) {
		// TODO Auto-generated constructor stub
		super(p);
	}

	public BTHeaderPage() {
		// TODO Auto-generated constructor stub
	}

	public int getKeytype() {
		return Keytype;
	}

	public void setKeytype(int keytype) {

		Keytype = keytype;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keynSize) {
		this.keySize = keynSize;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	//
	// public PageId getId() {
	// return id;
	// }
	//
	// public void setId(PageId id) {
	// this.id = id;
	// }
	public PageId getRoot() throws IOException {
		return super.getNextPage();
	}

	public void setRoot(PageId id) throws IOException {
		super.setNextPage(id);
	}
}
