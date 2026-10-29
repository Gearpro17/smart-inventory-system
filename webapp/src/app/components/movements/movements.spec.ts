import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { Movements } from './movements';

describe('Movements', () => {
  let fixture: ComponentFixture<Movements>;
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Movements],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap({ productId: 'tea', type: 'IN', quantity: '7' }),
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Movements);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    http.expectOne('http://localhost:8080/products').flush([
      { id: 'coffee', name: 'Coffee', currentStock: 20, minStock: 5, lowStock: false, supplierId: null, stockStatus: 'HEALTHY' },
      { id: 'tea', name: 'Tea', currentStock: 4, minStock: 5, lowStock: true, supplierId: null, stockStatus: 'LOW' },
    ]);
    http.expectOne('http://localhost:8080/movements').flush([]);
  });

  afterEach(() => http.verify());

  it('preselects the product, inbound type, and suggested quantity from the reorder link', () => {
    expect(fixture.componentInstance.productId).toBe('tea');
    expect(fixture.componentInstance.type).toBe('IN');
    expect(fixture.componentInstance.quantity).toBe(7);
    expect(fixture.componentInstance.productName('tea')).toBe('Tea');
  });

  it('filters the history independently and can return to all products', () => {
    const component = fixture.componentInstance;
    component.historyProductId = 'tea';
    component.loadMovements();
    http.expectOne('http://localhost:8080/products/tea/movements').flush([]);

    component.historyProductId = '';
    component.loadMovements();
    http.expectOne('http://localhost:8080/movements').flush([]);
  });
});
