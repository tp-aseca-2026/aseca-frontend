class TransactionsPage {
    get title() {
        return $('android=new UiSelector().text("Historial de transacciones")');
    }

    transactionItem(type: 'BUY' | 'SELL', ticker: string) {
        return $(`~transaction_item_${type}_${ticker}`);
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }

    async expectBuyTransaction(ticker: string) {
        const item = await this.transactionItem('BUY', ticker);
        await item.waitForDisplayed({ timeout: 10000 });
        await expect(item).toBeDisplayed();
    }
}

export default new TransactionsPage();