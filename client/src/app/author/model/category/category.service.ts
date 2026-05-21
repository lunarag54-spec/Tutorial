import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Category } from './model/category';
import { environment } from '../../../../environments/environment';

@Injectable({
providedIn: 'root'
})
export class CategoryService {
    constructor(private http: HttpClient) {}

    private baseUrl = `${environment.apiUrl}/category`;

    getCategories(): Observable<Category[]> {
        return this.http.get<Category[]>(this.baseUrl);
    }

    saveCategory(category: Category): Observable<Category> {
        const { id } = category;
        return id
            ? this.http.put<Category>(`${this.baseUrl}/${id}`, category)
            : this.http.post<Category>(this.baseUrl, category);
    }

    deleteCategory(idCategory : number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${idCategory}`);
    }  
}
