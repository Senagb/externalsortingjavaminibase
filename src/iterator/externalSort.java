package iterator;

import global.AttrType;
import global.TupleOrder;

import java.io.IOException;

public class externalSort extends Sort {

	public externalSort(AttrType[] in, short len_in, short[] str_sizes,
			Iterator am, int sort_fld, TupleOrder sort_order, int sort_fld_len,
			int n_pages) throws IOException, SortException {

		super(in, len_in, str_sizes, am, sort_fld, sort_order, sort_fld_len,
				n_pages);
		// TODO Auto-generated constructor stub
	
	}

	
	
}
