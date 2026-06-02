function escapeAdbText(value: string) {
    return value
        .replace(/ /g, '%s')
        .replace(/@/g, '\\@');
}

export async function typeByAccessibilityId(
    accessibilityId: string,
    value: string,
) {
    const element = await $(`~${accessibilityId}`);

    await element.waitForDisplayed({ timeout: 10000 });
    await element.click();
    await browser.pause(300);

    await browser.execute('mobile: shell', {
        command: 'input',
        args: ['text', escapeAdbText(value)],
    });

    await browser.pause(300);
}