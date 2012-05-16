import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class generate {
	
	public static int [] generateRandomInt(int num) throws IOException
	{
		FileWriter f=new FileWriter("test.txt");
		BufferedWriter br=new BufferedWriter(f);
		int [] array = new int[num];
		Random rd = new Random();
		for(int i=0;i<num;i++)
		{
			array[i]=rd.nextInt(1000);
			br.write(array[i]+" ");
		}
		br.close();
		return array;
	}
	
	public static void main(String [] args) throws IOException
	{
		generateRandomInt(3000);
	}
	
	

}
