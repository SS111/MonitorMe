package com.github.ss111;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class MonitorMeCore extends JavaPlugin
{
	public ServerSocket ss;
	public static Socket cs;
	public String PasswordHash;
	public PrintWriter pw;
	public BufferedReader br;
	public Boolean Accepted = false;
	
	public void Log(String message, String type)
	{
		if (type.equals("info"))
		{
			getLogger().info(message);
		}
		else if (type.equals("warning"))
		{
			getLogger().warning(message);
		}
		else if (type.equals("severe"))
		{
			getLogger().severe(message);
		}
		else
		{
			getLogger().warning("Invalid use of core function: Log");
		}
	}
	
	public void ExecuteCommand(String Command) throws IOException
	{
		getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
		DataWriter writer = new DataWriter();
        writer.Write(pw, cs, "info: " + "Command was completed successfuly.");
	}
	
	public void Chat(String Message) throws IOException
	{
		getServer().broadcastMessage(Message);
		DataWriter writer = new DataWriter();
		 writer.Write(pw, cs, "info: " + "Chat message was sent successfuly.");
	}
	
	public void EnableOrDisableListener(String type)
	{
		if (type.equals("enable"))
		{
			getServer().getPluginManager().registerEvents(new DataListener(), this);
		}
		else if (type.equals("disable"))
		{
			HandlerList.unregisterAll(new DataListener());
		}
		else
		{
			getLogger().warning("Invalid use of core function: EnableOrDisableListener");
		}
	}
	
	public static Socket GetClientSocket()
	{
		return cs;
	}
	
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
		
		WaitForAccept();
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
	
	public void WaitForAccept()
	{
		final DataReader reader = new DataReader();
		
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (Accepted.equals(false))
					{
						cs = ss.accept();
						Accepted = true;
						Log("A client connected with IP: " + cs.getRemoteSocketAddress().toString() + ". Asking for authentication.", "info");
					}
					else
					{
						
					}
					
					String RemotePasswordPlainText = reader.Read(br, cs);
					String RemotePasswordHash = DigestUtils.sha512Hex(RemotePasswordPlainText);
					
					if (RemotePasswordHash.equals(PasswordHash))
					{
						Log("Client connected with correct password.", "info");
						
						EnableOrDisableListener("enable");
						ListenForData();
						
						//Make sure that the client socket hasen't closed... this should solve a lot of problems... I hope
						while (true)
						{
							if (cs.isClosed())
							{
								Accepted = false;
								EnableOrDisableListener("disable");
								run();
								break;
							}
							else
							{

							}
						}
					}
					else
					{
						Log("Client connected with bad password hash: " + RemotePasswordHash, "warning");
						RemotePasswordPlainText = null;
						RemotePasswordHash = null;
						
						run();
					}
					
				}
				catch (IOException e)
				{
					Log("A severe error occured while a client was connecting or the client disconnected without entering the proper password.", "warning");
					
					cs = null;
					Accepted = false;
							
					run();
				}
			}
			
		});
	}
	
	public void ListenForData()
	{
		final DataReader reader = new DataReader();
		
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{

			@Override
			public void run()
			{
				while (Accepted.equals(true))
				{
					try
					{
						String input = reader.Read(br, cs);
						if (input.equals(""))
						{
							
						}
						else
						{
							if (input.startsWith("command: "))
							{
								input.replace("command: ", "");
								if (input.contains("/"))
								{
									input.replace("/", "");
									ExecuteCommand(input);
								}
								else
								{
									ExecuteCommand(input);
								}
							}
							else if (input.startsWith("chat: "))
							{
								input.replace("chat: ", "");
								Chat(input);
							}
						}
					}
					catch (IOException e)
					{
						//Should never be fired because the while loop in WaitForAccept() should tell me if the socket died. Idk though.
						e.printStackTrace();
					}
				}
			}
			
		});
	}
	
	}
