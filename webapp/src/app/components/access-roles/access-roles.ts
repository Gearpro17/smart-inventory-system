import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, Permission, RoleAccess, UserAccount } from '../../services/auth.service';
import { apiErrorMessage } from '../../services/product.service';

@Component({
  selector: 'app-access-roles', imports: [CommonModule, FormsModule],
  templateUrl: './access-roles.html', styleUrl: './access-roles.scss',
})
export class AccessRoles implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  readonly permissionLabels: Record<Permission, string> = {
    INVENTORY_READ: 'View products and inventory', PRODUCT_CREATE: 'Create products',
    MOVEMENT_READ: 'View stock movements', MOVEMENT_WRITE: 'Record stock movements',
    SUPPLIER_READ: 'View suppliers', SUPPLIER_MANAGE: 'Manage suppliers',
    REORDER_READ: 'View reorder suggestions', USER_MANAGE: 'Manage users and roles',
  };
  get permissionKeys(): Permission[] { return Object.keys(this.permissionLabels) as Permission[]; }
  users: UserAccount[] = [];
  roles: RoleAccess[] = [];
  form = { username: '', password: '', role: 'VIEWER' as UserAccount['role'] };
  error = ''; notice = ''; saving = false;
  ngOnInit(): void { this.load(); }
  get currentUsername(): string { return this.auth.currentSession?.username ?? ''; }
  isCurrentUser(user: UserAccount): boolean { return user.username.toLowerCase() === this.currentUsername.toLowerCase(); }
  load(): void {
    this.auth.listUsers().subscribe({ next: users => { this.users = users; this.changeDetector.markForCheck(); }, error: err => this.fail(err) });
    this.auth.listRoles().subscribe({ next: roles => { this.roles = roles.filter(role => role.role !== 'ADMIN'); this.changeDetector.markForCheck(); }, error: err => this.fail(err) });
  }
  createUser(): void {
    this.saving = true; this.error = ''; this.notice = '';
    this.auth.createUser(this.form.username.trim(), this.form.password, this.form.role).subscribe({
      next: () => { this.saving = false; this.form = { username: '', password: '', role: 'VIEWER' }; this.notice = 'User created.'; this.load(); this.changeDetector.markForCheck(); },
      error: err => { this.saving = false; this.fail(err); },
    });
  }
  deleteUser(user: UserAccount): void {
    if (this.isCurrentUser(user)) return;
    if (!window.confirm(`Delete user “${user.username}”? This permanently removes the account. The user will lose access immediately.`)) return;
    this.auth.deleteUser(user.username).subscribe({ next: () => { this.notice = `${user.username} was deleted.`; this.load(); this.changeDetector.markForCheck(); }, error: err => this.fail(err) });
  }
  toggle(role: RoleAccess, permission: Permission, checked: boolean): void {
    role.permissions = checked ? [...new Set([...role.permissions, permission])] : role.permissions.filter(item => item !== permission);
  }
  saveRole(role: RoleAccess): void {
    this.saving = true; this.error = ''; this.notice = '';
    this.auth.updateRole(role.role, role.permissions).subscribe({ next: () => { this.saving = false; this.notice = `${role.role.replace('_', ' ')} permissions saved.`; this.auth.loadRoleAccess(); this.changeDetector.markForCheck(); }, error: err => { this.saving = false; this.fail(err); } });
  }
  private fail(error: unknown): void { this.error = apiErrorMessage(error); this.changeDetector.markForCheck(); }
}
