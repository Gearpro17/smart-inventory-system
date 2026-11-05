import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { Products } from './components/products/products';
import { Suppliers } from './components/suppliers/suppliers';
import { Movements } from './components/movements/movements';
import { Reorder } from './components/reorder/reorder';
import { Login } from './components/login/login';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: Login, title: 'Sign in' },
  { path: '', component: Dashboard, canActivate: [authGuard], title: 'Inventory overview' },
  { path: 'products', component: Products, canActivate: [authGuard], title: 'Products' },
  { path: 'products/:id', component: Products, canActivate: [authGuard], title: 'Product details' },
  { path: 'movements', component: Movements, canActivate: [authGuard], title: 'Stock movements' },
  { path: 'suppliers', component: Suppliers, canActivate: [authGuard], title: 'Suppliers' },
  { path: 'reorder', component: Reorder, canActivate: [authGuard], title: 'Reorder suggestions' },
  { path: '**', redirectTo: '' },
];
