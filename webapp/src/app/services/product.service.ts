import { HttpClient } from '@angular/common/http';
import { inject, Injectable, InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface Product {
  id: string;
  name: string;
  currentStock: number;
  minStock: number;
  lowStock: boolean;
  supplierId: string | null;
}

export type MovementType = 'IN' | 'OUT';

export interface StockMovement {
  id: number;
  productId: string;
  type: MovementType;
  quantity: number;
  timestamp: string;
}

export interface Supplier {
  id: string;
  name: string;
}

export interface ReorderSuggestion {
  productId: string;
  productName: string;
  currentStock: number;
  minStock: number;
  targetStock: number;
  suggestedQuantity: number;
}

export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => 'http://localhost:8080',
});

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);

  listProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/products`);
  }

  getProduct(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/products/${encodeURIComponent(id)}`);
  }

  createProduct(product: Pick<Product, 'id' | 'name' | 'currentStock' | 'minStock'>): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/products/create-product`, product);
  }

  listMovements(productId: string): Observable<StockMovement[]> {
    return this.http.get<StockMovement[]>(`${this.baseUrl}/products/${encodeURIComponent(productId)}/movements`);
  }

  registerMovement(productId: string, type: MovementType, quantity: number): Observable<StockMovement> {
    return this.http.post<StockMovement>(
      `${this.baseUrl}/products/${encodeURIComponent(productId)}/movements`,
      { type, quantity },
    );
  }

  listSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.baseUrl}/suppliers`);
  }

  createSupplier(supplier: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(`${this.baseUrl}/suppliers`, supplier);
  }

  updateSupplier(id: string, name: string): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.baseUrl}/suppliers/${encodeURIComponent(id)}`, { name });
  }

  assignSupplier(productId: string, supplierId: string): Observable<Product> {
    return this.http.put<Product>(
      `${this.baseUrl}/products/${encodeURIComponent(productId)}/supplier`,
      { supplierId },
    );
  }

  removeSupplier(productId: string): Observable<Product> {
    return this.http.delete<Product>(`${this.baseUrl}/products/${encodeURIComponent(productId)}/supplier`);
  }

  listLowStock(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/products/low-stock`);
  }

  listReorderSuggestions(): Observable<ReorderSuggestion[]> {
    return this.http.get<ReorderSuggestion[]>(`${this.baseUrl}/products/reorder-suggestions`);
  }
}

export function apiErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null) {
    const payload = error as { error?: { detail?: string; message?: string }; message?: string };
    return payload.error?.detail ?? payload.error?.message ?? payload.message ?? 'The request failed. Please try again.';
  }
  return 'The request failed. Please try again.';
}
