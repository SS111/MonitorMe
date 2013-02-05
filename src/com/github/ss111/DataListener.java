package com.github.ss111;
import java.io.IOException;
import java.io.PrintWriter;
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
public class DataListener implements Listener
	{
	
		PrintWriter pw;
		DataWriterOut writer = new DataWriterOut();
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerLogin(PlayerLoginEvent event) throws IOException
		{
			//Send data to Android saying that someone logged in
			writer.Write(pw, MonitorMeCore.GetClientSocket(), "login: " + event.getPlayer().getName() + " logged in.");
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerQuit (PlayerQuitEvent event) throws IOException
		{
			//Send data to Android saying that someone logged out
			writer.Write(pw, MonitorMeCore.GetClientSocket(),"logout: " + event.getPlayer().getName() + " logged out.");
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerChat (PlayerChatEvent event) throws IOException
		{
			if (event.isCancelled())
			{
				
			}
			else
			{
				//Send data to Android saying the person's chat message
				writer.Write(pw, MonitorMeCore.GetClientSocket(),"chat: " + event.getPlayer().getName() + ": " + event.getMessage());
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onGamemodeChange (PlayerGameModeChangeEvent event) throws IOException
		{
			if (event.isCancelled())
			{
				
			}
			else
			{
				//Send data to Android saying someone's gamemode changed
				writer.Write(pw, MonitorMeCore.GetClientSocket(),"gamemode: " + event.getPlayer().getName() + "'s gamemode was changed to " + event.getNewGameMode().getValue());
				//Does this also work?
				//out.println("gamemode: " + event.getPlayer().getName() + "'s gamemode was changed to " + event.getNewGameMode().getByValue(event.getNewGameMode().getValue()).toString());
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerKick(PlayerKickEvent event) throws IOException
		{
			if (event.isCancelled())
			{
				
			}
			else
			{
				//Send data to Android saying someone was kicked
				writer.Write(pw, MonitorMeCore.GetClientSocket(),"kick: " + event.getPlayer().getName() + " was kicked for reason: " + event.getReason());
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onPlayerTeleport(PlayerTeleportEvent event) throws IOException
		{
			if (event.isCancelled())
			{
				
			}
			else
			{
				//Send data to Andoird saying someone teleported
				writer.Write(pw, MonitorMeCore.GetClientSocket(),"teleport: " + event.getPlayer().getName() + " teleported from X: " + event.getFrom().getX() + " Y: " + event.getFrom().getY() + " Z: " + event.getFrom().getZ() + " to X: " + event.getTo().getX() + " Y: " + event.getTo().getY() + " Z: " + event.getTo().getZ());
			}
		}
		
		//Gah, this event cannot be passed when an invalid command is entered. Annoying.
		@EventHandler (priority = EventPriority.MONITOR)
		public void onServerCommand(ServerCommandEvent event) throws IOException
		{
			writer.Write(pw, MonitorMeCore.GetClientSocket(),"command: " + event.getSender().getName() + " sent command: " + event.getCommand());
		}
	}