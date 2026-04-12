import { defineConfig, devices } from '@playwright/test';
import * as dotenv from 'dotenv';
import * as path from 'path';

// テスト用環境変数を読み込む
dotenv.config({ path: path.resolve(__dirname, '.env.test') });

export default defineConfig({
  testDir: './tests/e2e',
  globalSetup: './tests/e2e/global-setup.ts',
  timeout: 60000,
  retries: 0,
  use: {
    baseURL: 'http://localhost:3000',
    headless: !!process.env.CI,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
   webServer: {
     command: 'npm run serve',
     url: 'http://localhost:3000',
     reuseExistingServer: true,
     timeout: 120000,
   },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
