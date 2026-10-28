import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ProductService, ReorderSuggestion, apiErrorMessage } from '../../services/product.service';

@Component({ selector: 'app-reorder', imports: [CommonModule], templateUrl: './reorder.html', styleUrl: './reorder.scss' })
export class Reorder implements OnInit {
  private readonly service = inject(ProductService);
  suggestions: ReorderSuggestion[] = [];
  loading = true;
  error = '';
  ngOnInit(): void { this.service.listReorderSuggestions().subscribe({ next: items => { this.suggestions = items; this.loading = false; }, error: err => { this.error = apiErrorMessage(err); this.loading = false; } }); }
}
