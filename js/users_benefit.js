// 利用できる特典ページ
const users_benefit_tab = document.getElementById('users-benefit-tab');
const usersBenefitsResult = document.getElementById('users-benefit-result');

document.addEventListener('DOMContentLoaded', function (event) {
    event.preventDefault();
    usersBenefitsResult.innerHTML = '';
    const benefit_icon_dict = {
        "バス": "🚌",
        "コミュニティバス": "🚐",
        "電車": "🚆",
        "路面電車": "🚋",
        "タクシー": "🚕",
        "乗合タクシー": "🚕",
        "コミュニティバス／乗合タクシー": "🚐🚕",
        "ICカード": "💳",
        "その他": "🎁"
    }
    if (userToken) {
        const userId = atob(userToken);
        // ユーザーの特典を取得
        const fd_users_benefit = new FormData();
        fd_users_benefit.append('user_id', userId);
        fetch("./php/get_users_benefit.php", {
            method: "POST",
            body: fd_users_benefit,
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.success) {
                    const userBenefits = data.benefits;
                    if (userBenefits.length === 0) {
                        const message = document.createElement('p');
                        message.textContent = '利用可能な特典はありません';
                        usersBenefitsResult.appendChild(message);
                    }

                    // 特典データをHTMLに追加
                    userBenefits.forEach((benefit) => {
                        const benefitItem = document.createElement('div');
                        benefitItem.classList.add('benefit-item');
                        benefitItem.setAttribute('data-benefit-id', benefit.id);

                        const icon = benefit_icon_dict[benefit.category] || "🎁";

                        // 年齢制限の変換
                        let ageRestriction = '';
                        if (benefit.min_age && benefit.max_age) {
                            ageRestriction = `${benefit.min_age} 歳 ～ ${benefit.max_age} 歳`;
                        } else if (benefit.min_age) {
                            ageRestriction = `${benefit.min_age} 歳以上`;
                        } else if (benefit.max_age) {
                            ageRestriction = ` ～ ${benefit.max_age}歳`;
                        } else {
                            ageRestriction = '制限なし';
                        }
                        // 運転免許所持状況の変換
                        let licenseStatus = '';
                        switch (benefit.license_status) {
                            case 'returned':
                                licenseStatus = '自主返納した方';
                                break;
                            case 'any':
                            case '':
                                licenseStatus = 'どなたでも可';
                                break;
                            case 'expired':
                                licenseStatus = '失効した方';
                                break;
                            case 'unlicensed':
                                licenseStatus = '所持していない方';
                                break;
                            default:
                                licenseStatus = '不明';
                                break;
                        }
                        benefitItem.innerHTML = `
                                <h5>${icon} ${benefit.name}</h5>
                                <p><strong>特典内容</strong><br> ${benefit.detail}</p>
                                <p><strong>対象地域</strong><br> ${benefit.municipality_name ? `${benefit.municipality_name}に居住している方` : '熊本県に居住している方'}</p>
                                <p><strong>対象年齢</strong><br> ${ageRestriction}</p>
                                <p><strong>運転免許の所持状況</strong><br> ${licenseStatus}</p>
                                <p><strong>有効期限</strong><br> ${benefit.expiration || '　'}</p>
                                <p><strong>備考</strong><br> ${benefit.note || '　'}</p>
                                <p><strong>問い合わせ先</strong><br> ${benefit.tel_number}</p>
                        `;
                        usersBenefitsResult.appendChild(benefitItem);
                    });
                } else {
                    const message = document.createElement('p');
                    message.textContent = '利用可能な特典はありません';
                    usersBenefitsResult.appendChild(message);
                }
            })
            .catch((error) => {
                console.error("フェッチエラー:", error);
                console.log("リクエストに失敗しました。");
            });
    } else {
        const message = document.createElement('p');
        message.textContent = '利用するには';

        const loginLink = document.createElement('a');
        loginLink.href = '/navi_project/login.php';
        loginLink.textContent = 'ログイン';

        const messageEnd = document.createTextNode('してください');

        message.appendChild(loginLink);
        message.appendChild(messageEnd);
        usersBenefitsResult.appendChild(message);
    }
});

