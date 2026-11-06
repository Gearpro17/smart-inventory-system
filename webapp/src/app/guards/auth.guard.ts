import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Permission } from '../services/auth.service';
import { map } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.isAuthenticated) return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  const permission = route.data['permission'];
  if (typeof permission !== 'string') return true;
  return auth.canAccess(permission as Permission).pipe(
    map(allowed => allowed ? true : router.createUrlTree(['/'])),
  );
};
