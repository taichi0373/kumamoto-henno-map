package io.github.taichi0373.kumamoto_henno_map.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

/**
 * メール送信サービス
 * <p>
 * Resend HTTP API を使用してプレーンテキストメールを送信する。
 * </p>
 */
@Service
public class MailService {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    /** Resend APIキー */
    @Value("${resend.api-key}")
    private String resendApiKey;

    /** Resend sandboxモード（trueの場合はAPIを呼ばず、メールは送信されない） */
    @Value("${resend.sandbox:false}")
    private boolean sandboxMode;

    /** フロントエンドのベースURL（メール本文のリンク生成に使用） */
    @Value("${app.password-reset.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    /** メール送信元アドレス */
    @Value("${app.mail.from}")
    private String mailFrom;

    /**
     * フロントエンドのベースURLを取得する
     * @return フロントエンドのベースURL
     */
    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    /**
     * プレーンテキストメールを送信する
     * <p>
     * sandboxモードの場合は API を呼ばず、開発時の動作確認用に本文をログ出力する。
     * </p>
     * @param to 送信先メールアドレス
     * @param subject 件名
     * @param body 本文
     * @throws RuntimeException Resend API の呼び出しに失敗した場合
     */
    public void send(String to, String subject, String body) {
        // Resend には sandbox 機能がないため、API を呼ばずに送信をスキップする
        if (sandboxMode) {
            logger.info("sandboxモードのためメールの送信をスキップしました: subject={}\n{}", subject, body);
            return;
        }

        CreateEmailOptions params = CreateEmailOptions.builder()
            .from(mailFrom)
            .to(to)
            .subject(subject)
            .text(body)
            .build();

        try {
            new Resend(resendApiKey).emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Resendメール送信エラー: " + e.getMessage(), e);
        }
    }
}
