package io.github.taichi0373.kumamoto_henno_map.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTPリクエストユーティリティクラス
 */
public class RequestUtils {

    /** インスタンス化禁止 */
    private RequestUtils() {}

    /**
     * クライアントIPアドレスを取得する
     * <p>
     * X-Forwarded-For ヘッダーはクライアントが任意に偽装できるため使用しない。
     * リバースプロキシ配下にデプロイする場合は application.properties の
     * server.forward-headers-strategy を FRAMEWORK または NATIVE に変更し、
     * プロキシ側で信頼できる送信元のみ X-Forwarded-For を付与するよう設定すること。
     * その場合、Spring / Tomcat が安全に処理した結果が getRemoteAddr() に反映される。
     * Cloudflare 経由の k3s 構成では、左端の偽装値を採用しない NATIVE を使用する。
     * </p>
     * @param request HTTPリクエスト
     * @return クライアントIPアドレス
     */
    public static String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
