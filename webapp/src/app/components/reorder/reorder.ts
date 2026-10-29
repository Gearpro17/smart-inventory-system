import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Product, ProductService, ReorderSuggestion, apiErrorMessage } from '../../services/product.service';

@Component({ selector: 'app-reorder', imports: [CommonModule, RouterLink], templateUrl: './reorder.html', styleUrl: './reorder.scss' })
export class Reorder implements OnInit {
  private readonly service = inject(ProductService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  suggestions: ReorderSuggestion[] = [];
  products: Product[] = [];
  loading = true;
  error = '';
  ngOnInit(): void {
    let completed = 0;
    const done = () => { completed++; if (completed === 2) this.loading = false; this.changeDetector.markForCheck(); };
    this.service.listProducts().subscribe({ next: items => { this.products = items; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
    this.service.listReorderSuggestions().subscribe({ next: items => { this.suggestions = items; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
  }
}
