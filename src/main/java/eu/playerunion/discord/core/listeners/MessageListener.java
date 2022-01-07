package eu.playerunion.discord.core.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message msg = e.getMessage();
		
		if(e.getTextChannel().getIdLong() == 925172493004701787L) {
			if(msg.getAuthor().getName() == "Gregory") {
				msg.addReaction(":thumbs_up:").queue();
				msg.addReaction(":thumbs_down:").queue();
			}
		}
	}

}
