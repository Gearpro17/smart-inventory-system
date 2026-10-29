import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Product, ProductService, apiErrorMessage } from '../../services/product.service';

@Component({
  selector: 'app-products',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './products.html',
  styleUrl: './products.scss',
})
export class Products implements OnInit {
  private readonly service = inject(ProductService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  private readonly route = inject(ActivatedRoute);
  products: Product[] = [];
  selected: Product | null = null;
  loading = true;
  error = '';
  notice = '';
  showForm = false;
  saving = false;
  form = { id: '', name: '', currentStock: 0, minStock: 0 };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.loadProduct(id);
    else this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true; this.error = '';
    this.service.listProducts().subscribe({ next: items => { this.products = items; this.loading = false; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.loading = false; this.changeDetector.markForCheck(); } });
  }

  private loadProduct(id: string): void {
    this.loading = true; this.error = '';
    this.service.getProduct(id).subscribe({ next: product => { this.selected = product; this.loading = false; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.loading = false; this.changeDetector.markForCheck(); } });
  }

  createProduct(): void {
    if (!this.form.id.trim() || !this.form.name.trim() || !Number.isInteger(this.form.currentStock) || !Number.isInteger(this.form.minStock) || this.form.currentStock < 0 || this.form.minStock < 0) return;
    this.saving = true; this.error = ''; this.notice = '';
    this.service.createProduct({ ...this.form, id: this.form.id.trim(), name: this.form.name.trim() }).subscribe({
      next: () => { this.saving = false; this.showForm = false; this.notice = 'Product created.'; this.form = { id: '', name: '', currentStock: 0, minStock: 0 }; this.changeDetector.markForCheck(); this.loadProducts(); },
      error: err => { this.saving = false; this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); },
    });
  }
}
