import { createTestUser } from '../helpers/api';
import LoginPage from '../pageobjects/login.page';
import HomePage from '../pageobjects/home.page';
import NavigationPage from '../pageobjects/navigation.page';
import EdgarPage from '../pageobjects/edgar.page';

describe('EDGAR filing link flow', () => {
    it('should open a recent filing link outside the app', async () => {
        const user = await createTestUser();
        const ticker = 'MSFT';

        await LoginPage.expectVisible();
        await LoginPage.login(user.email, user.password);
        await HomePage.expectVisible();

        await NavigationPage.goEdgar();
        await EdgarPage.expectVisible();

        await EdgarPage.searchCompany(ticker);
        await EdgarPage.selectCompany(ticker);
        await EdgarPage.expectCompanyDetailsVisible();

        await EdgarPage.openFirstFiling();

        await browser.waitUntil(
            async () => (await browser.getCurrentPackage()) !== 'com.aseca.mobile',
            {
                timeout: 10000,
                timeoutMsg: 'Filing link did not open outside the app',
            },
        );
    });
});
