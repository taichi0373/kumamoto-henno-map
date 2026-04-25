<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">自主返納支援制度とは</h1>
      </div>
      
      <div class="content-sections">
        <!-- 制度について -->
        <div class="info-section">
          <h2 class="section-title">運転免許証の自主返納制度について</h2>
          <p class="section-description">
            運転免許証の自主返納制度は、加齢に伴う身体機能の変化等により、
            自動車等の運転に不安を感じる方が、運転免許証を自主的に返納できる制度です。
          </p>
        </div>
        
        <!-- メリット -->
        <div class="info-section">
          <h2 class="section-title">自主返納のメリット</h2>
          <div class="merit-list">
            <div class="merit-item">
              <i class="pi pi-shield"></i>
              <span>交通事故のリスクを減らすことができます</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-gift"></i>
              <span>様々な特典やサービスを利用できます</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-truck"></i>
              <span>公共交通機関の利用促進に繋がります</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-heart"></i>
              <span>地域社会の安全向上に貢献できます</span>
            </div>
          </div>
        </div>
        
        <!-- 返納手続き -->
        <div class="info-section">
          <h2 class="section-title">返納手続きについて</h2>
          <div class="procedure-steps">
            <div class="step-card">
              <div class="step-number">1</div>
              <div class="step-content">
                <h3 class="step-title">必要書類の準備</h3>
                <p class="step-description">運転免許証、本人確認書類を準備してください。</p>
              </div>
            </div>
            
            <div class="step-card">
              <div class="step-number">2</div>
              <div class="step-content">
                <h3 class="step-title">警察署・運転免許センターへ</h3>
                <p class="step-description">最寄りの警察署または運転免許センターで手続きを行います。</p>
              </div>
            </div>
            
            <div class="step-card">
              <div class="step-number">3</div>
              <div class="step-content">
                <h3 class="step-title">運転経歴証明書の交付</h3>
                <p class="step-description">希望者には運転経歴証明書が交付されます（有料）。</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 特典について -->
        <div class="info-section">
          <h2 class="section-title">特典について</h2>
          <p class="section-description">
            運転免許証を自主返納された方には、熊本県内の多くの店舗や施設で
            様々な特典やサービスをご利用いただけます。
          </p>
          
          <div class="benefit-categories">
            <div class="category-card">
              <div class="category-icon">🛒</div>
              <h3 class="category-title">ショッピング</h3>
              <p class="category-description">商品割引、配送サービスなど</p>
            </div>
            
            <div class="category-card">
              <div class="category-icon">🍽️</div>
              <h3 class="category-title">飲食店</h3>
              <p class="category-description">料金割引、ドリンクサービスなど</p>
            </div>
            
            <div class="category-card">
              <div class="category-icon">🏨</div>
              <h3 class="category-title">宿泊・温泉</h3>
              <p class="category-description">宿泊料金割引、入浴料割引など</p>
            </div>
            
            <div class="category-card">
              <div class="category-icon">🚌</div>
              <h3 class="category-title">交通機関</h3>
              <p class="category-description">運賃割引、回数券特典など</p>
            </div>
          </div>
        </div>
        
        <!-- お問い合わせ -->
        <div class="info-section">
          <h2 class="section-title">お問い合わせ</h2>
          <div class="contact-card">
            <h3 class="contact-title">熊本県警察本部 運転免許課</h3>
            <div class="contact-details">
              <div class="contact-item">
                <i class="pi pi-phone"></i>
                <span>電話：096-233-0110</span>
              </div>
              <div class="contact-item">
                <i class="pi pi-clock"></i>
                <span>受付時間：平日 8:30-17:15</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 戻るボタン -->
        <div class="back-section">
          <AppButton
            type="button"
            label="マップに戻る"
            severity="primary"
            size="large"
            icon="pi pi-map"
            @click="$router.push('/')"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppButton from '../components/atoms/AppButton.vue'
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.page {
  height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  overflow-y: auto;
  padding: 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100%;
  box-sizing: border-box;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  position: relative;
  display: inline-block;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.content-sections {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.info-section {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.info-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 20px;
  border-left: 4px solid #4299e1;
  padding-left: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: "📋";
  font-size: 18px;
}

.section-description {
  color: #4a5568;
  line-height: 1.7;
  margin-bottom: 0;
  font-size: 15px;
}

/* メリットリスト */
.merit-list {
  display: grid;
  gap: 15px;
}

.merit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.merit-item:hover {
  background: #edf2f7;
  transform: translateX(5px);
}

.merit-item i {
  color: #4299e1;
  font-size: 18px;
  width: 20px;
}

.merit-item span {
  color: #4a5568;
  font-weight: 500;
}

/* 手続きステップ */
.procedure-steps {
  display: grid;
  gap: 20px;
  margin-top: 25px;
}

.step-card {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.step-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.step-card:hover {
  background: #edf2f7;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.step-number {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 8px 0;
}

.step-description {
  color: #4a5568;
  margin: 0;
  line-height: 1.6;
}

/* 特典カテゴリー */
.benefit-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 25px;
}

.category-card {
  background: white;
  padding: 25px 20px;
  border-radius: 10px;
  text-align: center;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.category-icon {
  font-size: 32px;
  margin-bottom: 12px;
  display: block;
}

.category-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 8px 0;
}

.category-description {
  color: #718096;
  margin: 0;
  font-size: 13px;
  line-height: 1.5;
}

/* お問い合わせセクション */
.contact-card {
  display: flex;
  flex-direction: column;
  padding: 15px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.2s ease;
}
.contact-card:hover {
  background: #edf2f7;
  transform: translateY(-2px);
}
.contact-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5568;
  font-size: 14px;
}

.contact-item i {
  color: #4299e1;
  width: 16px;
}

/* 戻るボタンセクション */
.back-section {
  text-align: center;
  margin-top: 40px;
  padding: 20px;
}

/* アニメーション */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.info-section {
  animation: fadeInUp 0.6s ease-out;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .step-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .benefit-categories {
    grid-template-columns: 1fr;
  }
  
  .contact-details {
    text-align: center;
  }
  
  .contact-item {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page {
    padding: 10px 0;
  }
  
  .page-header {
    margin-bottom: 20px;
    padding: 20px 0;
  }
  
  .content-sections {
    gap: 15px;
  }
  
  .info-section {
    padding: 15px;
  }
  
  .section-title {
    font-size: 18px;
  }
}
</style>
