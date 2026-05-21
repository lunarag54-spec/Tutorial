import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pageable } from '../../core/header/model/page/Pageable';
import { Author } from './author';
import { AuthorPage } from '../../core/header/model/page/AuthorPage';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root',
})
export class AuthorService {
    constructor(private http: HttpClient) {}

    private baseUrl = `${environment.apiUrl}/author`;

    getAuthors(pageable: Pageable): Observable<AuthorPage> {
        return this.http.post<AuthorPage>(this.baseUrl, { pageable: pageable });
    }

    saveAuthor(author: Author): Observable<Author> {
        const { id } = author;
        return id
            ? this.http.put<Author>(`${this.baseUrl}/${id}`, author)
            : this.http.post<Author>(this.baseUrl, author);
    }

    deleteAuthor(idAuthor: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${idAuthor}`);
    }

    getAllAuthors(): Observable<Author[]> {
        return this.http.get<Author[]>(`${this.baseUrl}/all`);
    }
}
