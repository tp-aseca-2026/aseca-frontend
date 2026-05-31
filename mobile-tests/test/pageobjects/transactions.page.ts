class TransactionsPage {
    get title() {
        return $('android=new UiSelector().text("Historial de transacciones")');
    }

    transactionTicker(ticker: string) {
        return $(`~transaction_ticker_${ticker}`);
    }

    transactionType(type: 'BUY' | 'SELL', ticker: string) {
        return $(`~transaction_type_${type}_${ticker}`);
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }

    async expectBuyTransaction(ticker: string) {
        await expect(this.transactionTicker(ticker)).toBeDisplayed();
        await expect(this.transactionType('BUY', ticker)).toBeDisplayed();
    }
}

export default new TransactionsPage();