import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { apiErrorMessage } from '../../services/product.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected username = '';
  protected password = '';
  protected readonly error = signal('');
  protected readonly submitting = signal(false);

  protected submit(): void {
    if (this.submitting() || !this.username.trim() || !this.password) return;
    this.error.set('');
    this.submitting.set(true);
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        const requested = this.route.snapshot.queryParamMap.get('returnUrl');
        const destination = requested?.startsWith('/') && !requested.startsWith('//') ? requested : '/';
        void this.router.navigateByUrl(destination);
      },
      error: (failure: unknown) => {
        this.error.set(apiErrorMessage(failure));
        this.submitting.set(false);
      },
    });
  }
}
