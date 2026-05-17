package io.github.taichi0373.kumamoto_henno_map.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.taichi0373.kumamoto_henno_map.dto.FeedbackRequestDto;

/**
 * フィードバックサービス
 * <p>
 * ユーザーからのご意見・ご要望を受け取り、Slack Incoming Webhookを通じて通知する。
 * Webhook URL未設定時はSlack送信をスキップしてログ出力のみ行う。
 * </p>
 */
@Service
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    /** 送信日時フォーマット */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Slack Webhook URL */
    @Value("${slack.webhook.url:}")
    private String slackWebhookUrl;

    /** HTTP クライアント（再利用） */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * フィードバックをSlackに送信する
     *
     * @param request       フィードバックリクエスト
     * @param username      ログインユーザー名（未ログイン時はnull）
     */
    public void sendFeedback(FeedbackRequestDto request, String username) {
        String message = buildMessage(request, username);

        if (slackWebhookUrl == null || slackWebhookUrl.isBlank()) {
            logger.info("[Feedback] Webhook URL未設定のためログ出力のみ:\n{}", message);
            return;
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);
            restTemplate.postForEntity(slackWebhookUrl, payload, String.class);
            logger.info("[Feedback] Slackへの送信が完了しました");
        } catch (Exception e) {
            logger.error("[Feedback] Slack送信に失敗しました: {}", e.getMessage(), e);
        }
    }

    /**
     * Slackメッセージ本文を組み立てる
     *
     * @param request  フィードバックリクエスト
     * @param username ログインユーザー名（未ログイン時はnull）
     * @return Slackメッセージ文字列
     */
    private String buildMessage(FeedbackRequestDto request, String username) {
        String categoryLabel = resolveCategoryLabel(request.getCategory());
        String nameLabel = (request.getName() == null || request.getName().isBlank())
                ? "未記入" : request.getName();
        String emailLabel = (request.getEmail() == null || request.getEmail().isBlank())
                ? "未記入" : request.getEmail();
        String userLabel = (username != null) ? "ログイン済み（" + username + "）" : "未ログイン";
        String sendAt = LocalDateTime.now().format(DATE_FORMATTER);

        return "*【ご意見・ご要望】*\n"
                + "カテゴリ: " + categoryLabel + "\n"
                + "お名前: " + nameLabel + "\n"
                + "メールアドレス: " + emailLabel + "\n"
                + "内容:\n" + request.getContent() + "\n\n"
                + "送信日時: " + sendAt + "\n"
                + "ユーザー情報: " + userLabel;
    }

    /**
     * カテゴリコードを日本語ラベルに変換する
     *
     * @param category カテゴリコード
     * @return 日本語ラベル
     */
    private String resolveCategoryLabel(String category) {
        if (category == null) return "その他";
        return switch (category) {
            case "BUG" -> "バグ報告";
            case "REQUEST" -> "要望";
            default -> "その他";
        };
    }
}
