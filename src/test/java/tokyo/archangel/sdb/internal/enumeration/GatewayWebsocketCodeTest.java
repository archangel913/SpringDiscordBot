package tokyo.archangel.sdb.internal.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class GatewayWebsocketCodeTest {

	@DisplayName("定義済みの数値は対応するenumを返す")
	@ParameterizedTest
	@EnumSource(value = GatewayWebsocketCode.class, names = "UNKNOWN_CODE", mode = EXCLUDE)
	void getGatewayWebsocketCode_returnsMatchingEnumForDefinedCode(GatewayWebsocketCode code) {
		assertThat(GatewayWebsocketCode.getGatewayWebsocketCode(code.getValue())).isEqualTo(code);
	}

	@DisplayName("未定義の数値はUNKNOWN_CODEを返す")
	@ParameterizedTest
	@ValueSource(ints = { 1006, 9999, 0 })
	void getGatewayWebsocketCode_returnsUnknownCodeForUndefinedValue(int code) {
		assertThat(GatewayWebsocketCode.getGatewayWebsocketCode(code)).isEqualTo(GatewayWebsocketCode.UNKNOWN_CODE);
	}

	@DisplayName("再接続不可能なコードはfalseを返す")
	@ParameterizedTest
	@EnumSource(value = GatewayWebsocketCode.class, names = { "AUTHENTICATION_FAILED", "INVALID_SHARD",
			"SHARDING_REQUIRED", "INVALID_INTENTS", "DISALLOWED_INTENTS" })
	void canReconnect_returnsFalseForNonReconnectableCodes(GatewayWebsocketCode code) {
		assertThat(code.canReconnect()).isFalse();
	}

	@DisplayName("それ以外のコードはtrueを返す")
	@ParameterizedTest
	@EnumSource(value = GatewayWebsocketCode.class, names = { "AUTHENTICATION_FAILED", "INVALID_SHARD",
			"SHARDING_REQUIRED", "INVALID_INTENTS", "DISALLOWED_INTENTS" }, mode = EXCLUDE)
	void canReconnect_returnsTrueForOtherCodes(GatewayWebsocketCode code) {
		assertThat(code.canReconnect()).isTrue();
	}
}
