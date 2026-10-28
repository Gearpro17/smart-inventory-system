import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MovementType, Product, ProductService, StockMovement, apiErrorMessage } from '../../services/product.service';

@Component({ selector: 'app-movements', imports: [CommonModule, FormsModule], templateUrl: './movements.html', styleUrl: './movements.scss' })
export class Movements implements OnInit {
  private readonly service = inject(ProductService);
  products: Product[] = [];
  movements: StockMovement[] = [];
  productId = '';
  type: MovementType = 'IN';
  quantity: number | null = null;
  loading = true;
  saving = false;
  error = '';
  notice = '';

  ngOnInit(): void {
    this.service.listProducts().subscribe({ next: products => { this.products = products; this.productId = products[0]?.id ?? ''; this.loading = false; if (this.productId) this.loadMovements(); }, error: err => { this.error = apiErrorMessage(err); this.loading = false; } });
  }
  loadMovements(): void {
    if (!this.productId) { this.movements = []; return; }
    this.loading = true; this.error = '';
    this.service.listMovements(this.productId).subscribe({ next: rows => { this.movements = rows; this.loading = false; }, error: err => { this.error = apiErrorMessage(err); this.loading = false; } });
  }
  submit(): void {
    if (!this.productId || this.quantity === null || !Number.isInteger(this.quantity) || this.quantity <= 0) return;
    this.saving = true; this.error = ''; this.notice = '';
    this.service.registerMovement(this.productId, this.type, this.quantity).subscribe({
      next: () => { this.saving = false; this.notice = `${this.type === 'IN' ? 'Inbound' : 'Outbound'} movement recorded.`; this.quantity = null; this.loadMovements(); this.service.listProducts().subscribe({ next: products => this.products = products, error: err => this.error = apiErrorMessage(err) }); },
      error: err => { this.saving = false; this.error = apiErrorMessage(err); },
    });
  }
}
