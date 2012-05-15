package iterator;

import global.AttrType;
import global.Convert;
import global.GlobalConst;
import global.SystemDefs;
import global.TupleOrder;
import heap.Heapfile;

public class MyTest extends TestDriver implements GlobalConst {
	Heapfile hf;
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
			"meltz", "raghu", "xbao", "cychan", "leela", "ketola", "soma",
			"ulloa", "dhanoa", "dsilva", "kurniawa", "dissoswa", "waic",
			"susanc", "kinc", "marc", "scottc", "yuc", "ireland", "rathgebe",
			"joyce", "daode", "yuvadee", "he", "huxtable", "muerle",
			"flechtne", "thiodore", "jhowe", "frankief", "yiching", "xiaoming",
			"jsong", "yung", "muthiah", "bloch", "binh", "dai", "hai", "handi",
			"shi", "sonthi", "evgueni", "chung-pi", "chui", "siddiqui", "mak",
			"tak", "sungk", "randal", "barthel", "newell", "schiesl", "neuman",
			"heitzman", "wan", "gunawan", "djensen", "juei-wen", "josephin",
			"harimin", "xin", "zmudzin", "feldmann", "joon", "wawrzon",
			"yi-chun", "wenchao", "seo", "karsono", "dwiyono", "ginther",
			"keeler", "peter", "lukas", "edwards", "mirwais", "schleis",
			"haris", "meyers", "azat", "shun-kit", "robert", "markert", "wlau",
			"honghu", "guangshu", "chingju", "bradw", "andyw", "gray",
			"vharvey", "awny", "savoy", "meltz", "raghu", "xbao", "cychan",
			"leela", "ketola", "soma", "ulloa", "dhanoa", "dsilva", "kurniawa",
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
	private static int NUM_RECORDS = data1.length;
	private static short REC_LEN1 = 8;
	private static short REC_LEN2 = 160;
	private static int SORTPGNUM = 12;

	public MyTest() throws Exception {
		super("sorttest");
		SystemDefs sysdef = new SystemDefs(dbpath, 300, NUMBUF, "Clock");
		hf = new Heapfile("scanTest");
		for (int i = 0; i < data1.length; i++) {
			if (data1[i].length() != 8) {
				while (data1[i].length() != 8) {
					data1[i] += "p";
				}
			}
		}
		for (int i = 0; i < data1.length; i++) {
			byte[] array = new byte[data1[i].length() + 2];
			Convert.setStrValue(data1[i], 0, array);
			hf.insertRecord(array);
		}
		AttrType[] attrType = new AttrType[2];
		attrType[0] = new AttrType(AttrType.attrString);
		attrType[1] = new AttrType(AttrType.attrString);
		short[] attrSize = new short[2];
		attrSize[0] = REC_LEN1;
		attrSize[1] = REC_LEN2;
		TupleOrder[] order = new TupleOrder[2];
		order[0] = new TupleOrder(TupleOrder.Ascending);
		order[1] = new TupleOrder(TupleOrder.Descending);

		Sort sort = new Sort(attrType, (short) 2, attrSize, null, 0, order[0],
				8, SORTPGNUM);
		sort.setHeapFile(hf);
		sort.sortHeapfile();
	}

	public static void main(String[] args) throws Exception {
		MyTest o = new MyTest();
	}
}
