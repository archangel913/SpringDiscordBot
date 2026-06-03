package tokyo.archangel.sdb.discord.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import tokyo.archangel.sdb.discord.enumeration.ServiceThreadStatus;

/**
 * UDPコネクションの実装クラス
 * @author archangel
 */
@Component
@Scope("prototype")
@Slf4j
public class UdpConnectionImpl implements UdpConnection {

	private DatagramSocket socket;

	private UdpService service;

	private InetAddress targetAddress;

	private int targetPort;

	private static final int BUFFER_SIZE = 1024;

	private static final int TIMEOUT = 1000;

	private volatile ServiceThreadStatus status = ServiceThreadStatus.INITIALIZING;

	public UdpConnectionImpl(UdpService service) {
		this.service = service;
	}

	/**
	 * 初期化する
	 * @param address
	 * @param port
	 * @throws SocketException
	 */
	public synchronized void init(String address, int port) throws SocketException {
		if (socket != null) {
			return;
		}

		try {
			targetAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			log.error("不正なIPアドレスです。ソケットを作成できません", e);
			throw new IllegalArgumentException(e);
		}
		targetPort = port;

		socket = new DatagramSocket(null);
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(0));
		socket.setSoTimeout(TIMEOUT);
		
		status = ServiceThreadStatus.INITIALIZED;
	}

	/**
	 * 受信スレッドを立ち上げる
	 */
	@Async
	public void receive() {
		if (isClosed()) {
			log.warn("UDPソケットの初期化ができていません。");
			return;
		}

		if (status == ServiceThreadStatus.ACTIVE) {
			log.warn("既に受信ループが起動しています。");
			return;
		}

		Thread currentThread = Thread.currentThread();
		currentThread.setName("UDP-receive");

		log.debug("UDPの受付を開始します");
		log.trace("受信ポート：" + socket.getLocalPort());
		status = ServiceThreadStatus.ACTIVE;

		// バッファの準備
		byte[] buf = new byte[BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

		while (!isClosed()) {
			receivePacket.setLength(buf.length);
			try {
				socket.receive(receivePacket);
				byte[] receivedData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				service.recieve(receivedData);
			} catch (SocketTimeoutException e) {
				// タイムアウト時は何もせず次のループへ
			} catch (SocketException e) {
				log.debug("ソケットが閉じられたため受信を終了します。");
				break;
			} catch (IOException e) {
				log.error("UDP listen中に例外が発生しました。", e);
			}
		}

		shutdown();
	}

	@Override
	public void send(byte[] data) throws IOException {
		if (isClosed()) {
			log.error("UDPソケットの初期化ができていません。");
			return;
		}

		DatagramPacket sendPacket = new DatagramPacket(data, data.length, targetAddress, targetPort);
		socket.send(sendPacket);
	}
	
	@Override
	public boolean isClosed() {
		return socket == null || socket.isClosed();
	}

	@PreDestroy
	public void close() {
		shutdown();
	}
	
	/**
	 * 終了処理
	 */
	private synchronized void shutdown() {
		// 既に完全に終了している場合は重ねて処理しない
		if (status == ServiceThreadStatus.TERMINATED) {
			return;
		}

		// 終了処理中でなければ、ステータスを TERMINATING に移行
		if (status != ServiceThreadStatus.TERMINATING) {
			status = ServiceThreadStatus.TERMINATING;
		}

		// ソケットが開いていれば安全に閉じる
		if (!isClosed()) {
			log.info("UDP接続をクローズします。");
			socket.close(); // これにより receive() スレッドがブロック中なら強制解除される
		}

		// すべてのクリーンアップが完了したため、最終ステータスに固定
		status = ServiceThreadStatus.TERMINATED;
		log.debug("UDPの受付を終了しました。");
	}
}
