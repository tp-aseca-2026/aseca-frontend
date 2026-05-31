class WatchlistPage {
    get title() {
        return $('android=new UiSelector().text("Watchlist")');
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }
}

export default new WatchlistPage();