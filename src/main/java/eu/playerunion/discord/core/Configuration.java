package eu.playerunion.discord.core;

public class Configuration {
	
	private String token;
	private long serverId;
	private long supportChannelId;
	private long botChannelId;
	private String minecraftIp;
	private String rustIp;
	
	public String getToken() {
		return this.token;
	}
	
	public long getServerId() {
		return this.serverId;
	}
	
	public long getSupportChannelId() {
		return this.supportChannelId;
	}
	
	public long getBotChannelId() {
		return this.botChannelId;
	}
	
	public String getMinecraftIp() {
		return this.minecraftIp;
	}
	
	public String getRustIp() {
		return this.rustIp;
	}

}
