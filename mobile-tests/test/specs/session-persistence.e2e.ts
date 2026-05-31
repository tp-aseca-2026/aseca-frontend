import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';

describe('Session persistence flow', () => {
    it('should keep the user authenticated after reopening the app', async () => {
        const user = await createTestUser();

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);

        await HomePage.expectVisible();

        await browser.terminateApp('com.aseca.mobile');
        await browser.pause(1000);

        await browser.activateApp('com.aseca.mobile');

        await HomePage.expectVisible();
    });
});