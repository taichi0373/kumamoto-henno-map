import AppFormError from './AppFormError.vue';

export default {
  title: 'Design System/Atoms/AppFormError',
  component: AppFormError,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    error: {
      control: 'object',
      description: 'エラー情報',
    },
  },
};

export const Default = {
  args: {
    error: [{ type: 1, message: '必須項目です' }],
  },
};

export const Warning = {
  args: {
    error: [{ type: 2, message: '入力内容を確認してください' }],
  },
};

export const Multiple = {
  args: {
    error: [
      { type: 1, message: '必須項目です' },
      { type: 2, message: '30文字以内で入力してください' },
    ],
  },
};
