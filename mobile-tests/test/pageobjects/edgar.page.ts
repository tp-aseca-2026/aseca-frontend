import { typeByAccessibilityId } from '../helpers/input';

class EdgarPage {
    get title() {
        return $('android=new UiSelector().text("Buscar empresa")');
    }

    get searchButton() {
        return $('~edgar_search_button');
    }

    companyResult(ticker: string) {
        return $(`~edgar_company_${ticker}`);
    }

    get metricsTitle() {
        return $('android=new UiSelector().text("Métricas financieras")');
    }

    get historicalTitle() {
        return $('android=new UiSelector().text("Evolución histórica")');
    }

    async expectVisible() {
        await expect(this.title).toBeDisplayed();
    }

    async searchCompany(query: string) {
        await typeByAccessibilityId('edgar_search_input', query);

        await this.searchButton.waitForDisplayed({ timeout: 10000 });
        await this.searchButton.click();
    }

    async selectCompany(ticker: string) {
        const company = await this.companyResult(ticker);
        await company.waitForDisplayed({ timeout: 20000 });
        await company.click();
    }

    async scrollToText(text: string) {
        await $(
            `android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text("${text}"))`,
        );
    }

    async expectCompanyDetailsVisible() {
        await this.metricsTitle.waitForDisplayed({ timeout: 30000 });
        await expect(this.metricsTitle).toBeDisplayed();

        await this.scrollToText('Evolución histórica');
        await this.historicalTitle.waitForDisplayed({ timeout: 10000 });
        await expect(this.historicalTitle).toBeDisplayed();
    }
}

export default new EdgarPage();