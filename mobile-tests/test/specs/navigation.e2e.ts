import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import PortfolioPage from '../pageobjects/portfolio.page';
import WatchlistPage from '../pageobjects/watchlist.page';
import EdgarPage from '../pageobjects/edgar.page';
import TransactionsPage from '../pageobjects/transactions.page';

describe('Main navigation flow', () => {
    it('should navigate through the main app sections', async () => {
        const user = await createTestUser();

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goPortfolio();
        await PortfolioPage.expectVisible();

        await NavigationPage.goWatchlist();
        await WatchlistPage.expectVisible();

        await NavigationPage.goEdgar();
        await EdgarPage.expectVisible();

        await NavigationPage.goTransactions();
        await TransactionsPage.expectVisible();

        await NavigationPage.goHome();
        await HomePage.expectVisible();
    });
});