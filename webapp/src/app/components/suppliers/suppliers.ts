import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product, ProductService, Supplier, apiErrorMessage } from '../../services/product.service';

@Component({
  selector: 'app-suppliers',
  imports: [CommonModule, FormsModule],
  templateUrl: './suppliers.html',
  styleUrl: './suppliers.scss',
})
export class Suppliers implements OnInit {
  private readonly service = inject(ProductService);
  suppliers: Supplier[] = [];
  products: Product[] = [];
  loading = true;
  saving = false;
  error = '';
  notice = '';
  form = { id: '', name: '' };
  editingId = '';
  editingName = '';
  assignments: Record<string, string> = {};

  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true; this.error = '';
    this.service.listSuppliers().subscribe({ next: values => { this.suppliers = values; this.loading = false; }, error: err => { this.error = apiErrorMessage(err); this.loading = false; } });
    this.service.listProducts().subscribe({ next: values => { this.products = values; for (const product of values) this.assignments[product.id] = product.supplierId ?? ''; }, error: err => { this.error = apiErrorMessage(err); } });
  }
  create(): void {
    if (!this.form.id.trim() || !this.form.name.trim()) return;
    this.saving = true; this.error = ''; this.notice = '';
    this.service.createSupplier({ id: this.form.id.trim(), name: this.form.name.trim() }).subscribe({ next: () => { this.saving = false; this.form = { id: '', name: '' }; this.notice = 'Supplier created.'; this.load(); }, error: err => { this.saving = false; this.error = apiErrorMessage(err); } });
  }
  saveEdit(supplier: Supplier): void {
    if (!this.editingName.trim()) return;
    this.saving = true; this.error = '';
    this.service.updateSupplier(supplier.id, this.editingName.trim()).subscribe({ next: () => { this.saving = false; this.editingId = ''; this.notice = 'Supplier updated.'; this.load(); }, error: err => { this.saving = false; this.error = apiErrorMessage(err); } });
  }
  assign(product: Product): void {
    const supplierId = this.assignments[product.id];
    if (!supplierId) { this.remove(product); return; }
    this.error = ''; this.notice = '';
    this.service.assignSupplier(product.id, supplierId).subscribe({ next: updated => { this.products = this.products.map(item => item.id === updated.id ? updated : item); this.notice = `Supplier linked to ${product.name}.`; }, error: err => { this.error = apiErrorMessage(err); } });
  }
  remove(product: Product): void {
    this.error = ''; this.service.removeSupplier(product.id).subscribe({ next: updated => { this.products = this.products.map(item => item.id === updated.id ? updated : item); this.assignments[product.id] = ''; this.notice = `Supplier removed from ${product.name}.`; }, error: err => { this.error = apiErrorMessage(err); } });
  }
}
