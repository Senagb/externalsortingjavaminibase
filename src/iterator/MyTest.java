package iterator;

import java.io.IOException;

import chainexception.ChainException;

import diskmgr.DB;
import global.Convert;
import global.GlobalConst;
import global.RID;
import global.SystemDefs;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.HFException;
import heap.Heapfile;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.SpaceNotAvailableException;
import heap.Tuple;

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
		"meltz" };
	public MyTest() throws IOException, ChainException
	{
		super("sorttest");
		SystemDefs sysdef = new SystemDefs(dbpath, 300, NUMBUF, "Clock");
		hf= new Heapfile("scanTest");
		for(int i=0;i<data1.length;i++)
		{
			byte [] array = new byte [data1[i].length()+2];
			Convert.setStrValue(data1[i], 0, array);
			hf.insertRecord(array);
		}
		test tt= new test(hf, "scanTest");
		RID rr= new RID();
		for(int i=0;i<data1.length;i++){
		Tuple l=tt.getNext(rr);
		byte [] o=l.getTupleByteArray();
		String temp="";
		temp=Convert.getStrValue(0, o, o.length);
		System.out.println(temp);
		}
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException, ChainException {
		MyTest o = new MyTest();
	}
}
