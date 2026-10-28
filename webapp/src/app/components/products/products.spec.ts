import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Products } from './products';

describe('Products', () => {
  let component: Products;
  let fixture: ComponentFixture<Products>;
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Products],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Products);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    await fixture.whenStable();
    http.expectOne('http://localhost:8080/products').flush([]);
    await fixture.whenStable();
    fixture.detectChanges(false);
  });

  afterEach(() => http.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the empty state when the API has no products', () => {
    expect(component.loading).toBe(false);
    expect(component.products).toEqual([]);
  });

  it('renders product inventory and backend low-stock state', () => {
    component.products = [{ id: 'coffee', name: 'Coffee', currentStock: 2, minStock: 5, lowStock: true, supplierId: null }];
    expect(component.products[0].lowStock).toBe(true);
    expect(component.products[0].currentStock).toBe(2);
  });

  it('shows API errors clearly', () => {
    component.error = 'Inventory service unavailable';
    expect(component.error).toContain('Inventory service unavailable');
  });

  it('creates a valid product and refreshes the catalogue', () => {
    component.form = { id: 'coffee', name: 'Coffee', currentStock: 12, minStock: 4 };
    component.createProduct();
    const create = http.expectOne('http://localhost:8080/products/create-product');
    expect(create.request.method).toBe('POST');
    expect(create.request.body).toEqual(component.form);
    create.flush(null);
    http.expectOne('http://localhost:8080/products').flush([]);
    expect(component.notice).toBe('Product created.');
    expect(component.saving).toBe(false);
  });

  it('presents backend validation errors from product creation', () => {
    component.form = { id: 'coffee', name: 'Coffee', currentStock: 1, minStock: 3 };
    component.createProduct();
    http.expectOne('http://localhost:8080/products/create-product').flush({ detail: 'Product ID already exists' }, { status: 400, statusText: 'Bad Request' });
    expect(component.error).toBe('Product ID already exists');
  });
});
