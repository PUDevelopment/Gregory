package eu.playerunion.discord.core.listeners;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.playerunion.discord.core.Gregory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class CommandListener extends ListenerAdapter {
	
	private Gregory gregory = Gregory.getInstance();
	
	@Override
	public void onSlashCommand(SlashCommandEvent e) {
		if(e.getChannel().getIdLong() != this.gregory.getConfig().getBotChannelId())
			return;
		
		if(e.getName().equals("ip")) {
			MessageEmbed msg = new EmbedBuilder()
					.setTitle("**Szervereink elérhetősége**")
					.setColor(new Color(52, 195, 235))
					.setAuthor("gregory@playerunion", "https://playerunion.eu", "https://playerunion.eu/main_lib/img/gregory_logo.png")
					.addField("Minecraft (Vanilla)", this.gregory.getConfig().getMinecraftIp(), false)
					.addField("Rust", this.gregory.getConfig().getRustIp(), false)
					.build();
			
			e.replyEmbeds(msg).setEphemeral(true).queue();
		}
		
		if(e.getName().equals("kapcsolat")) {
			MessageEmbed msg = new EmbedBuilder()
					.setTitle("**Csapatunk elérhetősége**")
					.setColor(new Color(52, 195, 235))
					.setAuthor("gregory@playerunion", "https://playerunion.eu", "https://playerunion.eu/main_lib/img/gregory_logo.png")
					.addField("E-mail címünk", "admin@playerunion.eu", false)
					.addField("Facebook oldalunk", "https://www.facebook.com/playerunionHU", false)
					.addField("Youtube csatornánk", "https://bit.ly/3wvgJQM", false)
					.build();
			
			e.replyEmbeds(msg).setEphemeral(true).queue();
		}
		
		if(e.getName().equals("hibajegy")) {
			this.generateTicket(e.getGuild(), e.getMember());
			
			e.reply("A hibajegyed hamarosan elkészül...").setEphemeral(true).queue();
		}
		
		if(e.getName().equals("tgf"))
			e.reply("Tagfelvétellel kapcsolatos információnkat megtalálod a https://tagfelvetel.playerunion.eu weboldalon!").setEphemeral(true).queue();
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent e) {
		if(e.getComponentId().equals("hibajegy")) {
			this.generateTicket(e.getGuild(), e.getMember());
			
			e.reply("A hibajegyed hamarosan elkészül...").setEphemeral(true).queue();
		}
		
		if(e.getComponentId().equals("deleteTicket")) {
			if(e.getChannel().getName().equals("segitseg-" + e.getUser().getName()) || e.getMember().hasPermission(EnumSet.of(Permission.ADMINISTRATOR))) {
				e.reply("A hibajegy 5 másodperc múlva bezárja önmagát! Köszönjük, hogy felvetted velünk a kapcsolatot!").setEphemeral(false).queue();
				
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				
				executorService.schedule(() -> {
					TextChannel channel = e.getTextChannel();
					
					channel.delete().queue();
				}, 5, TimeUnit.SECONDS);
			}
		}
	}
	
	/**
	 * Hibajegy generálása.
	 * 
	 * @param guild A Guild.
	 * @param user A User, aki kérte a hibajegyet.
	 */
	
	private void generateTicket(Guild guild, Member user) {
		guild.createTextChannel("segitseg-" + user.getUser().getName())
		.addPermissionOverride(user, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY), null)
		.addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
		.queue();
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		
		executorService.schedule(() -> {
			TextChannel channel = guild.getTextChannelsByName("hibajegy-" + user.getUser().getName(), false).get(0);
			Role staffRole = guild.getRoleById(906365109444673556L);
			
			channel.sendMessage("Kedves, " + user.getAsMention() + "!\n"
					+ "Egy csapattagunk hamarosan jelentkezik és megpróbál segíteni neked!"
					+ "\nKérlek, írd körbe a pontos problémát, hogy a csapattagok is érthessék azt!"
					+ "\n**Köszönjük!**\n\n"
				    + staffRole.getAsMention())
			.setActionRow(Button.danger("deleteTicket", "✖️ Hibajegy törlése")).queue();
		}, 2, TimeUnit.SECONDS);
	}

}
