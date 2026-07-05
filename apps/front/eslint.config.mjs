import eslint from '@eslint/js';
import pluginVue from 'eslint-plugin-vue';
import tsParser from '@typescript-eslint/parser';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import globals from 'globals';

/** 共通グローバル変数 */
const commonGlobals = {
  ...globals.node,
  ...globals.browser,
  ...globals.es2015,
  ...globals.jest,
};

/** 共通TypeScriptルール */
const commonTsRules = {
  'no-unused-vars': 'off',
  '@typescript-eslint/no-unused-vars': 'warn',
  '@typescript-eslint/no-explicit-any': 'off',
};

export default [
  // 全ファイル共通: グローバル変数設定
  {
    languageOptions: {
      globals: commonGlobals,
    },
  },

  // eslint 推奨ルール
  eslint.configs.recommended,

  // Vue 推奨ルール（essential）
  ...pluginVue.configs['flat/essential'],

  // Vue ファイル: TypeScriptパーサーを設定
  {
    files: ['**/*.vue'],
    languageOptions: {
      parserOptions: {
        parser: tsParser,
        ecmaVersion: 2020,
        sourceType: 'module',
      },
    },
    plugins: {
      '@typescript-eslint': tsPlugin,
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      ...commonTsRules,
    },
  },

  // TypeScript ファイル
  {
    files: ['**/*.ts', '**/*.tsx'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module',
      },
    },
    plugins: {
      '@typescript-eslint': tsPlugin,
    },
    rules: commonTsRules,
  },
];
