const API_BASE_URL = process.env.API_BASE_URL ?? 'http://localhost:3000';

export async function createTestUser() {
    const email = `appium${Date.now()}@aseca.com`;
    const password = 'Password123';

    const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        const body = await response.text();
        throw new Error(`Could not create test user: ${response.status} ${body}`);
    }

    return { email, password };
}