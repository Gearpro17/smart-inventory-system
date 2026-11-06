import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { Products } from './components/products/products';
import { Suppliers } from './components/suppliers/suppliers';
import { Movements } from './components/movements/movements';
import { Reorder } from './components/reorder/reorder';
import { Login } from './components/login/login';
import { authGuard } from './guards/auth.guard';
import { AccessRoles } from './components/access-roles/access-roles';

export const routes: Routes = [
  { path: 'login', component: Login, title: 'Sign in' },
  { path: '', component: Dashboard, canActivate: [authGuard], data: { permission: 'INVENTORY_READ' }, title: 'Inventory overview' },
  { path: 'products', component: Products, canActivate: [authGuard], data: { permission: 'INVENTORY_READ' }, title: 'Products' },
  { path: 'products/:id', component: Products, canActivate: [authGuard], data: { permission: 'INVENTORY_READ' }, title: 'Product details' },
  { path: 'movements', component: Movements, canActivate: [authGuard], data: { permission: 'MOVEMENT_READ' }, title: 'Stock movements' },
  { path: 'suppliers', component: Suppliers, canActivate: [authGuard], data: { permission: 'SUPPLIER_READ' }, title: 'Suppliers' },
  { path: 'reorder', component: Reorder, canActivate: [authGuard], data: { permission: 'REORDER_READ' }, title: 'Reorder suggestions' },
  { path: 'access', component: AccessRoles, canActivate: [authGuard], data: { permission: 'USER_MANAGE' }, title: 'Access & roles' },
  { path: '**', redirectTo: '' },
];
