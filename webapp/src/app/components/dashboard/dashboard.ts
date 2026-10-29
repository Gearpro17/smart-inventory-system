import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Product, ProductService, ReorderSuggestion, apiErrorMessage } from '../../services/product.service';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private readonly service = inject(ProductService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  products: Product[] = [];
  lowStock: Product[] = [];
  suggestions: ReorderSuggestion[] = [];
  loading = true;
  error = '';
  ngOnInit(): void {
    let completed = 0;
    const done = () => { completed++; if (completed === 3) this.loading = false; this.changeDetector.markForCheck(); };
    this.service.listProducts().subscribe({ next: values => { this.products = values; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
    this.service.listLowStock().subscribe({ next: values => { this.lowStock = values; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
    this.service.listReorderSuggestions().subscribe({ next: values => { this.suggestions = values; done(); }, error: err => { this.error = apiErrorMessage(err); done(); } });
  }
}
