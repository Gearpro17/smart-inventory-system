import { Component, inject, OnInit } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-side-menu',
  imports: [CommonModule, NgOptimizedImage, RouterLink, RouterLinkActive],
  templateUrl: './side-menu.html',
  styleUrl: './side-menu.scss',
})
export class SideMenu implements OnInit {
  protected readonly auth = inject(AuthService);
  ngOnInit(): void { this.auth.loadRoleAccess(); }
  DASHBOARD_IMG: string = "/dashboard.png";
  PRODUCTS_IMG: string = "/products.png";
  SUPPLIERS_IMG: string = "/suppliers.png";
  MOVEMENTS_IMG: string = "/movements.png";
  REORDER_IMG: string = "/reorder.png";
  SETTINGS_IMG: string = "/settings.png";
  ACCESS_IMG: string = "/access.png";
}
