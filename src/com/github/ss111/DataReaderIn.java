package com.github.ss111;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DataReaderIn
{
	public String Read(BufferedReader reader, Socket clientSocket) throws IOException
	{
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String data = reader.readLine();
		reader.close();
		reader = null;
		return data;
	}
}
