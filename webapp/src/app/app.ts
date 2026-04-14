import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Header} from './components/header/header';
import {SideMenu} from './components/side-menu/side-menu';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, SideMenu],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('webapp');
}
