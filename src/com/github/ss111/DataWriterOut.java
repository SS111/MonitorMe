package com.github.ss111;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class DataWriterOut
{
	public void Write(PrintWriter writer, Socket clientSocket, String data) throws IOException
	{
		writer = new PrintWriter(clientSocket.getOutputStream(), true);
		writer.println(data);
		writer.close();
		writer = null;
	}
}
