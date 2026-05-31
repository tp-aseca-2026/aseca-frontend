class WatchlistPage {
    get title() {
        return $('android=new UiSelector().text("Watchlist")');
    }

    get addButton() {
        return $('~watchlist_add_button');
    }

    stockChoice(ticker: string) {
        return $(`~watchlist_stock_choice_${ticker}`);
    }

    removeButton(ticker: string) {
        return $(`~watchlist_remove_${ticker}`);
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }

    async addStock(ticker: string) {
        const choice = await this.stockChoice(ticker);
        await choice.waitForDisplayed({ timeout: 10000 });
        await choice.click();

        await this.addButton.waitForDisplayed({ timeout: 10000 });
        await this.addButton.click();
    }

    async expectStockAdded(ticker: string) {
        const removeButton = await this.removeButton(ticker);
        await removeButton.waitForDisplayed({ timeout: 15000 });
        await expect(removeButton).toBeDisplayed();
    }

    async removeStock(ticker: string) {
        const remove = await this.removeButton(ticker);
        await remove.waitForDisplayed({ timeout: 10000 });
        await remove.click();
    }

    async expectStockRemoved(ticker: string) {
        await browser.waitUntil(
            async () => !(await this.removeButton(ticker).isDisplayed().catch(() => false)),
            {
                timeout: 15000,
                timeoutMsg: `${ticker} was still visible in watchlist`,
            },
        );
    }
}

export default new WatchlistPage();