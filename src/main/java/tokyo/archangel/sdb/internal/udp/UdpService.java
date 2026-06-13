package tokyo.archangel.sdb.internal.udp;

import java.beans.Introspector;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.internal.servicies.udp.UdpServiceFactory;
import tokyo.archangel.sdb.internal.servicies.udp.UdpServiceInterface;
import tokyo.archangel.sdb.internal.servicies.udp.selectprotcol.SelectProtcol;

/**
 * UDPから受信したデータをパースし、各サービスクラスに振り分ける
 * @author archangel
 */
@Slf4j
public class UdpService {

	private UdpServiceFactory factory;

	public UdpService(UdpServiceFactory factory) {
		this.factory = factory;
	}

	public void recieve(byte[] data) {
		Thread currentThread = Thread.currentThread();
		currentThread.setName("UDP-service");
		//log.trace("UDP Received " + data.length);
		//log.trace(HexFormat.of().formatHex(data));

		if (isIpDiscovery(data)) {
			// IPディスカバリーだった場合		
			String serviceName = SelectProtcol.class.getSimpleName();
			serviceName = Introspector.decapitalize(serviceName);
			UdpServiceInterface udpService = factory.create(serviceName);
			udpService.exec(data);
			return;
		}

		// それ以外

	}

	private boolean isIpDiscovery(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data, 0, data.length);
		buffer.order(ByteOrder.BIG_ENDIAN);

		// タイプの確認
		short type = buffer.getShort();
		if (type != 2) {
			return false;
		}

		// 長さの確認
		short length = buffer.getShort();
		if (length != buffer.array().length - 4) {
			return false;
		}

		// IPアドレスが含まれているか
		buffer.position(8);
		byte[] byteAddress = new byte[64];
		buffer.get(byteAddress);
		String address = new String(byteAddress, StandardCharsets.UTF_8);
		int nullPos = address.indexOf('\0');
		if (nullPos != -1) {
			address = address.substring(0, nullPos);
		}
		try {
			InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}
}
