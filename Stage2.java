import java.net.*;
import java.io.*;
import java.util.*;

public class Stage2 {

	private static String jobReply = "";
	private static int submitTime;
	private static int jobID;
	private static int estRuntime;
	private static int core;
	private static int memory;
	private static int disk;
	private static int nRecs;
	private static int recLen;
	private static List<String[]> servers = new ArrayList<String[]>();

   public static void main (String[] args) throws Exception {
		Socket s = new Socket("127.0.0.1", 50000);
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		String str = "";
		String reply = "";
		
		dout.write(("HELO\n").getBytes());
		dout.flush();
		
		str = din.readLine();

		dout.write(("AUTH " + System.getProperty("user.name") + "\n").getBytes());
		dout.flush();
		
		str = din.readLine();
		
		boolean serverInfo = false;
		int lowCount = 0;
		int currCount = 0;
		String shortestServer;
		String ans = "";
		
		while (!jobReply.equals("NONE")) {
			if (jobReply.equals("JOBN")){
			
				// Avail more precise FF
				dout.write(("GETS Avail " + core + " " + memory + " " + disk + "\n").getBytes());
				dout.flush();
				getDataInfo(din.readLine());
				
				// If none currenty available, simple FF using capable
				if (nRecs == 0) {
					dout.write(("OK\n").getBytes());
					dout.flush();
					
					din.readLine();
					
					dout.write(("GETS Capable " + core + " " + memory + " " + disk + "\n").getBytes());
					dout.flush();
					getDataInfo(din.readLine());
				
				}
				
				dout.write(("OK\n").getBytes());
				dout.flush();
				
				// Reset servers list
				servers = new ArrayList<String[]>();
				
				for (int i = 0; i < nRecs; i++) {
					servers.add(parseServer(din.readLine()));
				}
				
				dout.write(("OK\n").getBytes());
				dout.flush();
				din.readLine();
				
				dout.write(("SCHD "+ jobID + " " + servers.get(0)[0] + " " + servers.get(0)[1] + "\n").getBytes());
				dout.flush();
			   din.readLine();
			}
			
			dout.write(("REDY\n").getBytes());
			dout.flush();
			
			str = din.readLine();
			getJobInfo(str);
				
		}
		
		
		dout.write(("QUIT\n").getBytes());
      din.close();
      dout.close();
      s.close();
   }
   
   public static void getJobInfo(String str) {
      String[] ans = str.split(" ", 7);
      jobReply = ans[0];
      if (!jobReply.equals("JOBN")) {
      	return;
      }
      submitTime = Integer.parseInt(ans[1]);
      jobID = Integer.parseInt(ans[2]);
      estRuntime = Integer.parseInt(ans[3]);
      core = Integer.parseInt(ans[4]);
      memory = Integer.parseInt(ans[5]);
      disk = Integer.parseInt(ans[6]);
   }
   
   public static void getDataInfo(String str) {
   	String[] ans = str.split(" ", 3);
      nRecs = Integer.parseInt(ans[1]);
      recLen = Integer.parseInt(ans[2]);
   }
   
   public static String[] parseServer(String str) {
   	String[] ans = str.split(" ", 9);
   	return ans;
   }
   
   public static boolean schdCheck(String str) {
   	if (str.contains("ERR:")) {
   		return false;
   	} else {
   		return true;
   	}
   
   }
   

}
