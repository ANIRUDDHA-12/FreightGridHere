// lib/api.ts
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export async function fetchWithAuth(endpoint: string, options: RequestInit = {}, token?: string) {
    const headers = {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
    };

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }

    return response.json();
}