package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.concurrent.CompletableFuture;

public interface VoiceSendService {
	public CompletableFuture<Void> send();

	public void pause();

	public void resume();

	public void close();
}
