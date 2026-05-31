class NavigationPage {
    get homeTab() {
        return $('~nav_home');
    }

    get portfolioTab() {
        return $('~nav_portfolio');
    }

    get watchlistTab() {
        return $('~nav_watchlist');
    }

    get edgarTab() {
        return $('~nav_edgar');
    }

    get transactionsTab() {
        return $('~nav_transactions');
    }

    async goHome() {
        await this.homeTab.click();
    }

    async goPortfolio() {
        await this.portfolioTab.click();
    }

    async goWatchlist() {
        await this.watchlistTab.click();
    }

    async goEdgar() {
        await this.edgarTab.click();
    }

    async goTransactions() {
        await this.transactionsTab.click();
    }
}

export default new NavigationPage();