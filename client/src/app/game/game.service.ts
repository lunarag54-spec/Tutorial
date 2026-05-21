import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from './model/game';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root',
})
export class GameService {
    constructor(private http: HttpClient) {}

    private baseUrl = `${environment.apiUrl}/game`;

    getGames(title?: string, idCategory?: number): Observable<Game[]> {
        let params: any = {};
        if (title != null && title.trim() !== '') {
            params.title = title;
        }
        if (idCategory != null) {
            params.idCategory = idCategory.toString();
        }
        return this.http.get<Game[]>(this.baseUrl, { params });
    }

    saveGame(game: Game): Observable<Game> {
        const { id } = game;
        return id
            ? this.http.put<Game>(`${this.baseUrl}/${id}`, game)
            : this.http.post<Game>(this.baseUrl, game);
    }

    deleteGame(idGame: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${idGame}`);
    }
}
