class HomePage {
    get dashboardTitle() {
        return $('~home_dashboard');
    }

    get logoutButton() {
        return $('~logout_button');
    }

    async expectVisible() {
        await expect(this.dashboardTitle).toBeDisplayed();
        await expect(this.logoutButton).toBeDisplayed();
    }

    async logout() {
        await this.logoutButton.click();
    }
}

export default new HomePage();