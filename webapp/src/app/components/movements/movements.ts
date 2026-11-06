import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MovementType, Product, ProductService, StockMovement, apiErrorMessage } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-movements', imports: [CommonModule, FormsModule], templateUrl: './movements.html', styleUrl: './movements.scss' })
export class Movements implements OnInit {
  private readonly service = inject(ProductService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  private readonly route = inject(ActivatedRoute);
  protected readonly auth = inject(AuthService);
  products: Product[] = [];
  movements: StockMovement[] = [];
  productId = '';
  historyProductId = '';
  type: MovementType = 'IN';
  quantity: number | null = null;
  loading = true;
  saving = false;
  error = '';
  notice = '';

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    const requestedProductId = params.get('productId');
    const requestedType = params.get('type');
    const requestedQuantity = Number(params.get('quantity'));
    if (requestedType === 'IN' || requestedType === 'OUT') this.type = requestedType;
    if (Number.isInteger(requestedQuantity) && requestedQuantity > 0) this.quantity = requestedQuantity;

    this.service.listProducts().subscribe({
      next: products => {
        this.products = products;
        this.productId = products.find(product => product.id === requestedProductId)?.id ?? products[0]?.id ?? '';
        this.loading = false;
        this.changeDetector.markForCheck();
        this.loadMovements();
      },
      error: err => { this.error = apiErrorMessage(err); this.loading = false; this.changeDetector.markForCheck(); },
    });
  }
  loadMovements(): void {
    this.loading = true; this.error = '';
    const request = this.historyProductId
      ? this.service.listMovements(this.historyProductId)
      : this.service.listAllMovements();
    request.subscribe({ next: rows => { this.movements = rows; this.loading = false; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.loading = false; this.changeDetector.markForCheck(); } });
  }

  productName(productId: string): string {
    return this.products.find(product => product.id === productId)?.name ?? productId;
  }
  submit(): void {
    if (!this.productId || this.quantity === null || !Number.isInteger(this.quantity) || this.quantity <= 0) return;
    this.saving = true; this.error = ''; this.notice = '';
    this.service.registerMovement(this.productId, this.type, this.quantity).subscribe({
      next: () => { this.saving = false; this.notice = `${this.type === 'IN' ? 'Inbound' : 'Outbound'} movement recorded.`; this.quantity = null; this.changeDetector.markForCheck(); this.loadMovements(); this.service.listProducts().subscribe({ next: products => { this.products = products; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } }); },
      error: err => { this.saving = false; this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); },
    });
  }
}
