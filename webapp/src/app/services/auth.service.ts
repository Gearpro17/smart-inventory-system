import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from './product.service';

export interface TokenResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  username: string;
  role: 'ADMIN' | 'INVENTORY_MANAGER' | 'VIEWER';
  expiresAt?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = inject(API_BASE_URL);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly storageKey = 'stockflow.auth';
  private readonly session = signal<TokenResponse | null>(this.readSession());

  get currentSession(): TokenResponse | null { return this.token ? this.session() : null; }
  get token(): string | null {
    const session = this.session();
    return session?.expiresAt && session.expiresAt > Date.now() ? session.accessToken : null;
  }
  get isAuthenticated(): boolean { return this.token !== null; }

  login(username: string, password: string): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.baseUrl}/auth/login`, { username, password }).pipe(
      tap((session) => {
        const persisted = { ...session, expiresAt: Date.now() + session.expiresIn * 1000 };
        if (isPlatformBrowser(this.platformId)) localStorage.setItem(this.storageKey, JSON.stringify(persisted));
        this.session.set(persisted);
      }),
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem(this.storageKey);
    this.session.set(null);
    void this.router.navigate(['/login']);
  }

  clearSession(): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem(this.storageKey);
    this.session.set(null);
  }

  private readSession(): TokenResponse | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    try {
      const stored = localStorage.getItem(this.storageKey);
      if (!stored) return null;
      const session = JSON.parse(stored) as TokenResponse;
      return session.accessToken && session.expiresAt && session.expiresAt > Date.now() ? session : null;
    } catch {
      return null;
    }
  }
}
