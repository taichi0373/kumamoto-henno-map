import router from '@/router'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import { BenefitDetailDto } from '@/dto/benefitDetailDto'

class BenefitShareUtils {

    /**
     * 特典の固定URLを生成する
     * ルーターのベースパスを考慮してURLを組み立てる
     */
    public static buildBenefitUrl(benefitId: string): string {
        const href = router.resolve({ path: '/', query: { benefitId } }).href
        return new URL(href, window.location.origin).toString()
    }

    /**
     * 特典の固定URLをクリップボードにコピーする
     */
    public static async copyBenefitUrl(benefit: BenefitDetailDto): Promise<void> {
        if (!benefit.benefitId) return
        try {
            await navigator.clipboard.writeText(BenefitShareUtils.buildBenefitUrl(benefit.benefitId))
            ToastMessageUtils.success('URLをコピーしました')
        } catch {
            ToastMessageUtils.error('URLのコピーに失敗しました')
        }
    }
}

export { BenefitShareUtils };
