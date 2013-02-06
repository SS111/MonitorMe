package com.github.ss111;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class DataWriter
{
	public void Write(PrintWriter writer, Socket clientSocket, String data) throws IOException
	{
		writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
		writer.println(data);
		writer.close();
		writer = null;
	}
}
