import { typeByAccessibilityId } from '../helpers/input';

class LoginPage {
    get emailInput() {
        return $('~login_email');
    }

    get passwordInput() {
        return $('~login_password');
    }

    get submitButton() {
        return $('~login_submit');
    }

    get goToRegisterButton() {
        return $('~go_to_register');
    }

    async expectVisible() {
        await expect(this.emailInput).toBeDisplayed();
        await expect(this.passwordInput).toBeDisplayed();
        await expect(this.submitButton).toBeDisplayed();
    }

    async goToRegister() {
        await this.goToRegisterButton.click();
    }

    async login(email: string, password: string) {
        await typeByAccessibilityId('login_email', email);
        await typeByAccessibilityId('login_password', password);
        await this.submitButton.click();
    }
}

export default new LoginPage();