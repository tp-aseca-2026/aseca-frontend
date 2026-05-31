class EdgarPage {
    get title() {
        return $('android=new UiSelector().text("Buscar empresa")');
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }
}

export default new EdgarPage();