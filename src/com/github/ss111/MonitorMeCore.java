package com.github.ss111;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class MonitorMeCore extends JavaPlugin
{
	public static DatagramSocket datagramServer;
	public byte[] sendBytes = new byte[1024];
	public byte[] recieveBytes = new byte[1024];
	public String recievedDataText;
	public static Map<String, Integer> clients = new HashMap<String, Integer>();
	public String adminPasswordHash;
	public String guestPasswordHash;
	public Boolean listening = false;
	
	public void log(String message, String type)
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
			getLogger().warning("Invalid use of core function: log");
		}
	}
	
	public void executeCommand(String command, String ipAddress)
	{	
		for (Map.Entry<String, Integer> entry: clients.entrySet())
		{
			String entryFromMap = new String(entry.getKey());
			if (entryFromMap.startsWith("admin: "))
			{
				if (entryFromMap.replace("admin: ", "").equals(ipAddress))
				{
					getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("command: ", "").replace("/", ""));
					//Send data that command was sent
					break;
				}
			}
			else
			{
				
				if (entryFromMap.replace("guest: ", "").equals(ipAddress))
				{
					Boolean allowed = this.getConfig().getBoolean("GuestPermissions.Actions.AllowedToSendCommand");
					if (allowed.equals(true))
					{
						getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
						//Send data that command was sent
						break;
					}
					else
					{
						//Send data that dont have perm to send command
						break;
					}
				}
			}
			
		}
	}
	
	public void chat(String message, String ipAddress)
	{
		for (Map.Entry<String, Integer> entry: clients.entrySet())
		{
			String entryFromMap = new String(entry.getKey());
			if (entryFromMap.startsWith("admin: "))
			{
				if (entryFromMap.replace("admin: ", "").equals(ipAddress))
				{
					getServer().broadcastMessage(message.replace("chat: ", ""));
					//Send data that chat was sent
					break;
				}
			}
			else
			{
				
				if (entryFromMap.replace("guest: ", "").equals(ipAddress))
				{
					Boolean allowed = this.getConfig().getBoolean("GuestPermissions.Actions.AllowedToChat");
					if (allowed.equals(true))
					{
						getServer().broadcastMessage(message.replace("chat: ", ""));
						//Send chat that command was sent
						break;
					}
					else
					{
						//Send data that dont have perm to send command
						break;
					}
				}
			}
			
		}
	}
	
	public void enableOrDisableListener(String type)
	{
		if (type.equals("enable"))
		{
			getServer().getPluginManager().registerEvents(new EventListener(), this);
		}
		else if (type.equals("disable"))
		{
			HandlerList.unregisterAll(new EventListener());
		}
		else
		{
			getLogger().warning("Invalid use of core function: enableOrDisableListener");
		}
	}
	
	public static Map<String, Integer> getClientMap()
	{
		return clients;
	}
	
	public static DatagramSocket GetServer()
	{
		return datagramServer;
	}
		
	@Override
	public void onEnable()
	{
		final FileConfiguration config = this.getConfig();
		config.addDefault("AdminPassword", "c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec");
		config.addDefault("GuestPassword", "b0e0ec7fa0a89577c9341c16cff870789221b310a02cc465f464789407f83f377a87a97d635cac2666147a8fb5fd27d56dea3d4ceba1fc7d02f422dda6794e3c");
		config.addDefault("Port", 3002);
		config.addDefault("GuestPermissions.Actions.AllowedToChat", true);
		config.addDefault("GuestPermissions.Actions.AllowedToSendCommand", false);
		config.addDefault("GuestPermissions.AllowedToSeePlayerLogin", true);
		config.addDefault("GuestPermissions.AllowedToSeePlayerLogout", true);
		config.addDefault("GuestPermissions.AllowedToSeePlayerChat", true);
		config.addDefault("GuestPermissions.AllowedToSeePlayerGamemodeChange", false);
		config.addDefault("GuestPermissions.AllowedToSeePlayerKicked", false);
		config.addDefault("GuestPermissions.AllowedToSeePlayerTeleported", false);
		config.addDefault("GuestPermissions.AllowedToSeeServerCommand", false);
		config.options().header("NOTE: To make your password, go to http://www.fileformat.info/tool/hash.htm and get the SHA-512 hash! The default password for admins is \"admin\" and the defualt password for guests is \"guest\".");
		config.options().copyDefaults(true);
		saveConfig();
		
		getLogger().info("MonitorMe loaded! Starting mini-server...");
		
		try 
		{
			datagramServer = new DatagramSocket(config.getInt("Port"));
			getLogger().info("Mini-server is enabled and listening on port " + config.getInt("Port") + "!");
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			getLogger().severe("Mini-server cannot be started on port " + config.getInt("Port") + "! Is the port forwarding set up correctly?");
		}
		
		adminPasswordHash = config.getString("AdminPassword");
		guestPasswordHash = config.getString("GuestPassword");
		
		waitForLoginPacket();
	}
	
	@Override
	public void onDisable()
	{
		datagramServer.close();
	}
	
	public void waitForLoginPacket()
	{	
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					DatagramPacket RecievePacket = new DatagramPacket(recieveBytes, recieveBytes.length);
					datagramServer.receive(RecievePacket);
					
					recievedDataText = new String(RecievePacket.getData(), 0, RecievePacket.getLength());
					
					if (recievedDataText.startsWith("login: "))
					{
						
					}
					else
					{
						run();
					}
					
					String remotePasswordHash = DigestUtils.sha512Hex(recievedDataText.replace("login: ", ""));
					
					if (remotePasswordHash.equals(adminPasswordHash))
					{
						log("Client " + RecievePacket.getAddress().toString() + " connected as admin.", "info");
						clients.put("admin: " + RecievePacket.getAddress().toString(), RecievePacket.getPort());
						
						remotePasswordHash = null;
						
						if (listening.equals(false))
						{
							enableOrDisableListener("enable");
							listenForData();
						}
						
						run();
						
					}
					else if (remotePasswordHash.equals(guestPasswordHash))
					{
						log("Client " + RecievePacket.getAddress().toString() + " connected as guest.", "info");
						clients.put("guest: " + RecievePacket.getAddress().toString(), RecievePacket.getPort());
						
						remotePasswordHash = null;
						
						if (listening.equals(false))
						{
							enableOrDisableListener("enable");
							listenForData();
						}
						
						run();
					}
					else
					{
						log("Client " + RecievePacket.getAddress().toString() + " connected with bad password hash: " + remotePasswordHash, "warning");
						
						remotePasswordHash = null;
						
						run();
					}
					
				}
				catch (IOException e)
				{
					//What would even throw this?
							
					run();
				}
			}
			
		});
	}
	
	public void listenForData()
	{
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{

			@Override
			public void run()
			{
				while (!clients.isEmpty())
				{
					try
					{
						DatagramPacket recievePacketInput = new DatagramPacket(recieveBytes, recieveBytes.length);
						datagramServer.receive(recievePacketInput);
						
						String input = new String(recievePacketInput.getData(), 0, recievePacketInput.getLength());
						if (input.equals(""))
						{
							
						}
						else
						{
							if (input.startsWith("command: "))
							{
								executeCommand(input, recievePacketInput.getAddress().toString());
							}
							else if (input.startsWith("chat: "))
							{
								chat(input, recievePacketInput.getAddress().toString());
							}
							else if (input.startsWith("logout: "))
							{
								for (Map.Entry<String, Integer> entry: clients.entrySet())
								{
									if (entry.getKey().startsWith("admin: "))
									{
										if (entry.getKey().replace("admin: ", "").equals(recievePacketInput.getAddress().toString()))
										{
											clients.remove("admin: " + recievePacketInput.getAddress().toString());
											log("Client " + recievePacketInput.getAddress().toString() + " has logged out.", "info");
											break;
										}
									}
									else
									{
										if (entry.getKey().replace("guest: ", "").equals(recievePacketInput.getAddress().toString()))
										{
											clients.remove("guest: " + recievePacketInput.getAddress().toString());
											log("Client " + recievePacketInput.getAddress().toString() + " has logged out.", "info");
											break;
										}
									}
								}
								
								if (clients.isEmpty())
								{
									enableOrDisableListener("disable");
									listening = false;
									break;
								}
							}
						}
					}
					catch (IOException e)
					{
						//How would this even be fired? Packet not being sent?
						e.printStackTrace();
					}
				}
			}
			
		});
	}
	
	}
