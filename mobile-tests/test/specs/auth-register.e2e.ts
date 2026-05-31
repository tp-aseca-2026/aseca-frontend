import LoginPage from '../pageobjects/login.page';
import RegisterPage from '../pageobjects/register.page';
import HomePage from '../pageobjects/home.page';

describe('Register flow', () => {
    const email = `appiumregister${Date.now()}@aseca.com`;
    const password = 'Password123';

    it('should register a new user and reach home dashboard', async () => {
        await LoginPage.goToRegister();

        await RegisterPage.expectVisible();
        await RegisterPage.register(email, password);

        await HomePage.expectVisible();
    });
});