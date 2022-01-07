package eu.playerunion.discord.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.playerunion.discord.core.listeners.CommandListener;
import eu.playerunion.discord.core.listeners.MessageListener;
import eu.playerunion.discord.core.listeners.StartupEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Gregory {
	
	private static Gregory instance;
	
	private JDA api;
	private Configuration config;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static Gregory getInstance() {
		return instance;
	}
	
	public void init() {
		instance = this;
		
		System.out.println("[ DEBUG ] Gregory indítása...");
		
		this.checkConfig();
		
		try {
			ArrayList<GatewayIntent> intents = new ArrayList<GatewayIntent>();
			
			intents.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
			intents.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
			intents.add(GatewayIntent.DIRECT_MESSAGES);
			intents.add(GatewayIntent.GUILD_BANS);
			intents.add(GatewayIntent.GUILD_EMOJIS);
			intents.add(GatewayIntent.GUILD_INVITES);
			intents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
			intents.add(GatewayIntent.GUILD_MEMBERS);
			intents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
			intents.add(GatewayIntent.GUILD_MESSAGES);
			intents.add(GatewayIntent.GUILD_PRESENCES);
			intents.add(GatewayIntent.GUILD_VOICE_STATES);
			intents.add(GatewayIntent.GUILD_WEBHOOKS);
			
			this.api = JDABuilder.createDefault(this.config.getToken(), intents)
					.addEventListeners(new StartupEvent())
					.build();
			
			this.api.addEventListener(new CommandListener());
			this.api.addEventListener(new MessageListener());
			
			this.api.getPresence().setActivity(Activity.playing("PlayerUnion"));
			this.api.getPresence().setStatus(OnlineStatus.IDLE);
			
			this.api.upsertCommand("ip", "Szervereink IP-címének kilistázása.").queue();
			this.api.upsertCommand("kapcsolat", "Elérhetőségeink listája.").queue();
			this.api.upsertCommand("hibajegy", "Segítségkérés a szever csapatától.").queue();
			this.api.upsertCommand("tgf", "Tagfelvétel információk.").queue();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	private void checkConfig() {
		System.out.println("[ DEBUG ] Konfiguráció betöltése...");
		
		File configFile = new File("config.json");
		JSONObject defaultConfig = new JSONObject();
		
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
				
				defaultConfig.put("token", "ide valami");
				defaultConfig.put("serverId", 0000000000000L);
				defaultConfig.put("supportChannelId", 0000000000000L);
				defaultConfig.put("botChannelId", 0000000000000L);
				defaultConfig.put("minecraftIp", "s1.playerunion.eu");
				defaultConfig.put("rustIp", "rust.playerunion.eu");
				
				FileWriter fw = new FileWriter(configFile);
				
				fw.write(defaultConfig.toString(4));
				
				fw.close();
				
				System.out.println("[ DEBUG ] Alapkonfiguráció elmentve!");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			StringBuilder configLines = new StringBuilder();
			
			Files.lines(configFile.toPath()).forEach(line -> configLines.append(line));
			
			this.config = this.gson.fromJson(configLines.toString(), Configuration.class);
			
			System.out.println("[ DEBUG ] Konfiguráció sikeresen betöltve!");
			
			if(StringUtils.isEmpty(this.config.getToken()) || this.config.getToken().equalsIgnoreCase("ide valami")) {
				System.out.println("[ HIBA ] Üres token! A bot leállítja önmagát...");
				
				System.gc();
				System.exit(0);
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendSupportMessage() {
		// Vészhelyzeti esetre, ha eltűnne az üzenet.
		
		/** TextChannel channel = this.api.getTextChannelById(this.config.getSupportChannelId());
		
		 channel.sendMessage(":question:| **Segítségre van szükséged?**\n"
				+ "\n"
				+ "Elakadtál szervereinkre való csatlakozáskor? Esetleg történt egy bug valamelyik szerveren? Vagy egy megválaszolatlan kérdés merült fel benned szervereinkkel kapcsolatban?\n"
				+ "\n"
				+ ":exclamation:**Ne habozz, kérdezz Tőlünk bátran!**\n"
				+ "Kattints a gombra, vagy használd a /hibajegy parancsot, hogy felvehesd velünk a kapcsolatot!\n"
				+ "\n"
				+ ":envelope: **E-mail címünk:**\n"
				+ "admin@playerunion.eu\n"
				+ "\n"
				+ "*Amennyiben a probléma sürgős, vagy nehezen tudnád szavakba leírni, kérlek, csatlakozz be a fent található Várakozó szobába és várd, míg egy csapattagunk behúz Téged! Abban az esetben, ha felesleges/lényegtelen kérdésekkel bombázod csapattagjainkat, azért büntetés jár!*")
		.setActionRow(Button.primary("hibajegy", "Hibajegy nyitása")).queue(); **/
	}
	
    public void listenForCommands() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String command = reader.readLine();
			
			if(command.equalsIgnoreCase("shutdown")) {
				System.out.println("[ DEBUG ] A bot leáll...");
				
				this.api.shutdownNow();
				
				System.out.println("[ DEBUG ] A bot leállt!");
				
				System.gc();
				System.exit(0);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public JDA getApi() {
		return this.api;
	}
	
	public Configuration getConfig() {
		return this.config;
	}

}
