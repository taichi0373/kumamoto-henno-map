import { checkAdminRouteAccess } from './adminGuard';

/**
 * 管理者ルートガードロジックのテスト
 */
describe('checkAdminRouteAccess', () => {

  describe('requiresAdmin が true の場合', () => {

    test('管理者でないユーザーは "/" へのリダイレクトパスを返す', () => {
      expect(checkAdminRouteAccess(true, false)).toBe('/');
    });

    test('管理者ユーザーはnullを返す（リダイレクト不要）', () => {
      expect(checkAdminRouteAccess(true, true)).toBeNull();
    });
  });

  describe('requiresAdmin が false の場合', () => {

    test('管理者でないユーザーもnullを返す（リダイレクト不要）', () => {
      expect(checkAdminRouteAccess(false, false)).toBeNull();
    });

    test('管理者ユーザーもnullを返す（リダイレクト不要）', () => {
      expect(checkAdminRouteAccess(false, true)).toBeNull();
    });
  });
});
