import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { Products } from './components/products/products';
import { Suppliers } from './components/suppliers/suppliers';
import { Movements } from './components/movements/movements';
import { Reorder } from './components/reorder/reorder';

export const routes: Routes = [
  { path: '', component: Dashboard, title: 'Inventory overview' },
  { path: 'products', component: Products, title: 'Products' },
  { path: 'products/:id', component: Products, title: 'Product details' },
  { path: 'movements', component: Movements, title: 'Stock movements' },
  { path: 'suppliers', component: Suppliers, title: 'Suppliers' },
  { path: 'reorder', component: Reorder, title: 'Reorder suggestions' },
  { path: '**', redirectTo: '' },
];
