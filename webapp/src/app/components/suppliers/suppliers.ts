import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product, ProductService, Supplier, apiErrorMessage } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-suppliers',
  imports: [CommonModule, FormsModule],
  templateUrl: './suppliers.html',
  styleUrl: './suppliers.scss',
})
export class Suppliers implements OnInit {
  private readonly service = inject(ProductService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  protected readonly auth = inject(AuthService);
  suppliers: Supplier[] = [];
  products: Product[] = [];
  loading = true;
  saving = false;
  addSupplierExpanded = false;
  error = '';
  notice = '';
  form = { id: '', name: '', contactName: '', email: '', phone: '', address: '', leadTimeDays: null as number | null, taxNumber: '' };
  editingSupplier: Supplier | null = null;
  assignments: Record<string, string> = {};

  ngOnInit(): void { this.load(); }
  edit(supplier: Supplier): void { this.error = ''; this.editingSupplier = { ...supplier }; }
  load(): void {
    this.loading = true; this.error = '';
    this.service.listSuppliers().subscribe({ next: values => { this.suppliers = values; this.loading = false; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.loading = false; this.changeDetector.markForCheck(); } });
    this.service.listProducts().subscribe({ next: values => { this.products = values; for (const product of values) this.assignments[product.id] = product.supplierId ?? ''; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  create(): void {
    if (!this.form.id.trim() || !this.form.name.trim()) return;
    this.saving = true; this.error = ''; this.notice = '';
    this.service.createSupplier({ ...this.form, id: this.form.id.trim(), name: this.form.name.trim() }).subscribe({ next: () => { this.saving = false; this.form = { id: '', name: '', contactName: '', email: '', phone: '', address: '', leadTimeDays: null, taxNumber: '' }; this.notice = 'Supplier created.'; this.changeDetector.markForCheck(); this.load(); }, error: err => { this.saving = false; this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  saveEdit(): void {
    const editedSupplier = this.editingSupplier;
    if (!editedSupplier?.name.trim()) return;
    this.saving = true; this.error = '';
    this.service.updateSupplier(editedSupplier.id, editedSupplier).subscribe({ next: () => { this.saving = false; this.editingSupplier = null; this.notice = 'Supplier updated.'; this.changeDetector.markForCheck(); this.load(); }, error: err => { this.saving = false; this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  cancelEdit(): void { this.editingSupplier = null; }
  assign(product: Product): void {
    const supplierId = this.assignments[product.id];
    if (!supplierId) { this.remove(product); return; }
    this.error = ''; this.notice = '';
    this.service.assignSupplier(product.id, supplierId).subscribe({ next: updated => { this.products = this.products.map(item => item.id === updated.id ? updated : item); this.notice = `Supplier linked to ${product.name}.`; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
  remove(product: Product): void {
    this.error = ''; this.service.removeSupplier(product.id).subscribe({ next: updated => { this.products = this.products.map(item => item.id === updated.id ? updated : item); this.assignments[product.id] = ''; this.notice = `Supplier removed from ${product.name}.`; this.changeDetector.markForCheck(); }, error: err => { this.error = apiErrorMessage(err); this.changeDetector.markForCheck(); } });
  }
}
