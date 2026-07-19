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

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);

    /** 送信日時フォーマット */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Slack Webhook URL */
    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    /** HTTP クライアント（再利用） */
    private final RestTemplate restTemplate;

    /**
     * コンストラクタ
     *
     * @param restTemplate HTTPクライアント
     */
    public FeedbackService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * フィードバックをSlackに送信する
     *
     * @param request       フィードバックリクエスト
     * @param username      ログインユーザー名（未ログイン時はnull）
     */
    public void sendFeedback(FeedbackRequestDto request, String username) {
        String message = buildMessage(request, username);

        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("[Feedback] Webhook URL未設定のためログ出力のみ:\n{}", message);
            return;
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);
            restTemplate.postForEntity(webhookUrl, payload, String.class);
            log.info("[Feedback] Slackへの送信が完了しました");
        } catch (Exception e) {
            log.error("[Feedback] Slack送信に失敗しました: {}", e.getMessage(), e);
        }
    }

    /**
     * 特典情報の報告をSlackに送信する
     *
     * @param benefitId  報告対象の特典ID
     * @param benefitName 報告対象の特典名
     */
    public void sendBenefitReport(String benefitId, String benefitName) {
        String message = buildBenefitReportMessage(benefitId, benefitName);
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("Slack Webhook URL未設定のため特典報告をスキップ: benefitId={}", benefitId);
            return;
        }
        try {
            Map<String, String> body = new HashMap<>();
            body.put("text", message);
            restTemplate.postForEntity(webhookUrl, body, String.class);
            log.info("特典報告をSlackに送信しました: benefitId={}", benefitId);
        } catch (Exception e) {
            log.error("特典報告のSlack送信に失敗しました: benefitId={}", benefitId, e);
        }
    }

    /**
     * 特典報告用Slackメッセージを組み立てる
     *
     * @param benefitId   報告対象の特典ID
     * @param benefitName 報告対象の特典名
     * @return Slackメッセージ文字列
     */
    private String buildBenefitReportMessage(String benefitId, String benefitName) {
        String name = (benefitName != null) ? benefitName : "（不明）";
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return String.format("""
                ⚠️ 特典情報の報告
                特典名: %s
                特典ID: %s
                報告日時: %s
                """, name, benefitId, now);
    }

    /**
     * Slackメッセージ本文を組み立てる
     *
     * @param request  フィードバックリクエスト
     * @param username ログインユーザー名（未ログイン時はnull）
     * @return Slackメッセージ文字列
     */
    private String buildMessage(FeedbackRequestDto request, String username) {
        String categoryLabel = getCategoryLabel(request.getCategory());
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
    private String getCategoryLabel(String category) {
        if (category == null) return "その他";
        return switch (category) {
            case "BUG" -> "バグ報告";
            case "REQUEST" -> "要望";
            default -> "その他";
        };
    }
}
