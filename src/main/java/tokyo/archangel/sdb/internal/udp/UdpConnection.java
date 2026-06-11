package tokyo.archangel.sdb.internal.udp;

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
	
	/**
	 * コネクションが閉じてるか
	 * @return 閉じていたらtrue
	 */
	public boolean isClosed();
}
