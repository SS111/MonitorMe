package com.github.ss111;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MonitorMeCore extends JavaPlugin
{
	public ServerSocket ss;
	public Socket cs;
	public PrintWriter out;
	public BufferedReader in;
	public String PasswordHash;
	public Boolean Allowed;
	
	@Override
	public void onEnable()
	{
		final FileConfiguration config = this.getConfig();
		config.addDefault("Password", "daef4953b9783365cad6615223720506cc46c5167cd16ab500fa597aa08ff964eb24fb19687f34d7665f778fcb6c5358fc0a5b81e1662cf90f73a2671c53f991");
		config.addDefault("Port", 3002);
		config.options().header("NOTE: To make your password, go to http://www.fileformat.info/tool/hash.htm and get the SHA-512 hash! The default password is \"test123\"");
		config.options().copyDefaults(true);
		saveConfig();
		
		getLogger().info("MonitorMe loaded! Starting mini-server...");
		
		try 
		{
			ss = new ServerSocket(config.getInt("Port"));
			getLogger().info("Mini-server is enabled and listening on port " + config.getInt("Port") + "!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			getLogger().severe("Mini-server cannot be started on port " + config.getInt("Port") + "! Is the port forwarding set up correctly?");
		}
		
		PasswordHash = config.getString("Password");
		
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					cs = ss.accept();
					
					getLogger().info("A client connected to MonitorMe's mini-server with IP: " + cs.getRemoteSocketAddress().toString() + ". Asking for authentication.");
					
					in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
					out = new PrintWriter(cs.getOutputStream(), true);

					while (Allowed == false)
					{
						
							String data = in.readLine();
							
							String FinalHash = DigestUtils.sha512Hex(data);
							
							if (FinalHash.equals(PasswordHash))
							{
								Allowed = true;
								getLogger().info("Client connected with the correct password.");
							}
							else
							{
								getLogger().warning("The client connected with bad SHA-512 hash: " + FinalHash);
								
								data = null;
								FinalHash = null;
							}
					}
				} 
				catch (IOException e)
				{
					getLogger().severe("A severe error occured while a client was connecting.");
					
					e.printStackTrace();
				}
			}
			
		});
	}
	
	@Override
	public void onDisable()
	{
		try
		{
			ss.close();
			cs.close();
		} 
		catch (IOException e)
		{
			getLogger().warning("An error occurred while trying to close the server or client socket.");
			e.printStackTrace();
		}
	}
	
	}
