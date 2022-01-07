package eu.playerunion.discord.core.listeners;

import eu.playerunion.discord.core.Gregory;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class StartupEvent implements EventListener {

	private Gregory gregory = Gregory.getInstance();
	
	@Override
	public void onEvent(GenericEvent e) {
		if(e instanceof ReadyEvent) {
			System.out.println("[ DEBUG ] A bot sikeresen elindult!");
			
			this.gregory.sendSupportMessage();
			
			Thread listen = new Thread(() -> {
				this.gregory.listenForCommands();
			});
			
			listen.start();
		}
	}

}
