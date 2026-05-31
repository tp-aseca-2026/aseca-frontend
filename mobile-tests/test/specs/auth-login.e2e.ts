import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';

describe('Login flow', () => {
    it('should login with an existing user and reach home dashboard', async () => {
        const user = await createTestUser();

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);

        await HomePage.expectVisible();
    });
});