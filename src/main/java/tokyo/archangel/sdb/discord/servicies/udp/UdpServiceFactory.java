package tokyo.archangel.sdb.discord.servicies.udp;

import java.util.Map;

/**
 * UDP経由で送られてきたデータを処理できる専用のサービスを生成するクラス
 * @author archangel
 */
public class UdpServiceFactory {
	private Map<String, UdpServiceInterface> udpServiceInterface;

	public UdpServiceFactory(Map<String, UdpServiceInterface> udpServiceInterface) {
		this.udpServiceInterface = udpServiceInterface;
	}

	/**
	 * オペコードに応じたサービスクラスを生成します
	 * @param baseDto
	 * @param session
	 * @return
	 */
	public UdpServiceInterface create(String className) {
		return udpServiceInterface.get(className);
	}
}
