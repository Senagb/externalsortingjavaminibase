package iterator;

import java.io.IOException;
import java.util.Random;

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
	public String [] generateRandomStrings(int num)
	{
		String [] array= new String [num];
		Random rd = new Random();
		for (int i = 0; i < num; i++) {
			String current = "";
			for (int j = 0; j < 8; j++) {
				int n = rd.nextInt(57) + 65;
				while (n > 90 && n < 97) {
					n = rd.nextInt(57) + 65;
				}
				current += "" + (char) n;
			}
			array[i]=current;
		}
		return array;
	}
	public int [] generateRandomInt(int num)
	{
		int [] array = new int[num];
		Random rd = new Random();
		for(int i=0;i<num;i++)
			array[i]=rd.nextInt(1000);
		return array;
	}
	public MyTest() throws Exception {
		super("hptest");
		//SystemDefs sysdef = new SystemDefs(dbpath, 300, NUMBUF, "Clock");
	//****************************************************************

		System.out
				.println("\n" + "Running " + testName() + " tests...." + "\n");

		SystemDefs sysdef = new SystemDefs(dbpath, 1000, 700, "Clock");

		// Kill anything that might be hanging around
		String newdbpath;
		String newlogpath;
		String remove_logcmd;
		String remove_dbcmd;
		String remove_cmd = "cmd /c del <dbpath, logpath path>";

		newdbpath = dbpath;
		newlogpath = logpath;

		remove_logcmd = remove_cmd + logpath;
		remove_dbcmd = remove_cmd + dbpath;

		// Commands here is very machine dependent. We assume
		// user are on UNIX system here
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}

		remove_logcmd = remove_cmd + newlogpath;
		remove_dbcmd = remove_cmd + newdbpath;

		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}
		hf = new Heapfile("sTest");
		int max=0;
		for(int i=0;i<data1.length;i++)
		{
			if(data1[i].length()>max)
				max=data1[i].length();
		}
		for (int i = 0; i < data1.length; i++) {
			if (data1[i].length() < max) {
				while (data1[i].length() != max) {
					data1[i] += "p";
				}
			}
		}
		
		//data1=generateRandomStrings(9500);
		int [] num=generateRandomInt(1500);
		for (int i = 0; i < num.length; i++) {
			byte[] array = new byte[4];
			Convert.setIntValue(num[i], 0, array);	
			hf.insertRecord(array);
		}

		//		for (int i = 0; i < data1.length; i++) {
//			byte[] array = new byte[data1[i].length() + 2];
//			Convert.setStrValue(data1[i], 0, array);
//			hf.insertRecord(array);
//		}
		AttrType[] attrType = new AttrType[2];
		attrType[0] = new AttrType(AttrType.attrInteger);
		attrType[1] = new AttrType(AttrType.attrString);
		short[] attrSize = new short[2];
		attrSize[0] = REC_LEN1;
		attrSize[1] = REC_LEN2;
		TupleOrder[] order = new TupleOrder[2];
		order[0] = new TupleOrder(TupleOrder.Ascending);
		order[1] = new TupleOrder(TupleOrder.Descending);

		Sort sort = new Sort(attrType, (short) 2, attrSize, null, 0, order[0],
				REC_LEN1, SORTPGNUM);
		sort.setHeapFile(hf);
		sort.organizer();

	
		// Clean up again
		try {
			Runtime.getRuntime().exec(remove_logcmd);
			Runtime.getRuntime().exec(remove_dbcmd);
		} catch (IOException e) {
			System.err.println("IO error: " + e);
		}

	
		//***********************************************************
			}

	public static void main(String[] args) throws Exception {
		MyTest o = new MyTest();
	}
}
