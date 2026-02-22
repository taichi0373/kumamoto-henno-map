import ToastMessageUtils from './toastMessageUtils';

/**
 * ToastMessageUtilsのテストクラス
 */
describe('ToastMessageUtils', () => {

  const mockAdd = jest.fn();
  const mockRemoveGroup = jest.fn();
  const mockRemoveAllGroups = jest.fn();
  const mockToast = {
    add: mockAdd,
    removeGroup: mockRemoveGroup,
    removeAllGroups: mockRemoveAllGroups,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    ToastMessageUtils.init(mockToast);
  });

  // ----------------------------------------------------------------
  // success
  // ----------------------------------------------------------------
  describe('success', () => {

    test('severity: success、life: 5000でaddが呼ばれること', () => {
      ToastMessageUtils.success('成功メッセージ');
      expect(mockAdd).toHaveBeenCalledTimes(1);
      expect(mockAdd).toHaveBeenCalledWith({
        severity: 'success',
        detail: '成功メッセージ',
        life: 5000,
      });
    });
  });

  // ----------------------------------------------------------------
  // notice
  // ----------------------------------------------------------------
  describe('notice', () => {

    test('severity: info、life: 5000でaddが呼ばれること', () => {
      ToastMessageUtils.notice('情報メッセージ');
      expect(mockAdd).toHaveBeenCalledTimes(1);
      expect(mockAdd).toHaveBeenCalledWith({
        severity: 'info',
        detail: '情報メッセージ',
        life: 5000,
      });
    });
  });

  // ----------------------------------------------------------------
  // error
  // ----------------------------------------------------------------
  describe('error', () => {

    test('severity: error、life: 5000でaddが呼ばれること', () => {
      ToastMessageUtils.error('エラーメッセージ');
      expect(mockAdd).toHaveBeenCalledTimes(1);
      expect(mockAdd).toHaveBeenCalledWith({
        severity: 'error',
        detail: 'エラーメッセージ',
        life: 5000,
      });
    });
  });

  // ----------------------------------------------------------------
  // warning
  // ----------------------------------------------------------------
  describe('warning', () => {

    test('severity: warn、life: 5000でaddが呼ばれること', () => {
      ToastMessageUtils.warning('警告メッセージ');
      expect(mockAdd).toHaveBeenCalledTimes(1);
      expect(mockAdd).toHaveBeenCalledWith({
        severity: 'warn',
        detail: '警告メッセージ',
        life: 5000,
      });
    });
  });

  // ----------------------------------------------------------------
  // remove
  // ----------------------------------------------------------------
  describe('remove', () => {

    test('removeAllGroupsが1回呼ばれること', () => {
      ToastMessageUtils.remove();
      expect(mockRemoveAllGroups).toHaveBeenCalledTimes(1);
    });

    test('removeAllGroups呼び出し時にaddは呼ばれないこと', () => {
      ToastMessageUtils.remove();
      expect(mockAdd).not.toHaveBeenCalled();
    });
  });
});
