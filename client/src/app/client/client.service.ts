import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Client } from './model/client';

@Injectable({
    providedIn: 'root',
})
export class ClientService {
    private baseUrl = `${environment.apiUrl}/client`;

    constructor(private http: HttpClient) {}

    getClients(): Observable<Client[]> {
        return this.http.get<Client[]>(this.baseUrl);
    }

    saveClient(client: Client): Observable<Client> {
        const { id } = client;
        return id
            ? this.http.put<Client>(`${this.baseUrl}/${id}`, client)
            : this.http.post<Client>(this.baseUrl, client);
    }

    deleteClient(idClient: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${idClient}`);
    }
}
