import { Client } from '../../client/model/client';
import { Game } from '../../game/model/game';

export interface Loan {
    id?: number;
    client: Client | null;
    game: Game | null;
    startDate: string;
    endDate: string;
}

export interface LoanFilters {
    gameId?: number | null;
    clientId?: number | null;
    activeDate?: string | null;
}

export interface LoanPage {
    content: Loan[];
    totalElements: number;
}

export interface LoanRequestPayload {
    gameId: number;
    clientId: number;
    startDate: string;
    endDate: string;
}


export interface LoanApiDto {
    id?: number;
    juego?: { id: number; title: string };
    cliente?: { id: number; name: string };
    fechaInicio?: string;
    fechaFin?: string;
}

export interface LoanPageApiResponse {
    content: LoanApiDto[];
    totalElements: number;
}
