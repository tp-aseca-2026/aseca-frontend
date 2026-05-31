import { typeByAccessibilityId } from '../helpers/input';

class PortfolioPage {
    get title() {
        return $('android=new UiSelector().text("Portfolio completo")');
    }

    get openBuyDialogButton() {
        return $('~portfolio_open_buy_dialog');
    }

    get openSellDialogButton() {
        return $('~portfolio_open_sell_dialog');
    }

    get quantityInput() {
        return $('~transaction_quantity_input');
    }

    get submitTransactionButton() {
        return $('~transaction_submit_button');
    }

    stockChoice(ticker: string) {
        return $(`~transaction_stock_choice_${ticker}`);
    }

    positionTicker(ticker: string) {
        return $(`android=new UiSelector().text("${ticker}")`);
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }

    async openBuyDialog() {
        await this.openBuyDialogButton.waitForDisplayed({ timeout: 10000 });
        await this.openBuyDialogButton.click();
    }

    async selectStock(ticker: string) {
        const stock = await this.stockChoice(ticker);
        await stock.waitForDisplayed({ timeout: 10000 });
        await stock.click();
    }

    async fillQuantity(quantity: string) {
        await typeByAccessibilityId('transaction_quantity_input', quantity);
    }

    async submitTransaction() {
        await this.submitTransactionButton.waitForDisplayed({ timeout: 10000 });
        await this.submitTransactionButton.click();
    }

    async buyStock(ticker: string, quantity: string) {
        await this.openBuyDialog();
        await this.selectStock(ticker);
        await this.fillQuantity(quantity);
        await this.submitTransaction();
    }

    async expectPositionVisible(ticker: string) {
        await expect(this.positionTicker(ticker)).toBeDisplayed();
    }
}

export default new PortfolioPage();