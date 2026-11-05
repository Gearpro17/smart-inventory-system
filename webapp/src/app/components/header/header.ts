import { Component, inject, signal } from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  protected readonly auth = inject(AuthService);
  protected readonly accountMenuOpen = signal(false);

  APP_LOGO: string = '/app_logo.png';

  protected openAccountMenu(): void { this.accountMenuOpen.set(true); }

  protected toggleAccountMenu(): void { this.accountMenuOpen.update((open) => !open); }

  protected closeAccountMenuOnMouseLeave(event: MouseEvent): void {
    const container = event.currentTarget as HTMLElement;
    if (!container.contains(document.activeElement)) this.accountMenuOpen.set(false);
  }

  protected closeAccountMenuOnFocusOut(event: FocusEvent): void {
    const container = event.currentTarget as HTMLElement;
    const nextTarget = event.relatedTarget;
    if (!container.contains(nextTarget as Node | null) && !container.matches(':hover')) {
      this.accountMenuOpen.set(false);
    }
  }

  protected closeAccountMenuOnEscape(event: Event): void {
    event.preventDefault();
    this.accountMenuOpen.set(false);
    (event.currentTarget as HTMLElement).querySelector<HTMLButtonElement>('.account-trigger')?.focus();
    this.accountMenuOpen.set(false);
  }
}
