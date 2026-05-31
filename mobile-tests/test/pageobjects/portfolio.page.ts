class PortfolioPage {
    get title() {
        return $('android=new UiSelector().text("Portfolio completo")');
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }
}

export default new PortfolioPage();