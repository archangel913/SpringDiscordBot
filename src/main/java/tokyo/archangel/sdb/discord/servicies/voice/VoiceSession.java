package tokyo.archangel.sdb.discord.servicies.voice;

import java.util.concurrent.atomic.AtomicReference;

import tokyo.archangel.sdb.discord.servicies.libdave.E2eeCryptServiceImpl;
import tokyo.archangel.sdb.discord.servicies.transportcrypter.TransportCryptServiceImpl;
import tokyo.archangel.sdb.discord.udp.UdpConnectionImpl;

public class VoiceSession {
	private final AtomicReference<UdpConnectionImpl> udpConnection = new AtomicReference<>();

	private final AtomicReference<E2eeCryptServiceImpl> e2eeCryptService = new AtomicReference<>();

	private final AtomicReference<TransportCryptServiceImpl> transportCryptService = new AtomicReference<>();

	public UdpConnectionImpl compareAndSetUdpConnection(UdpConnectionImpl udpConnection) {
		if (this.udpConnection.compareAndSet(null, udpConnection)) {
			return udpConnection; // セットに成功
		}
		return getUdpConnection();
	}

	public UdpConnectionImpl getUdpConnection() {
		return this.udpConnection.get();
	}

	public E2eeCryptServiceImpl compareAndSetE2eeCryptService(E2eeCryptServiceImpl e2eeCryptService) {
		if (this.e2eeCryptService.compareAndSet(null, e2eeCryptService)) {
			return e2eeCryptService;
		}
		return getE2eeCryptService();
	}

	public E2eeCryptServiceImpl getE2eeCryptService() {
		return this.e2eeCryptService.get();
	}

	public TransportCryptServiceImpl compareAndSetTransportCryptService(TransportCryptServiceImpl transportCryptService) {
		if (this.transportCryptService.compareAndSet(null, transportCryptService)) {
			return transportCryptService;
		}
		return getTransportCryptService();
	}

	public TransportCryptServiceImpl getTransportCryptService() {
		return this.transportCryptService.get();
	}
}
