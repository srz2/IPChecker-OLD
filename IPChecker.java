import java.net.*;
import java.io.*;
import java.util.Scanner;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class IPChecker
{
	public static void main(String[] args) throws IOException
	{
		String oldIP = "";
		String newIP = "";

		System.out.println("");
		try{
			BufferedReader reader = new BufferedReader(new FileReader("last_ip.txt"));
			Scanner scan = new Scanner(reader);
			
			oldIP = scan.next();			//Get The old IP

			System.out.println("Old IP Address: " + oldIP);
		}
		catch (FileNotFoundException ex){
			System.out.println("File not found: " + ex);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex);
		}

		URL awsChecker = new URL("http://checkip.amazonaws.com/");
		URLConnection connection = awsChecker.openConnection();

		System.out.println("Checking Current IP Address...");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = "";

		while((line = in.readLine()) != null){
			System.out.println("\tIP Found " + line);
			break;
		}
		in.close();

		newIP = line;

		//System.out.println("\'" + oldIP + "\'" + "\tVS\t" + "\'" + newIP + "\'");

		if(oldIP.equals(newIP)){			//IP Address is the same, no action is required
			System.out.println("Found IP Address is the same as before. No action required.");
			System.out.println("");
			return;
		}
		
//GOT ALL OF MY NECESSARY INFORMATION
		System.out.print("IP Addresses differ. Change Detected. Sending Update...");
		String msg = "The server IP changed!\nThe new IP Address is: " + newIP;
	
		BasicAWSCredentials cruds = new BasicAWSCredentials("AKIAIYWMJAAEIPTOM5SA", "sNro9fpXLln8f+wuDI5zmsLb5fUhVkqeqG3WTk/E");
		AmazonSNSClient snsClient = new AmazonSNSClient(cruds);
		
		try{
			PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:799009161476:Personal_Server", msg);
			PublishResult publishResult = snsClient.publish(publishRequest);

			System.out.println("Response ID - " + publishResult.getMessageId());
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("last_ip.txt"));
			writer.write(newIP);
			writer.close();
		}
		catch(Exception ex){
			System.out.println("ERROR: " + ex);
		}
		System.out.println("");
	}
}