package com.github.ss111;
import java.net.*;
import java.io.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MonitorMeCore extends JavaPlugin
{
	public ServerSocket ss;
	public Socket cs;
	public PrintWriter out;
	public BufferedReader in;
	
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
		} 
		catch (IOException e)
		{
			getLogger().severe("Mini-server cannot listen on port " + config.getInt("Port") + "! Is the port correctly forwarded?");
			e.printStackTrace();
		}
		
		getLogger().info("Mini-server is enabled and listening on port " + config.getInt("Port") + "!");
		
		try
		{
			cs = ss.accept();
		}
		catch (IOException e)
		{
			
		}
	}
	
	@Override
	public void onDisable()
	{
		
	}
}
