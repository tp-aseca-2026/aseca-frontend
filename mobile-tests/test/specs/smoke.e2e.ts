import { expect } from '@wdio/globals'

describe('ASECA Mobile smoke test', () => {
    it('should open login screen', async () => {
        const emailInput = await $('~login_email');
        const passwordInput = await $('~login_password');
        const loginButton = await $('~login_submit');
        const registerButton = await $('~go_to_register');

        await expect(emailInput).toBeDisplayed();
        await expect(passwordInput).toBeDisplayed();
        await expect(loginButton).toBeDisplayed();
        await expect(registerButton).toBeDisplayed();
    });

    it('should navigate from login to register and back', async () => {
        await $('~go_to_register').click();

        await expect($('~register_email')).toBeDisplayed();
        await expect($('~register_password')).toBeDisplayed();
        await expect($('~register_submit')).toBeDisplayed();

        await $('~back_to_login').click();

        await expect($('~login_email')).toBeDisplayed();
    });
});