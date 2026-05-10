import path from 'path';
import type { StorybookConfig } from '@storybook/vue3-vite';
import { mergeConfig } from 'vite';

const config: StorybookConfig = {
  stories: ["../src/**/*.stories.@(js|jsx|ts|tsx|mdx)"],
  addons: [],
  framework: {
    name: "@storybook/vue3-vite",
    options: {},
  },
  viteFinal: async (viteConfig) => {
    return mergeConfig(viteConfig, {
      resolve: {
        alias: {
          '@': path.resolve(__dirname, '../src'),
        },
      },
    });
  },
};

export default config;
