package snippet;

public class Snippet {
	
	SystemDefs sysdef = new SystemDefs(dbpath, 300, NUMBUF, "Clock");
	
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
	
}

