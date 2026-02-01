const path = require('path');

module.exports = {
  stories: ["../src/**/*.stories.@(js|jsx|ts|tsx|mdx)"],
  addons: [
    "@storybook/addon-webpack5-compiler-babel",
    "@storybook/addon-essentials"
  ],
  framework: {
    name: "@storybook/vue3-webpack5",
    options: {},
  },
  typescript: {
    check: false,
  },
  webpackFinal: async (config: any) => {
    // CSS/SCSS support
    config.module.rules.push({
      test: /\.scss$/,
      use: ['style-loader', 'css-loader', 'sass-loader'],
      include: path.resolve(__dirname, '../'),
    });

    // Alias for @ imports
    config.resolve.alias['@'] = path.resolve(__dirname, '../src');
    
    return config;
  }
};