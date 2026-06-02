import { typeByAccessibilityId } from '../helpers/input';

class RegisterPage {
    get emailInput() {
        return $('~register_email');
    }

    get passwordInput() {
        return $('~register_password');
    }

    get submitButton() {
        return $('~register_submit');
    }

    get backToLoginButton() {
        return $('~back_to_login');
    }

    async expectVisible() {
        await expect(this.emailInput).toBeDisplayed();
        await expect(this.passwordInput).toBeDisplayed();
        await expect(this.submitButton).toBeDisplayed();
    }

    async register(email: string, password: string) {
        await typeByAccessibilityId('register_email', email);
        await typeByAccessibilityId('register_password', password);
        await this.submitButton.click();
    }

    async backToLogin() {
        await this.backToLoginButton.click();
    }
}

export default new RegisterPage();