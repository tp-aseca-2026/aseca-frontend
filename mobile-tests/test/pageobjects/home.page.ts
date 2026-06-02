class HomePage {
    get dashboardTitle() {
        return $('~home_dashboard');
    }

    get logoutButton() {
        return $('~logout_button');
    }

    get updatePricesButton() {
        return $('~prices_update_button');
    }

    get latestPricesTitle() {
        return $('android=new UiSelector().text("Últimos precios")');
    }

    async expectVisible() {
        await expect(this.dashboardTitle).toBeDisplayed();
        await expect(this.logoutButton).toBeDisplayed();
    }

    async updatePrices() {
        await this.updatePricesButton.waitForDisplayed({ timeout: 10000 });
        await this.updatePricesButton.click();
    }

    async expectLatestPricesSection() {
        await expect(this.latestPricesTitle).toBeDisplayed();
    }
}

export default new HomePage();