import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import PortfolioPage from '../pageobjects/portfolio.page';

describe('Portfolio buy flow', () => {
    it('should buy a stock and show it as an active position', async () => {
        const user = await createTestUser();
        const ticker = 'MSFT';
        const quantity = '10';

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goPortfolio();
        await PortfolioPage.expectVisible();

        await PortfolioPage.buyStock(ticker, quantity);

        await PortfolioPage.expectPositionVisible(ticker);
    });
});