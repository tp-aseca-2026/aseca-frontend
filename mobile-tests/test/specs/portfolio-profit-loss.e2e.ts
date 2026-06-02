import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import PortfolioPage from '../pageobjects/portfolio.page';

describe('Portfolio profit and loss flow', () => {
    it('should display portfolio summary and P&L after buying a stock', async () => {
        const user = await createTestUser();
        const ticker = 'MSFT';

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goPortfolio();
        await PortfolioPage.expectVisible();

        await PortfolioPage.buyStock(ticker, '10');
        await PortfolioPage.expectPositionVisible(ticker);

        await PortfolioPage.expectSummaryVisible();
    });
});