import assert from "node:assert/strict";
import { remote } from "webdriverio";

const PASSWORD = "password123";
const APP_PACKAGE = "com.aseca.mobile";
const APP_ACTIVITY = ".MainActivity";
const DEFAULT_TIMEOUT = 15000;

const selectors = {
  goToRegister: "go_to_register",
  registerSubmit: "register_submit",
  loginSubmit: "login_submit",
  logoutButton: "logout_button",
  homeDashboard: "home_dashboard"
};

let driver;

try {
  driver = await createDriver();
  const email = `appium.${Date.now()}@test.com`;

  await step("register new user", async () => {
    await tap(selectors.goToRegister);
    await typeTextField(0, email);
    await typeTextField(1, PASSWORD);
    await tap(selectors.registerSubmit);
    await expectVisible(selectors.homeDashboard);
  });

  await step("logout", async () => {
    await tap(selectors.logoutButton);
    await expectText("Acceso seguro");
  });

  await step("login existing user", async () => {
    await typeTextField(0, email);
    await typeTextField(1, PASSWORD);
    await tap(selectors.loginSubmit);
    await expectVisible(selectors.homeDashboard);
  });

  console.log("Appium auth smoke test passed");
} finally {
  if (driver) {
    await driver.deleteSession();
  }
}

async function createDriver() {
  return remote({
    hostname: process.env.APPIUM_HOST ?? "127.0.0.1",
    port: Number(process.env.APPIUM_PORT ?? 4723),
    path: "/",
    logLevel: "error",
    capabilities: {
      platformName: "Android",
      "appium:automationName": "UiAutomator2",
      "appium:deviceName": process.env.APPIUM_DEVICE_NAME ?? "Android Emulator",
      "appium:appPackage": APP_PACKAGE,
      "appium:appActivity": APP_ACTIVITY,
      "appium:noReset": false,
      "appium:autoGrantPermissions": true
    }
  });
}

async function step(name, action) {
  console.log(`step: ${name}`);
  await action();
}

async function tap(accessibilityId) {
  const element = await driver.$(`~${accessibilityId}`);
  await element.waitForDisplayed({ timeout: DEFAULT_TIMEOUT });
  await element.click();
}

async function typeTextField(index, value) {
  const fields = await driver.$$("android.widget.EditText");
  const element = fields[index];
  assert.ok(element, `Missing text field at index ${index}`);
  await element.click();
  await element.setValue(value);
}

async function expectText(text) {
  const selector = `android=new UiSelector().text("${text}")`;
  const element = await driver.$(selector);
  await element.waitForDisplayed({ timeout: DEFAULT_TIMEOUT });
  assert.equal(await element.isDisplayed(), true);
}

async function expectVisible(accessibilityId) {
  const element = await driver.$(`~${accessibilityId}`);
  await element.waitForDisplayed({ timeout: DEFAULT_TIMEOUT });
  assert.equal(await element.isDisplayed(), true);
}
