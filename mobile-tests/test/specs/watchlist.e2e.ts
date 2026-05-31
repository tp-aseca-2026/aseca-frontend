import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import WatchlistPage from '../pageobjects/watchlist.page';

describe('Watchlist flow', () => {
    it('should add and remove a stock from the watchlist', async () => {
        const user = await createTestUser();
        const ticker = 'MSFT';

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goWatchlist();
        await WatchlistPage.expectVisible();

        await WatchlistPage.addStock(ticker);
        await WatchlistPage.expectStockAdded(ticker);

        await WatchlistPage.removeStock(ticker);
        await WatchlistPage.expectStockRemoved(ticker);
    });
});