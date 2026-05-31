class TransactionsPage {
    get title() {
        return $('android=new UiSelector().text("Historial de transacciones")');
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }
}

export default new TransactionsPage();