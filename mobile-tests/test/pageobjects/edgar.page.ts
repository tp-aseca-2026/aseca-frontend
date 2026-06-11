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

    get filingLink() {
        return $('~edgar_filing_link');
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

    async scrollToFilingLink() {
        const { width, height } = await browser.getWindowSize();

        // tuve que hacer un scroll mas largo porque no encontraba el link de filing
        for (let attempt = 0; attempt < 8; attempt += 1) {
            if (await this.filingLink.isDisplayed().catch(() => false)) return;

            await browser.execute('mobile: scrollGesture', {
                left: Math.round(width * 0.1),
                top: Math.round(height * 0.2),
                width: Math.round(width * 0.8),
                height: Math.round(height * 0.65),
                direction: 'down',
                percent: 0.85,
            });

            await browser.pause(300);
        }
    }

    async expectCompanyDetailsVisible() {
        await this.metricsTitle.waitForDisplayed({ timeout: 30000 });
        await expect(this.metricsTitle).toBeDisplayed();

        await this.scrollToText('Evolución histórica');
        await this.historicalTitle.waitForDisplayed({ timeout: 10000 });
        await expect(this.historicalTitle).toBeDisplayed();
    }

    async openFirstFiling() {
        await this.scrollToFilingLink();
        await this.filingLink.waitForDisplayed({ timeout: 10000 });
        await this.filingLink.click();
    }
}

export default new EdgarPage();
