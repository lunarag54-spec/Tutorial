import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
    Loan,
    LoanApiDto,
    LoanFilters,
    LoanPage,
    LoanPageApiResponse,
    LoanRequestPayload,
} from './model/loan';

@Injectable({
    providedIn: 'root',
})
export class LoanService {
    private baseUrl = `${environment.apiUrl}/prestamos`;

    constructor(private http: HttpClient) {}

    getLoansPage(filters: LoanFilters, page: number, size: number): Observable<LoanPage> {
        let params = new HttpParams().set('page', page).set('size', size);

        if (filters.gameId != null) {
            params = params.set('idJuego', filters.gameId);
        }
        if (filters.clientId != null) {
            params = params.set('idCliente', filters.clientId);
        }
        if (filters.activeDate) {
            params = params.set('fechaBusqueda', filters.activeDate);
        }

        return this.http.get<LoanPageApiResponse>(this.baseUrl, { params }).pipe(
            map((response) => ({
                content: response.content.map((dto) => this.mapFromApi(dto)),
                totalElements: response.totalElements,
            }))
        );
    }

    saveLoan(loan: Loan): Observable<Loan> {
        const payload: LoanRequestPayload = {
            gameId: loan.game!.id!,
            clientId: loan.client!.id!,
            startDate: loan.startDate,
            endDate: loan.endDate,
        };

        const { id } = loan;
        const request$ = id
            ? this.http.put<LoanApiDto>(`${this.baseUrl}/${id}`, payload)
            : this.http.post<LoanApiDto>(this.baseUrl, payload);

        return request$.pipe(map((dto) => this.mapFromApi(dto)));
    }

    deleteLoan(idLoan: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${idLoan}`);
    }

    private mapFromApi(dto: LoanApiDto): Loan {
        return {
            id: dto.id,
            game: dto.juego
                ? ({
                      id: dto.juego.id,
                      title: dto.juego.title,
                  } as Loan['game'])
                : null,
            client: dto.cliente
                ? ({
                      id: dto.cliente.id,
                      name: dto.cliente.name,
                  } as Loan['client'])
                : null,
            startDate: dto.fechaInicio ?? '',
            endDate: dto.fechaFin ?? '',
        };
    }
}
