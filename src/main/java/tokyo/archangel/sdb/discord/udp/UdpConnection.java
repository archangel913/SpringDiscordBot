package tokyo.archangel.sdb.discord.udp;

import java.io.IOException;

/**
 * UDPコネクションのインターフェース
 * @author archangel
 */
public interface UdpConnection {
	
	/**
	 * UDPからバイナリデータを送信する
	 * @param data 
	 * @throws IOException
	 */
	public void send(byte[] data) throws IOException;
}
