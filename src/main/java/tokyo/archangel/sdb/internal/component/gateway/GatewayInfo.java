package tokyo.archangel.sdb.internal.component.gateway;

import lombok.Getter;
import lombok.Setter;
import tokyo.archangel.sdb.internal.dto.gateway.opcode.code0.ready.ReadyDetail;
import tokyo.archangel.sdb.internal.enumeration.ReconnectMode;

@Getter
@Setter
public class GatewayInfo {

	/**
	 * 再接続失敗回数<br>
	 * ReconnectModeがhardで再接続が失敗した回数
	 */
	private int connectionFailCount = 0;

	/** 
	 * 再接続の方法
	 */
	private ReconnectMode reconnectMode = ReconnectMode.NONE;

	/**
	 * ディスコードから送信されたシーケンス
	 */
	private Long sequence;

	/**
	 * readyイベント時の内容
	 */
	private ReadyDetail readyDetail;

}
