package com.github.ss111;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class MonitorMeCore extends JavaPlugin
{
	public ServerSocket ss;
	public Socket cs;
	public PrintWriter out;
	public BufferedReader in;
	public String PasswordHash;
	public Boolean Allowed;
	
	public class DataListener implements Listener
	{
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerLogin(PlayerLoginEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				//Send data to Android saying that someone logged in
				out.println("login: " + event.getPlayer().getName() + " logged in.");
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerQuit (PlayerQuitEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				//Send data to Android saying that someone logged out
				out.println("logout: " + event.getPlayer().getName() + " logged out.");
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerChat (PlayerChatEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				if (event.isCancelled())
				{
					
				}
				else
				{
					//Send data to Android saying the person's chat message
					out.println("chat: " + event.getPlayer().getName() + ": " + event.getMessage());
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onGamemodeChange (PlayerGameModeChangeEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				if (event.isCancelled())
				{
					
				}
				else
				{
					//Send data to Android saying someone's gamemode changed
					out.println("gamemode: " + event.getPlayer().getName() + "'s gamemode was changed to " + event.getNewGameMode().getValue());
					//Does this also work?
					//out.println("gamemode: " + event.getPlayer().getName() + "'s gamemode was changed to " + event.getNewGameMode().getByValue(event.getNewGameMode().getValue()).toString());
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerKick(PlayerKickEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				if (event.isCancelled())
				{
					
				}
				else
				{
					//Send data to Android saying someone was kicked
					out.println("kick: " + event.getPlayer().getName() + " was kicked for reason: " + event.getReason());
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerTeleport(PlayerTeleportEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				if (event.isCancelled())
				{
					
				}
				else
				{
					//Send data to Andoird saying someone teleported
					out.println("teleport: " + event.getPlayer().getName() + " teleported from X: " + event.getFrom().getX() + " Y: " + event.getFrom().getY() + " Z: " + event.getFrom().getZ() + " to X: " + event.getTo().getX() + " Y: " + event.getTo().getY() + " Z: " + event.getTo().getZ());
				}
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onServerCommand(ServerCommandEvent event)
		{
			if (Allowed.equals(false))
			{
				
			}
			else
			{
				//Send data to Android saying someone sent a command
				out.println("command: " + event.getSender().getName() + " sent command: " + event.getCommand());
			}
		}
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
		
		getLogger().info("Registering data listener...");
		getServer().getPluginManager().registerEvents(new DataListener(), this);
		getLogger().info("Data listener registered!");
		
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
	
	//Is this void asnyc (ran from asnyc 'run' void) or not?
	public void ListenForData()
	{
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						String input = new String(in.readLine());
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
						}
					}
					catch (IOException e)
					{
						//The client has disconnected, right?
						getLogger().info("Client has disconnected.");
						cs = null;
						Allowed = false;
						e.printStackTrace();
					}
				}
			}
			
		});
	}
	
	public void ExecuteCommand(String Command)
	{
		getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
		out.println("info: " + "Command was completed successfuly.");
	}
	
	public void WaitForAccept()
	{
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
								ListenForData();
								
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
					
					cs = null;
					Allowed = false;
					
					run();
				}
			}
			
		});
	}
	
	}
