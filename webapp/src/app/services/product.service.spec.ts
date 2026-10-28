import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { API_BASE_URL, ProductService, apiErrorMessage } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), { provide: API_BASE_URL, useValue: 'http://inventory.test' }] });
    service = TestBed.inject(ProductService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('loads products from the configured API', () => {
    let result: unknown;
    service.listProducts().subscribe(value => result = value);
    const request = http.expectOne('http://inventory.test/products');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 'tea', name: 'Tea', currentStock: 4, minStock: 8, lowStock: true, supplierId: null }]);
    expect(result).toEqual([{ id: 'tea', name: 'Tea', currentStock: 4, minStock: 8, lowStock: true, supplierId: null }]);
  });

  it('posts stock movement input without client-side inventory calculations', () => {
    service.registerMovement('tea/large', 'OUT', 2).subscribe();
    const request = http.expectOne('http://inventory.test/products/tea%2Flarge/movements');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ type: 'OUT', quantity: 2 });
    request.flush({ id: 1, productId: 'tea/large', type: 'OUT', quantity: 2, timestamp: '2026-10-28T12:00:00' });
  });

  it('loads backend-calculated reorder suggestions', () => {
    service.listReorderSuggestions().subscribe();
    const request = http.expectOne('http://inventory.test/products/reorder-suggestions');
    expect(request.request.method).toBe('GET');
    request.flush([{ productId: 'tea', productName: 'Tea', currentStock: 4, minStock: 8, targetStock: 8, suggestedQuantity: 4 }]);
  });

  it('supports supplier directory and product supplier association endpoints', () => {
    service.listSuppliers().subscribe();
    http.expectOne('http://inventory.test/suppliers').flush([{ id: 'acme', name: 'Acme' }]);

    service.createSupplier({ id: 'north', name: 'North Supply' }).subscribe();
    const create = http.expectOne('http://inventory.test/suppliers');
    expect(create.request.method).toBe('POST');
    create.flush({ id: 'north', name: 'North Supply' });

    service.updateSupplier('north', 'North Wholesale').subscribe();
    const update = http.expectOne('http://inventory.test/suppliers/north');
    expect(update.request.method).toBe('PUT');
    expect(update.request.body).toEqual({ name: 'North Wholesale' });
    update.flush({ id: 'north', name: 'North Wholesale' });

    service.assignSupplier('tea', 'north').subscribe();
    const assign = http.expectOne('http://inventory.test/products/tea/supplier');
    expect(assign.request.method).toBe('PUT');
    expect(assign.request.body).toEqual({ supplierId: 'north' });
    assign.flush({ id: 'tea', name: 'Tea', currentStock: 4, minStock: 8, lowStock: true, supplierId: 'north' });

    service.removeSupplier('tea').subscribe();
    const remove = http.expectOne('http://inventory.test/products/tea/supplier');
    expect(remove.request.method).toBe('DELETE');
    remove.flush({ id: 'tea', name: 'Tea', currentStock: 4, minStock: 8, lowStock: true, supplierId: null });
  });

  it('loads low-stock products and extracts backend problem details', () => {
    service.listLowStock().subscribe();
    const request = http.expectOne('http://inventory.test/products/low-stock');
    expect(request.request.method).toBe('GET');
    request.flush([], { status: 200, statusText: 'OK' });
    expect(apiErrorMessage({ error: { detail: 'Quantity must be positive' } })).toBe('Quantity must be positive');
  });
});
