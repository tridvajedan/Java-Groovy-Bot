package bot;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;


public class Main {
	public static TrackScheduler tS = new TrackScheduler(null);

	public static void main(String[] args) {
		String token = "discord bot token";
		 DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
		 System.out.println(api.createBotInvite());

		 
		 api.addMessageCreateListener(event -> {
			if(event.getMessageContent().equalsIgnoreCase("*pause"))
			{
				Main.tS.pause();
			}
		 });
		 
		 api.addMessageCreateListener(event -> {
			if(event.getMessageContent().equalsIgnoreCase("*goaway"))
			{
				Server server = event.getServer().get();
				AudioConnection ac = server.getAudioConnection().get();
				ac.close();
			}
		 });
		 api.addMessageCreateListener(event -> {
			if(event.getMessageContent().equalsIgnoreCase("*s"))
			{
				Main.tS.nextTrack();
			}
		 });
		 api.addMessageCreateListener( event -> {
			if(event.getMessageContent().contains("*p"))
			{
				
				String messageUncut = event.getMessageContent();
				String[] messageCut = messageUncut.split(" ");
				String link = messageCut[1];
				ServerVoiceChannel channel = event.getMessageAuthor().getConnectedVoiceChannel().get();
				channel.connect().thenAccept(audioConnection -> {
					
					AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
					playerManager.registerSourceManager(new YoutubeAudioSourceManager());
					AudioPlayer player = playerManager.createPlayer();
					
					AudioSource source = new LavaPlayerAudioSource(api, player);
					audioConnection.setAudioSource(source);
					TrackScheduler tSL = new TrackScheduler(player);
					Main.tS = tSL;
					
					
					
					playerManager.loadItem(link, new AudioLoadResultHandler() {
	
	
	@Override
	public void trackLoaded(AudioTrack track) {

		Main.tS.queue(track);
				
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		for(AudioTrack track : playlist.getTracks()) {
            player.playTrack(track);
            
		}
		
	}

	@Override
	public void noMatches() {
		event.getChannel().sendMessage("Can't find anything on that link!");
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		event.getChannel().sendMessage("Ok idk what happened link died or something uhh try again i guess");
		
	}
						
					});
				});
				
				
			}
		 });

	
	

}
}

