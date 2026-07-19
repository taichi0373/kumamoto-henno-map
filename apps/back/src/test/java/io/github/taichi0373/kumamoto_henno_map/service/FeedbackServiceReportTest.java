package io.github.taichi0373.kumamoto_henno_map.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatNoException;

/** FeedbackService の特典報告機能のテスト */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceReportTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // webhookUrlが空のときはSlack送信をスキップする設計のため空文字をセット
        ReflectionTestUtils.setField(feedbackService, "webhookUrl", "");
    }

    @Test
    void webhookUrl未設定のとき例外なく終了する() {
        assertThatNoException()
            .isThrownBy(() -> feedbackService.sendBenefitReport("BENEFIT_001", "○○スーパー 5%割引"));
    }

    @Test
    void 特典名がnullのとき例外なく終了する() {
        assertThatNoException()
            .isThrownBy(() -> feedbackService.sendBenefitReport("BENEFIT_001", null));
    }
}
