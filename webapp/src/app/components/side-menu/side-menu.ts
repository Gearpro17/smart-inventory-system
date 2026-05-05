import { Component } from '@angular/core';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-side-menu',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './side-menu.html',
  styleUrl: './side-menu.scss',
})
export class SideMenu {
  DASHBOARD_IMG: string = "/dashboard.png";
  PRODUCTS_IMG: string = "/products.png";
  SUPPLIERS_IMG: string = "/suppliers.png";
  MOVEMENTS_IMG: string = "/movements.png";
  REORDER_IMG: string = "/reorder.png";
  SETTINGS_IMG: string = "/settings.png";
  ACCESS_IMG: string = "/access.png";
}
