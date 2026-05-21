import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', redirectTo: '/games', pathMatch: 'full'},
    { path: 'authors', loadComponent: () => import('./author/author-list/author-list.component').then(m => m.AuthorListComponent)},
    { path: 'categories', loadComponent: () => import('./author/model/category/category-list/category-list.component').then(m => m.CategoryListComponent)},
    { path: 'clients', loadComponent: () => import('./client/client-list/client-list.component').then(m => m.ClientListComponent)},
    { path: 'games', loadComponent: () => import('./game/game-list/game-list.component').then(m => m.GameListComponent)},
    { path: 'loans', loadComponent: () => import('./loan/loan-list/loan-list.component').then(m => m.LoanListComponent)}
];
