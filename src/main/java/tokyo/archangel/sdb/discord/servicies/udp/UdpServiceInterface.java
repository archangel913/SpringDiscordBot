package tokyo.archangel.sdb.discord.servicies.udp;

/**
 * UDP経由で送信されてきたデータを処理する
 */
public interface UdpServiceInterface {
	/**
	 * サービスの処理を実行
	 * @param dto
	 */
	public void exec(byte[] data);
}
