import type { StorybookConfig } from '@storybook/vue3-vite';

const config: StorybookConfig = {
  stories: ["../src/**/*.stories.@(js|jsx|ts|tsx|mdx)"],
  addons: [],
  framework: {
    name: "@storybook/vue3-vite",
    options: {},
  },
  /** Storybook では Service Worker・マニフェストを生成しないよう PWA プラグインを除外する */
  async viteFinal(config) {
    config.plugins = (config.plugins ?? []).flat(Infinity).filter(
      (plugin) =>
        !(plugin && typeof plugin === 'object' && 'name' in plugin && plugin.name.startsWith('vite-plugin-pwa')),
    );
    return config;
  },
};

export default config;
