package test;

import global.GlobalConst;
import global.RID;
import global.SystemDefs;
import heap.FieldNumberOutOfBoundException;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.Heapfile;
import heap.Scan;
import heap.Tuple;

import java.io.IOException;

public class mm {

	public static void main(String[] args)
			throws FieldNumberOutOfBoundException, IOException, Exception,
			HFBufMgrException, HFDiskMgrException {
		TestDriver t = new TestDriver("sor");

		SystemDefs sysdef = new SystemDefs(t.dbpath, 300, GlobalConst.NUMBUF,
				"Clock");

		// Kill anything that might be hanging around
		String newdbpath;
		String newlogpath;
		String remove_logcmd;
		String remove_dbcmd;
		String remove_cmd = "cmd /c del <dbpath, logpath path>";

		newdbpath = t.dbpath;
		newlogpath = t.logpath;

		remove_logcmd = remove_cmd + t.logpath;
		remove_dbcmd = remove_cmd + t.dbpath;

		// Commands here is very machine dependent. We assume
		// user are on UNIX system here
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("" + e);
		}

		remove_logcmd = remove_cmd + newlogpath;
		remove_dbcmd = remove_cmd + newdbpath;

		// This step seems redundant for me. But it's in the original
		// C++ code. So I am keeping it as of now, just in case I
		// I missed something
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("" + e);
		}

		String data1[] = { "raghu", "xbao", "cychan", "leela", "ketola",
				"soma", "ulloa", "dhanoa", "dsilva", "kurniawa", "dissoswa",
				"waic", "susanc", "kinc", "marc", "scottc", "yuc", "ireland",
				"rathgebe", "joyce", "daode", "yuvadee", "he", "huxtable",
				"muerle", "flechtne", "thiodore", "jhowe", "frankief",
				"yiching", "xiaoming", "jsong", "yung", "muthiah", "bloch",
				"binh", "dai", "hai", "handi", "shi", "sonthi", "evgueni",
				"chung-pi", "chui", "siddiqui", "mak", "tak", "sungk",
				"randal", "barthel", "newell", "schiesl", "neuman", "heitzman",
				"wan", "gunawan", "djensen", "juei-wen", "josephin", "harimin",
				"xin", "zmudzin", "feldmann", "joon", "wawrzon", "yi-chun",
				"wenchao", "seo", "karsono", "dwiyono", "ginther", "keeler",
				"peter", "lukas", "edwards", "mirwais", "schleis", "haris",
				"meyers", "azat", "shun-kit", "robert", "markert", "wlau",
				"honghu", "guangshu", "chingju", "bradw", "andyw", "gray",
				"vharvey", "awny", "savoy", "meltz" };

		Heapfile hf = new Heapfile("ahmad");
		Tuple t1 = new Tuple();
		for (int i = 0; i < data1.length; i++) {
			t1.setStrFld(1, data1[i]);
			hf.insertRecord(t1.getTupleByteArray());
		}
		Scan s = hf.openScan();
		while (t != null) {
			t1 = s.getNext(new RID());
			if (t != null) {
				String s1 = t1.getStrFld(1);
				System.out.println(s1);

			}
		}

	}
}
