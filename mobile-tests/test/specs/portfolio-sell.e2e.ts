import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import PortfolioPage from '../pageobjects/portfolio.page';

describe('Portfolio sell flow', () => {
    it('should sell part of an active position', async () => {
        const user = await createTestUser();
        const ticker = 'MSFT';

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goPortfolio();
        await PortfolioPage.expectVisible();

        await PortfolioPage.buyStock(ticker, '10');
        await PortfolioPage.expectPositionVisible(ticker);

        await PortfolioPage.sellStock(ticker, '4');
        await PortfolioPage.expectPositionVisible(ticker);
    });
});