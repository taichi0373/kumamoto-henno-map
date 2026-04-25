/**
 * 管理者ルートアクセスチェック
 *
 * @param requiresAdmin - ルートが管理者権限を要求するか
 * @param isAdmin - 現在のユーザーが管理者か
 * @returns リダイレクト先のパス（リダイレクト不要の場合はnull）
 */
export function checkAdminRouteAccess(requiresAdmin: boolean, isAdmin: boolean): string | null {
  if (requiresAdmin && !isAdmin) {
    return '/';
  }
  return null;
}
