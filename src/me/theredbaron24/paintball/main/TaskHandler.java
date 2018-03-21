package me.theredbaron24.paintball.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class TaskHandler {

	private static volatile List<Runnable> tasks = new ArrayList<Runnable>();
	private static volatile Thread thread = null;
	private static volatile boolean sleep = true;
	
	public static void init(){
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(tasks.isEmpty() == false){
						sleep = false;
						List<Runnable> tasksToRun = new ArrayList<Runnable>();
						tasksToRun.addAll(tasks);
						tasks.clear();
						for(Runnable runnable : tasksToRun){
							runnable.run();
						}
						tasksToRun.clear();
						tasksToRun = null;
					}else{
						sleep = true;
					}
					if(sleep){
						try{
							Thread.sleep(100l);
						}catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		});
		thread.start();
	}
	public static void close(){
		if(thread != null){
			thread.interrupt();
		}
	}
	public static synchronized void runTask(Runnable runnable){
		if(thread.isAlive()){
			tasks.add(runnable);
		}else{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Paintball Async Thread was dead! Restarting...");
			init();
			tasks.add(runnable);
		}
	}
}