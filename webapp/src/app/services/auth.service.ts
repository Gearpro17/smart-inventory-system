import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { API_BASE_URL } from './product.service';

export interface TokenResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  username: string;
  role: 'ADMIN' | 'INVENTORY_MANAGER' | 'VIEWER';
  expiresAt?: number;
}
export type Permission = 'INVENTORY_READ' | 'PRODUCT_CREATE' | 'MOVEMENT_READ' | 'MOVEMENT_WRITE' | 'SUPPLIER_READ' | 'SUPPLIER_MANAGE' | 'REORDER_READ' | 'USER_MANAGE';
export interface UserAccount { username: string; role: TokenResponse['role']; }
export interface RoleAccess { role: TokenResponse['role']; permissions: Permission[]; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = inject(API_BASE_URL);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly storageKey = 'stockflow.auth';
  private readonly session = signal<TokenResponse | null>(this.readSession());
  private readonly rolePermissions = signal<Record<string, Permission[]> | null>(null);

  get currentSession(): TokenResponse | null { return this.token ? this.session() : null; }
  get token(): string | null {
    const session = this.session();
    return session?.expiresAt && session.expiresAt > Date.now() ? session.accessToken : null;
  }
  get isAuthenticated(): boolean { return this.token !== null; }

  can(permission: Permission): boolean {
    const session = this.currentSession;
    if (!session) return false;
    const configured = this.rolePermissions();
    if (configured) return configured[session.role]?.includes(permission) ?? false;
    const defaults: Record<string, Permission[]> = {
      ADMIN: ['INVENTORY_READ','PRODUCT_CREATE','MOVEMENT_READ','MOVEMENT_WRITE','SUPPLIER_READ','SUPPLIER_MANAGE','REORDER_READ','USER_MANAGE'],
      INVENTORY_MANAGER: ['INVENTORY_READ','MOVEMENT_READ','MOVEMENT_WRITE','SUPPLIER_READ','SUPPLIER_MANAGE','REORDER_READ'],
      VIEWER: ['INVENTORY_READ','MOVEMENT_READ','SUPPLIER_READ','REORDER_READ'],
    };
    return defaults[session.role]?.includes(permission) ?? false;
  }
  loadRoleAccess(): void {
    this.http.get<RoleAccess[]>(`${this.baseUrl}/auth/roles`).subscribe({ next: roles => this.rolePermissions.set(Object.fromEntries(roles.map(role => [role.role, role.permissions]))) });
  }
  canAccess(permission: Permission): Observable<boolean> {
    return this.http.get<RoleAccess[]>(`${this.baseUrl}/auth/roles`).pipe(
      tap(roles => this.rolePermissions.set(Object.fromEntries(roles.map(role => [role.role, role.permissions])))),
      map(roles => roles.find(role => role.role === this.currentSession?.role)?.permissions.includes(permission) ?? false),
      catchError(() => of(false)),
    );
  }
  listUsers(): Observable<UserAccount[]> { return this.http.get<UserAccount[]>(`${this.baseUrl}/auth/users`); }
  createUser(username: string, password: string, role: UserAccount['role']): Observable<UserAccount> { return this.http.post<UserAccount>(`${this.baseUrl}/auth/users`, { username, password, role }); }
  deleteUser(username: string): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/auth/users/${encodeURIComponent(username)}`); }
  listRoles(): Observable<RoleAccess[]> { return this.http.get<RoleAccess[]>(`${this.baseUrl}/auth/roles`); }
  updateRole(role: UserAccount['role'], permissions: Permission[]): Observable<void> { return this.http.put<void>(`${this.baseUrl}/auth/roles/${role}`, { permissions }); }

  login(username: string, password: string): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.baseUrl}/auth/login`, { username, password }).pipe(
      tap((session) => {
        const persisted = { ...session, expiresAt: Date.now() + session.expiresIn * 1000 };
        if (isPlatformBrowser(this.platformId)) localStorage.setItem(this.storageKey, JSON.stringify(persisted));
        this.session.set(persisted);
        this.loadRoleAccess();
      }),
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem(this.storageKey);
    this.session.set(null);
    this.rolePermissions.set(null);
    void this.router.navigate(['/login']);
  }

  clearSession(): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem(this.storageKey);
    this.session.set(null);
    this.rolePermissions.set(null);
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
