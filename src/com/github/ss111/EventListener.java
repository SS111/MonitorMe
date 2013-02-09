package com.github.ss111;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerCommandEvent;

@SuppressWarnings("deprecation")
public class EventListener implements Listener
{
	public DatagramSocket datagramServer = MonitorMeCore.GetServer();
	public MonitorMeCore plugin;
	
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerLogin(PlayerLoginEvent event)
		{
			Map<String, Integer> clients = MonitorMeCore.getClientMap();
			
			if (!clients.isEmpty())
			{
				byte[] sendData = new byte[1024];
				
				String loginString = "login: " + event.getPlayer().getName() + " logged in.";
				
				sendData = loginString.getBytes();
				
				for (Map.Entry<String, Integer> entry: clients.entrySet())
				{
					DatagramPacket sendLogin = null;
					
					if (entry.getKey().startsWith("admin: "))
					{	
						try 
						{
							sendLogin = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
						}
						catch (UnknownHostException e1)
						{
							clients.remove("admin: " + entry.getKey());
							
							continue;
						}
						try
						{
							datagramServer.send(sendLogin);
						} 
						catch (IOException e)
						{
							continue;
						}
					}
					else
					{
						Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerLogin"));
						if (Allowed.equals(true))
						{	
							try 
							{
								sendLogin = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("guest: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendLogin);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
					}
					
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerQuit (PlayerQuitEvent event)
		{
            Map<String, Integer> clients = MonitorMeCore.getClientMap();
			
			if (!clients.isEmpty())
			{
				byte[] sendData = new byte[1024];
				
				String logoutString = "logout: " + event.getPlayer().getName() + " logged out.";
				
				sendData = logoutString.getBytes();
				
				for (Map.Entry<String, Integer> entry: clients.entrySet())
				{
					DatagramPacket sendLogout = null;
					
					if (entry.getKey().startsWith("admin: "))
					{	
						try 
						{
							sendLogout = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
						}
						catch (UnknownHostException e1)
						{
							clients.remove("admin: " + entry.getKey());
							
							continue;
						}
						try
						{
							datagramServer.send(sendLogout);
						} 
						catch (IOException e)
						{
							continue;
						}
					}
					else
					{
						Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerLogout"));
						if (Allowed.equals(true))
						{	
							try 
							{
								sendLogout = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("guest: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendLogout);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
					}
					
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerChat (PlayerChatEvent event)
		{
			if (!event.isCancelled())
			{
				 Map<String, Integer> clients = MonitorMeCore.getClientMap();
					
					if (!clients.isEmpty())
					{
						byte[] sendData = new byte[1024];
						
						String chatString = "chat: " + event.getPlayer().getName() + ": " + event.getMessage();
						
						sendData = chatString.getBytes();
						
						for (Map.Entry<String, Integer> entry: clients.entrySet())
						{
							DatagramPacket sendChat = null;
							
							if (entry.getKey().startsWith("admin: "))
							{	
								try 
								{
									sendChat = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
								}
								catch (UnknownHostException e1)
								{
									clients.remove("admin: " + entry.getKey());
									
									continue;
								}
								try
								{
									datagramServer.send(sendChat);
								} 
								catch (IOException e)
								{
									continue;
								}
							}
							else
							{
								Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerChat"));
								if (Allowed.equals(true))
								{	
									try 
									{
										sendChat = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
									}
									catch (UnknownHostException e1)
									{
										clients.remove("guest: " + entry.getKey());
										
										continue;
									}
									try
									{
										datagramServer.send(sendChat);
									} 
									catch (IOException e)
									{
										continue;
									}
								}
							}
							
						}
					}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onGamemodeChange (PlayerGameModeChangeEvent event) throws IOException
		{
			if (!event.isCancelled())
			{
				Map<String, Integer> clients = MonitorMeCore.getClientMap();
				
				if (!clients.isEmpty())
				{
					byte[] sendData = new byte[1024];
					
					String gamemodeChangeString = "gamemode: " + event.getPlayer().getName() + "'s gamemode was changed to " + event.getNewGameMode().getValue();
					
					sendData = gamemodeChangeString.getBytes();
					
					for (Map.Entry<String, Integer> entry: clients.entrySet())
					{
						DatagramPacket sendGamemodeChange = null;
						
						if (entry.getKey().startsWith("admin: "))
						{	
							try 
							{
								sendGamemodeChange = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("admin: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendGamemodeChange);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
						else
						{
							Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerGamemodeChange"));
							if (Allowed.equals(true))
							{	
								try 
								{
									sendGamemodeChange = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
								}
								catch (UnknownHostException e1)
								{
									clients.remove("guest: " + entry.getKey());
									
									continue;
								}
								try
								{
									datagramServer.send(sendGamemodeChange);
								} 
								catch (IOException e)
								{
									continue;
								}
							}
						}
						
					}
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerKick(PlayerKickEvent event) throws IOException
		{
			if (!event.isCancelled())
			{
                Map<String, Integer> clients = MonitorMeCore.getClientMap();
				
				if (!clients.isEmpty())
				{
					byte[] sendData = new byte[1024];
					
					String kickString = "kick: " + event.getPlayer().getName() + " was kicked for reason: " + event.getReason();
					
					sendData = kickString.getBytes();
					
					for (Map.Entry<String, Integer> entry: clients.entrySet())
					{
						DatagramPacket sendKick = null;
						
						if (entry.getKey().startsWith("admin: "))
						{	
							try 
							{
								sendKick = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("admin: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendKick);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
						else
						{
							Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerKicked"));
							if (Allowed.equals(true))
							{	
								try 
								{
									sendKick = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
								}
								catch (UnknownHostException e1)
								{
									clients.remove("guest: " + entry.getKey());
									
									continue;
								}
								try
								{
									datagramServer.send(sendKick);
								} 
								catch (IOException e)
								{
									continue;
								}
							}
						}
						
					}
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerTeleport(PlayerTeleportEvent event) throws IOException
		{
			if (event.isCancelled())
			{
                Map<String, Integer> clients = MonitorMeCore.getClientMap();
				
				if (!clients.isEmpty())
				{
					byte[] sendData = new byte[1024];
					
					String teleportString = "teleport: " + event.getPlayer().getName() + " teleported from X: " + event.getFrom().getX() + " Y: " + event.getFrom().getY() + " Z: " + event.getFrom().getZ() + " to X: " + event.getTo().getX() + " Y: " + event.getTo().getY() + " Z: " + event.getTo().getZ();
					
					sendData = teleportString.getBytes();
					
					for (Map.Entry<String, Integer> entry: clients.entrySet())
					{
						DatagramPacket sendTeleport = null;
						
						if (entry.getKey().startsWith("admin: "))
						{	
							try 
							{
								sendTeleport = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("admin: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendTeleport);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
						else
						{
							Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerKicked"));
							if (Allowed.equals(true))
							{	
								try 
								{
									sendTeleport = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
								}
								catch (UnknownHostException e1)
								{
									clients.remove("guest: " + entry.getKey());
									
									continue;
								}
								try
								{
									datagramServer.send(sendTeleport);
								} 
								catch (IOException e)
								{
									continue;
								}
							}
						}
						
					}
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onServerCommand(ServerCommandEvent event) throws IOException
		{
			Map<String, Integer> clients = MonitorMeCore.getClientMap();
			
			if (!clients.isEmpty())
			{
				byte[] sendData = new byte[1024];
				
				String commandString = "command: " + event.getSender().getName() + " sent command: " + event.getCommand();
				
				sendData = commandString.getBytes();
				
				for (Map.Entry<String, Integer> entry: clients.entrySet())
				{
					DatagramPacket sendCommand = null;
					
					if (entry.getKey().startsWith("admin: "))
					{	
						try 
						{
							sendCommand = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("admin: ", "")), entry.getValue());
						}
						catch (UnknownHostException e1)
						{
							clients.remove("admin: " + entry.getKey());
							
							continue;
						}
						try
						{
							datagramServer.send(sendCommand);
						} 
						catch (IOException e)
						{
							continue;
						}
					}
					else
					{
						Boolean Allowed = new Boolean(plugin.getConfig().getBoolean("GuestPermissions.AllowedToSeePlayerKicked"));
						if (Allowed.equals(true))
						{	
							try 
							{
								sendCommand = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(entry.getKey().toString().replace("guest: ", "")), entry.getValue());
							}
							catch (UnknownHostException e1)
							{
								clients.remove("guest: " + entry.getKey());
								
								continue;
							}
							try
							{
								datagramServer.send(sendCommand);
							} 
							catch (IOException e)
							{
								continue;
							}
						}
					}
					
				}
			}
		}
	}