import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import PortfolioPage from '../pageobjects/portfolio.page';
import TransactionsPage from '../pageobjects/transactions.page';

describe('Transactions flow', () => {
    it('should display buy operations in transaction history', async () => {
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

        await NavigationPage.goTransactions();
        await TransactionsPage.expectVisible();

        await TransactionsPage.expectBuyTransaction(ticker);
    });
});