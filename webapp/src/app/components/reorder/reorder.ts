import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Product, ProductService, ReorderSuggestion, SupplierOrder, apiErrorMessage } from '../../services/product.service';

@Component({ selector: 'app-reorder', imports: [CommonModule, FormsModule, RouterLink], templateUrl: './reorder.html', styleUrl: './reorder.scss' })
export class Reorder implements OnInit {
  private readonly service = inject(ProductService);
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly changeDetector = inject(ChangeDetectorRef);
  suggestions: ReorderSuggestion[] = [];
  products: Product[] = [];
  orders: SupplierOrder[] = [];
  stockFilter = '';
  appliedStockFilter = '';
  orderStatusFilter = '';
  orderSupplierFilter = '';
  orderDateFilter = '';
  appliedOrderStatus = '';
  appliedOrderSupplier = '';
  appliedOrderDate = '';
  loading = true;
  error = '';
  notice = '';
  orderingProductId = '';
  ngOnInit(): void {
    let completed = 0;
    const done = () => { completed++; if (completed === 3) this.loading = false; this.changeDetector.markForCheck(); };
    this.service.listProducts().subscribe({ next: items => { this.products = items; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
    this.service.listReorderSuggestions().subscribe({ next: items => { this.suggestions = items; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
    this.loadOrders(done);
  }
  private loadOrders(done?: () => void): void {
    this.service.listSupplierOrders().subscribe({ next: items => { this.orders = items; done?.(); this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); done?.(); this.changeDetector.markForCheck(); } });
  }
  get filteredProducts(): Product[] { return this.products.filter(p => !this.appliedStockFilter || p.stockStatus === this.appliedStockFilter); }
  hasSupplier(productId: string): boolean { return !!this.products.find(p => p.id === productId)?.supplierId; }
  get filteredOrders(): SupplierOrder[] { return this.orders.filter(o => (!this.appliedOrderStatus || o.status === this.appliedOrderStatus) && (!this.appliedOrderSupplier || o.supplierId === this.appliedOrderSupplier) && (!this.appliedOrderDate || o.orderDate.slice(0, 10) === this.appliedOrderDate)); }
  get orderSuppliers(): SupplierOrder[] { return this.orders.filter((order, index, all) => all.findIndex(item => item.supplierId === order.supplierId) === index); }
  applyOrderFilters(): void { this.appliedOrderStatus = this.orderStatusFilter; this.appliedOrderSupplier = this.orderSupplierFilter; this.appliedOrderDate = this.orderDateFilter; }
  clearOrderFilters(): void {
    this.orderStatusFilter = '';
    this.orderSupplierFilter = '';
    this.orderDateFilter = '';
    this.applyOrderFilters();
  }
  applyStockFilter(): void { this.appliedStockFilter = this.stockFilter; }
  placeOrder(item: ReorderSuggestion): void {
    this.orderingProductId = item.productId; this.error = ''; this.notice = '';
    this.service.createSupplierOrder(item.productId, item.suggestedQuantity).subscribe({ next: order => { this.orderingProductId = ''; this.notice = `Supplier order #${order.id} placed as Pending.`; this.orders = [order, ...this.orders]; this.changeDetector.markForCheck(); }, error: err => { this.orderingProductId = ''; this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  cancelOrder(order: SupplierOrder): void {
    this.service.cancelSupplierOrder(order.id).subscribe({ next: updated => { this.orders = this.orders.map(item => item.id === updated.id ? updated : item); this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  receiveOrder(order: SupplierOrder): void {
    this.router.navigate(['/movements'], { queryParams: { productId: order.productId, type: 'IN', quantity: order.quantity, supplierOrderId: order.id } });
  }
}
