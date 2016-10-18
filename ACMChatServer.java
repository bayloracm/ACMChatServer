import java.io.FileOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.Runnable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;

/** @author Juan Carlos Ramirez and Sean Lavoie ACMChatServer created to demo networking in the 10/18/16 ACM Meeting */
public class ACMChatServer
{
	private static int serverPort = 1337;
	private static ServerSocket servSocket;
	private static int maxClients = 15;
	private static String fileToWriteTo;

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			System.err.println("Usage: ACMChatServer [fileToWriteTo] [OPTIONAL: ServerPort]");
			return;
		}
		else
		{
			fileToWriteTo = args[0];
			if (args.length > 1)
			{
				serverPort = Integer.parseInt(args[2]);
			}
		}

		servSocket = new ServerSocket(ACMChatServer.serverPort);
		servSocket.setReuseAddress(true);

		Executor threadPool = Executors.newFixedThreadPool(ACMChatServer.maxClients);

		System.out.println("Started Server...");

		while (true)
		{
			Socket clientSocket = ACMChatServer.servSocket.accept();
			System.out.println("Servicing - " + clientSocket.toString());
			threadPool.execute(new clientHandler(clientSocket, new FileOutputStream(fileToWriteTo, true)));
		}
	}
}

class clientHandler implements Runnable
{
	Socket myClient;
	int bufferSize = 1024;
	FileOutputStream fileToWriteTo;

	clientHandler(Socket newConnection, FileOutputStream fo)
	{
		myClient = newConnection;
		fileToWriteTo = fo;
	}

	public void run()
	{
		byte buffer[] = new byte[bufferSize];
		try
		{
			StringBuilder chatMessage = new StringBuilder();
			while (myClient.getInputStream().read(buffer) != -1)
			{
				chatMessage.append(new String(buffer).trim());
			}
			if (chatMessage.length() > 0)
			{
				System.out.println("Received a message: " + chatMessage.toString());
				fileToWriteTo.write(sanatize(chatMessage.toString().trim()).getBytes());
			}
			myClient.close();
			return;
		}
		catch (IOException e)
		{
			System.err.println("There was an error reading. Killing Connection");
			try
			{
				myClient.close();
			}
			catch (IOException e2)
			{
				System.err.println("There was an error killing the connection.");
				return;
			}
		}
	}

	public String sanatize(String str)
	{
		String payload;
		String username = str.substring(0, str.indexOf("\n"));
		str = str.substring(str.indexOf("\n"));
		str.replaceAll("<", "{");
		str.replaceAll(">", "}");
		payload = "<ol class=\"chat\">" +
			    "<li class=\"other\">" +
			        "<div class=\"avatar\"><img src=\"http://i.imgur.com/DY6gND0.png\" draggable=\"false\"/></div>" +
			      "<div class=\"msg\">" +
			        "<p><b>" + username + "</b></p>" +
			        "<p>" + str + "</p>" +
			        "<time>" + new SimpleDateFormat("HHmm").format(new Date()) + "</time>" +
			      "</div>" +
			    "</li>" +
			"</ol>";
		return payload;
	}
}
