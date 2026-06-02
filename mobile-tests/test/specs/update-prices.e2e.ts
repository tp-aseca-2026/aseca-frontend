import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';

describe('Update prices flow', () => {
    it('should allow the user to update latest prices from home', async () => {
        const user = await createTestUser();

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);

        await HomePage.expectVisible();
        await HomePage.expectLatestPricesSection();

        await HomePage.updatePrices();

        await HomePage.expectLatestPricesSection();
    });
});